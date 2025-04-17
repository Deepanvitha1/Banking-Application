package com.banking_portal.dao.impl;

import com.banking_portal.constants.DbConstants;
import com.banking_portal.constants.SQLQueryConstants;
import com.banking_portal.exception.CustomSQLException;
import com.banking_portal.exception.UserAlreadyExistsException;
import com.banking_portal.constants.dto.UserEntity;
import com.banking_portal.dao.UserRepository;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class UserDAOImpl implements UserRepository
{

    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());
    private final DataSource dataSource;
    public UserDAOImpl(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<UserEntity> findByPhoneNumber(String phoneNumber)
    {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            String query = SQLQueryConstants.FIND_BY_PHONE_NUMBER;
            statement = connection.prepareStatement(query);
            statement.setString(1, phoneNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(UserEntity.newBuilder()
                            .setPhoneNumber(resultSet.getString(DbConstants.USER_COL_MOBILE_NUMBER))
                            .setPassword(resultSet.getString(DbConstants.USER_COL_PASSWORD))
                            .setUid(resultSet.getString(DbConstants.USER_COL_USER_ID))
                            .setFirstName(resultSet.getString(DbConstants.USER_COL_FIRST_NAME))
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new CustomSQLException("Error fetching user by phone number", e);
        }
        return Optional.empty();

    }

    @Override
    public Optional<UserEntity> createUser(UserEntity userEntity) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            if (findByPhoneNumber(userEntity.getPhoneNumber()).isPresent()) {
                throw new UserAlreadyExistsException("User with phone number " + userEntity.getPhoneNumber() + " already exists.");
            }
            LOGGER.info("saving");
            String insertQuery = SQLQueryConstants.CREATE_USER;

            try {
                statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, userEntity.getFirstName());
                statement.setString(2, userEntity.getLastName());
                statement.setString(3, userEntity.getEmail());
                statement.setString(4, userEntity.getPhoneNumber());
                statement.setString(5, userEntity.getAadhar());
                statement.setString(6, userEntity.getAddress());
                statement.setString(7, userEntity.getDob());
                statement.setString(8, userEntity.getPassword());
                statement.setString(9, userEntity.getRePassword());
                LOGGER.info("saving2");
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        boolean l = generatedKeys.next();
                        LOGGER.info(String.valueOf(l));
                        if (l) {
                            String generatedId = String.valueOf(generatedKeys.getLong(1)); // Convert ID to String
                            userEntity = userEntity.toBuilder().setUid(generatedId).build();
                        }
                    }
                    connection.commit();
                    return Optional.of(userEntity); // Return the saved UserEntity
                }
            } catch (SQLException e) {
                connection.rollback();
                throw new CustomSQLException("Error while saving user because %s".formatted(e.getMessage()), e);
            }
        } catch (SQLException e) {
            throw new CustomSQLException("Error at creating user",e);
        } finally {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserEntity> findByUserId(String userId) {
        String query = SQLQueryConstants.FIND_USER_BY_USER_ID;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, Long.parseLong(userId));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(UserEntity.newBuilder()
                            .setUid(resultSet.getString(DbConstants.USER_COL_USER_ID))
                            .setFirstName(resultSet.getString(DbConstants.USER_COL_FIRST_NAME))
                            .setLastName(resultSet.getString(DbConstants.USER_COL_LAST_NAME))
                            .setEmail(resultSet.getString(DbConstants.USER_COL_EMAIL))
                            .setPhoneNumber(resultSet.getString(DbConstants.USER_COL_MOBILE_NUMBER))
                            .setAadhar(resultSet.getString(DbConstants.USER_COL_AADHAR_NUMBER))
                            .setAddress(resultSet.getString(DbConstants.USER_COL_ADDRESS))
                            .setDob(resultSet.getString(DbConstants.USER_COL_DOB))
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new CustomSQLException("Error fetching user details for userId: " + userId, e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid userId format: " + userId, e);
        }
        return Optional.empty();
    }
}

