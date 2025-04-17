package com.banking_portal.controller;

import com.banking_portal.constants.URLConstants;
import com.banking_portal.constants.api.ApiResponse;
import com.banking_portal.constants.dto.*;
import com.banking_portal.exception.CommonStatusCode;
import com.banking_portal.services.IUserService;
import com.banking_portal.services.JwtService;
import com.banking_portal.utils.ResponseUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(URLConstants.PATH_USER)
public class UserController {

    private static final Logger LOGGER = LogManager.getLogger(UserController.class);
    private final IUserService userService;


    public UserController(IUserService userService, JwtService jwtService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ApiResponse signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        LOGGER.info("SignUp endpoint called.");
        SignUpResponseDTO signUpResponseDTO = userService.createUser(signUpRequestDTO);
        return ResponseUtils.buildResponse(CommonStatusCode.SUCCESS, signUpResponseDTO);
    }

    @PostMapping("/log-in")
    public ApiResponse logIn(@RequestBody LoginRequestDTO loginRequestDTO) {
        LOGGER.info("Login endpoint called.");
        LoginResponseDTO loginResponseDTO = userService.verify(loginRequestDTO);
        return ResponseUtils.buildResponse(CommonStatusCode.SUCCESS, loginResponseDTO);
    }

    @GetMapping("/{user-id}")
    public ApiResponse getUserDetails(@PathVariable(name="user-id") String userId) {
        LOGGER.info("Get details endpoint called.");
        SignUpResponseDTO userData = userService.fetchUserDetails(userId);
        return ResponseUtils.buildResponse(CommonStatusCode.SUCCESS, userData);
    }

    @GetMapping("/get-accounts/{user-id}")
    public ApiResponse getAccounts(@PathVariable(name="user-id") String uid) {
        LOGGER.info("Get accounts endpoint called.");
        GetAccountResponse getAccountResponse = userService.getAccount(uid);
        return ResponseUtils.buildResponse(CommonStatusCode.SUCCESS, getAccountResponse);
    }
}







