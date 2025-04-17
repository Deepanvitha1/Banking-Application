package com.banking_portal.dao;

import com.banking_portal.constants.dto.AccountEntity;
import java.util.List;
import java.util.Optional;

public interface AccountsRepository {

    List<AccountEntity> getAccounts(String userId);

    Optional<AccountEntity> createAccount(AccountEntity accountEntity);

    Optional<String> findLatestAccountId();

}
