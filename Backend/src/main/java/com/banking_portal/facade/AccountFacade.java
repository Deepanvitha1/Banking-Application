package com.banking_portal.facade;

import com.banking_portal.dao.AccountsRepository;
import com.banking_portal.constants.dto.AccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AccountFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountFacade.class);

    private final AccountsRepository accountsRepository;

    public AccountFacade(AccountsRepository accountsRepository)
    {
        this.accountsRepository = accountsRepository;
    }

    public Optional<String> findLatestAccountIdF()
    {
        LOGGER.info("Fetching latest account ID");
        return accountsRepository.findLatestAccountId();
    }

    public List<AccountEntity> getAccounts(String userId) {
        LOGGER.info("Getting accounts");
        return accountsRepository.getAccounts(userId);
    }

    public Optional<AccountEntity> createAccount(AccountEntity accountEntity) {
        LOGGER.info("Creating account for user: {} ", accountEntity.getUid());
        return accountsRepository.createAccount(accountEntity);

    }
}

