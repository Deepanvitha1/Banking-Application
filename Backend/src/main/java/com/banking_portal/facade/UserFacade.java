package com.banking_portal.facade;

import com.banking_portal.constants.dto.UserEntity;
import com.banking_portal.dao.UserRepository;
import com.banking_portal.dao.impl.UserDAOImpl;
import com.banking_portal.exception.CustomSQLException;
import com.banking_portal.exception.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserFacade.class);

    private final UserRepository userRepository;
    private final UserDAOImpl userDAOImpl;

    public UserFacade(UserRepository userRepository, UserDAOImpl userDAOImpl) {
        this.userRepository = userRepository;
        this.userDAOImpl = userDAOImpl;
    }

    public Optional<UserEntity> createUser(UserEntity userEntity)
    {
        LOGGER.info("Creating User: {}",userEntity.getPhoneNumber());
        return userRepository.createUser(userEntity);
    }

    public Optional<UserEntity> findByPhoneNumber(String phone_number) {

        LOGGER.info("Finding User: {}",phone_number);
        return userRepository.findByPhoneNumber(phone_number);
    }

    public Optional<UserEntity> fetchUserDetails(String userId) {
        LOGGER.info("Fetching User Details: {}", userId);
        return userDAOImpl.findByUserId(userId);
    }

}
