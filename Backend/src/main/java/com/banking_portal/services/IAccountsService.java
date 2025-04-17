package com.banking_portal.services;

import com.banking_portal.constants.dto.*;

public interface IAccountsService
{
    CreateAccountResponse createAccount(CreateAccountRequest request);

    GetAccountResponse getAccount(String uid);

}
