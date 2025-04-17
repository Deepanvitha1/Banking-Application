package com.banking_portal.services;

import com.banking_portal.constants.dto.*;

public interface IUserService {

    SignUpResponseDTO createUser(SignUpRequestDTO signUpRequestDTO);

    LoginResponseDTO verify(LoginRequestDTO loginRequestDTO);

    SignUpResponseDTO fetchUserDetails(String userId);

    GetAccountResponse getAccount(String uid);
}
