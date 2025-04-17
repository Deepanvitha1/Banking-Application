package com.banking_portal.dao.impl;

import com.banking_portal.constants.DbConstants;
import com.banking_portal.constants.SQLQueryConstants;
import com.banking_portal.constants.dto.PaymentEntity;
import com.banking_portal.constants.dto.TransactionEntity;
import com.banking_portal.dao.PaymentRepository;
import com.banking_portal.exception.CustomSQLException;
import com.banking_portal.exception.DatabaseErrorException;
import com.banking_portal.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentDAOImpl implements PaymentRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDAOImpl.class);
    private final DataSource dataSource;

    public PaymentDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<PaymentEntity> transferFunds(PaymentEntity paymentEntity) {
        LOGGER.info("dao1");
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            System.out.println("Receiver Account ID: [" + paymentEntity.getReceiverAccountId()+ "]");
            try (PreparedStatement deductStmt = connection.prepareStatement(SQLQueryConstants.UPDATE_SENDER_BALANCE)) {
                deductStmt.setDouble(1, paymentEntity.getAmount());
                deductStmt.setString(2, paymentEntity.getSenderAccountId());
                if (deductStmt.executeUpdate() == 0) {
                    connection.rollback();
                    throw new DatabaseErrorException("Failed to deduct amount from sender account");
                }
            }

            try (PreparedStatement addStmt = connection.prepareStatement(SQLQueryConstants.UPDATE_RECEIVER_BALANCE)) {
                addStmt.setDouble(1, paymentEntity.getAmount());
                addStmt.setString(2, paymentEntity.getReceiverAccountId());
                if (addStmt.executeUpdate() == 0) {
                    connection.rollback();
                    throw new DatabaseErrorException("Failed to add amount to receiver account");
                }
            }
            connection.commit();
            return Optional.of(paymentEntity);
        } catch (SQLException e) {
            throw new CustomSQLException("Database error:", e);
        }

    }


    @Override
    public Optional<TransactionEntity> insertTransaction(TransactionEntity transactionEntity) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(SQLQueryConstants.INSERT_TRANSACTION, Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, transactionEntity.getSenderAccountId());
                statement.setString(2, transactionEntity.getReceiverAccountId());
                statement.setString(3, transactionEntity.getTransactionalNote());
                statement.setDouble(4, transactionEntity.getAmount());

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            long generatedId = generatedKeys.getLong(1);
                            transactionEntity = transactionEntity.toBuilder()
                                    .setTransactionId(generatedId)
                                    .build();

                            connection.commit();
                            LOGGER.info("Transaction successfully saved with ID: {}", generatedId);
                            return Optional.of(transactionEntity);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Database error while inserting transaction: {}", e.getMessage(), e);
            throw new CustomSQLException("Failed to insert transaction", e);
        }
        return Optional.empty();
    }


    @Override
    public int getTotalTransactionCount(String accountNumber) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLQueryConstants.GET_TRANSACTION_COUNT)) {

            statement.setString(1, accountNumber);
            statement.setString(2, accountNumber);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Fetch total count
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching transaction count for account {}: {}", accountNumber, e.getMessage(), e);
            throw new CustomSQLException("Error: ",e);
        }
        return 0;
    }


    @Override
    public boolean accountExists(String accountId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            System.out.println("Checking existence for Account ID: [" + accountId + "]");
            try (PreparedStatement stmt = connection.prepareStatement(SQLQueryConstants.CHECK_ACCOUNT_EXISTS)) {
                stmt.setString(1, accountId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        return count > 0;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            throw new CustomSQLException("Error: ",e);
        }
    }

    @Override
    public double getBalance(String accountId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(SQLQueryConstants.GET_ACCOUNT_BALANCE)) {
                LOGGER.info("Getting Balance for Account ID: [" + accountId + "]");
                stmt.setString(1, accountId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble(1);
                    }
                }
            }
            throw new UserNotFoundException("Account ID " + accountId + " not found");
        } catch (SQLException e) {
            throw new CustomSQLException("Error: ",e);
        }
    }

    @Override
    public List<TransactionEntity> getTransactionHistory(String accountNumber, int page, int size) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(SQLQueryConstants.GET_TRANSACTION_HISTORY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, accountNumber);
            statement.setString(2, accountNumber);
            statement.setInt(3, size);
            statement.setInt(4, page * size);
            ResultSet rs = statement.executeQuery();
            List<TransactionEntity> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(
                        TransactionEntity.newBuilder()
                                .setTransactionalNote(rs.getString(DbConstants.TRANSACTION_COL_TRANSACTIONAL_NOTE))
                                .setSenderAccountId(rs.getString(DbConstants.TRANSACTION_COL_SENDER_ACCOUNT_ID))
                                .setReceiverAccountId(rs.getString(DbConstants.TRANSACTION_COL_RECEIVER_ACCOUNT_ID))
                                .setAmount(rs.getBigDecimal(DbConstants.TRANSACTION_COL_AMOUNT).doubleValue())
                                .setTransactionId(Long.parseLong(String.valueOf(rs.getLong(DbConstants.TRANSACTION_COL_TRANSACTION_ID))))
                                .build()
                );
            }
            return transactions;
        } catch (SQLException e) {
            throw new CustomSQLException("Error: ",e);
        }

    }
}

