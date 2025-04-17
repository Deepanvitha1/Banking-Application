package com.banking_portal.controller;

import com.banking_portal.constants.URLConstants;
import com.banking_portal.constants.dto.*;
import com.banking_portal.constants.api.ApiResponse;
import com.banking_portal.constants.dto.GetAccountResponse;
import com.banking_portal.exception.CommonStatusCode;
import com.banking_portal.services.IAccountsService;
import com.banking_portal.utils.ResponseUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;



@CrossOrigin
@RestController
@RequestMapping(URLConstants.PATH_ACCOUNT)
public class AccountController
{
    private static final Logger LOGGER = LogManager.getLogger(AccountController.class);
    private final IAccountsService accountService;

    public AccountController(IAccountsService accountService)
    {
        this.accountService = accountService;
    }

    @PostMapping("/create")
    public ApiResponse createAccount(@RequestBody CreateAccountRequest createAccountRequest)
    {
        LOGGER.info("Create Account request received for UID: {}", createAccountRequest.getUid());

        CreateAccountResponse createAccountResponse = accountService.createAccount(createAccountRequest);
        return ResponseUtils.buildResponse(CommonStatusCode.SUCCESS, createAccountResponse);

    }

}

