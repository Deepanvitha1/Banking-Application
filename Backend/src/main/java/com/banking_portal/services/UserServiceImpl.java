package com.banking_portal.services;

import com.banking_portal.constants.dto.*;
import com.banking_portal.exception.InvalidCredentialsException;
import com.banking_portal.exception.UserNotFoundException;
import com.banking_portal.facade.UserFacade;

import com.banking_portal.helper.EntityToProtoHelper;
import com.banking_portal.helper.ProtoToEntityHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);
    private final ProtoToEntityHelper protoToEntityHelper;
    private final EntityToProtoHelper entityToProtoHelper;
    private final UserFacade userFacade;
    private final AccountServiceImpl accountService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authManager;;

    public UserServiceImpl(ProtoToEntityHelper protoToEntityHelper, UserFacade userFacade, EntityToProtoHelper entityToProtoHelper, AccountServiceImpl accountService) {
        this.protoToEntityHelper = protoToEntityHelper;
        this.userFacade = userFacade;
        this.accountService= accountService;
        this.entityToProtoHelper = entityToProtoHelper;
    }

    @Override
    public SignUpResponseDTO createUser(SignUpRequestDTO signUpRequestDTO) {
        LOGGER.info("SignUp service called");
        UserEntity userEntity = protoToEntityHelper.convertToEntity(signUpRequestDTO);
        UserEntity savedUserEntity = userFacade.createUser(userEntity).orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (savedUserEntity == null) {
            LOGGER.error("SignUp failed: Could not save user.");
            throw new RuntimeException("User registration failed");
        }

        LOGGER.info("SignUp service completed successfully");
        return entityToProtoHelper.convertToProto(savedUserEntity);
    }

    @Override
    public LoginResponseDTO verify(LoginRequestDTO loginRequestDTO)
    {
        LOGGER.info("Login service called");
        Authentication authentication;
        try {
            authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getPhoneNumber(), loginRequestDTO.getPassword()
                    )
            );
        } catch (Exception e) {
            LOGGER.error("Authentication failed: {}", e.getMessage());
            throw new InvalidCredentialsException("Invalid credentials"); // Or return a meaningful error response
        }
        if (authentication.isAuthenticated()) {
            UserEntity userEntity = userFacade.findByPhoneNumber(loginRequestDTO.getPhoneNumber()).orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
            String token = jwtService.generateAccessToken(userEntity.getUid(), loginRequestDTO.getPhoneNumber());
            // Convert using helper function
            LoginResponseDTO.Builder loginResponseBuilder = entityToProtoHelper.convertToLoginResponseDTO(userEntity).toBuilder();
            // Set the JWT token
            loginResponseBuilder.setJwtToken(token);
            return loginResponseBuilder.build();
        }
        throw new InvalidCredentialsException("Invalid credentials");
    }

    @Override
    public SignUpResponseDTO fetchUserDetails(String userId) {
        LOGGER.info("Details service called");
        UserEntity userEntity= userFacade.fetchUserDetails(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        SignUpResponseDTO signUpResponseDTO = entityToProtoHelper.convertToProto(userEntity);
        return signUpResponseDTO;
    }

    @Override
    public GetAccountResponse getAccount(String uid){
        return accountService.getAccount(uid);
    }
}

