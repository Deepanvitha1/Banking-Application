package com.banking_portal.dao.impl;

import com.banking_portal.constants.DbConstants;
import com.banking_portal.constants.SQLQueryConstants;
import com.banking_portal.dao.AccountsRepository;
import com.banking_portal.constants.dto.AccountEntity;
import com.banking_portal.exception.AccountNotFoundException;
import com.banking_portal.exception.CustomSQLException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountsDAOImpl implements AccountsRepository {

    private final DataSource dataSource;

    public AccountsDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<String> findLatestAccountId() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLQueryConstants.FIND_LATEST_ACCOUNT_ID);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return Optional.of(resultSet.getString(DbConstants.ACCOUNT_COL_ACCOUNT_ID));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new AccountNotFoundException("Error while fetching the latest account ID", e);
        }
    }

    @Override
    public List<AccountEntity> getAccounts(String userId)
    {
        List<AccountEntity> accounts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLQueryConstants.GET_ACCOUNTS_BY_USER_ID)) {

            statement.setString(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    AccountEntity account = mapResultSetToAccountEntity(resultSet);
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            throw new CustomSQLException("Error while retrieving accounts for user ID: " + userId, e);
        }
        return accounts;
    }

    @Override
    public Optional<AccountEntity> createAccount(AccountEntity accountEntity) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

             statement = connection.prepareStatement(SQLQueryConstants.CREATE_ACCOUNT);
                statement.setString(1, accountEntity.getAccountId());
                statement.setString(2, accountEntity.getUid());
                statement.setDouble(3, accountEntity.getBalance());
                statement.setString(4, accountEntity.getType());

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    connection.commit();
                    return Optional.of(accountEntity);
                }
            } catch (SQLException e) {
                throw new CustomSQLException("Error while executing insert query for account: " + accountEntity.getAccountId(), e);
            }finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (connection != null) try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return Optional.empty();

    }

    private AccountEntity mapResultSetToAccountEntity(ResultSet resultSet) throws SQLException {
        return AccountEntity.newBuilder()
                .setAccountId(resultSet.getString(DbConstants.ACCOUNT_COL_ACCOUNT_ID))
                .setUid(resultSet.getString(DbConstants.ACCOUNT_COL_USER_ID))
                .setBalance(resultSet.getDouble(DbConstants.ACCOUNT_COL_BALANCE))
                .setType(resultSet.getString(DbConstants.ACCOUNT_COL_TYPE))
                .build();
    }
}

