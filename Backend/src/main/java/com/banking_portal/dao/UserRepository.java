package com.banking_portal.dao;

import com.banking_portal.constants.dto.UserEntity;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findByPhoneNumber(String username);

    Optional<UserEntity> createUser(UserEntity user);

    Optional<UserEntity> findByUserId(String userId);
}

