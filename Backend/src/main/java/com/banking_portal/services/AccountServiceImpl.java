package com.banking_portal.services;

import com.banking_portal.constants.dto.*;

import com.banking_portal.exception.IllegalArgumentsException;
import com.banking_portal.facade.AccountFacade;
import com.banking_portal.helper.EntityToProtoHelper;
import com.banking_portal.helper.ProtoToEntityHelper;
import com.banking_portal.constants.dto.AccountEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements IAccountsService {

    private static final Logger LOGGER = LogManager.getLogger(AccountServiceImpl.class);

    private final AccountFacade accountFacade;
    private final ProtoToEntityHelper protoToEntityHelper;
    private final EntityToProtoHelper entityToProtoHelper;

    public AccountServiceImpl(AccountFacade accountFacade, ProtoToEntityHelper protoToEntityHelper, EntityToProtoHelper entityToProtoHelper) {
        this.accountFacade = accountFacade;
        this.protoToEntityHelper = protoToEntityHelper;
        this.entityToProtoHelper = entityToProtoHelper;
    }

    private String generateNextAccountId() {
        String latestAccountId = accountFacade.findLatestAccountIdF().orElseThrow(() -> new IllegalArgumentsException("Invalid account ID format in database"));

        if (latestAccountId == null || latestAccountId.isEmpty()) {
            return "AC1000";
        }

        int nextId;
        try {
            nextId = Integer.parseInt(latestAccountId.replace("AC", "")) + 1;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid account ID format in database: " + latestAccountId, e);
        }

        return "AC" + nextId;
    }

    @Override
    public CreateAccountResponse createAccount(CreateAccountRequest createAccountRequest) {
        LOGGER.info("Create account request called");

        String uid = createAccountRequest.getUid();
        String requestedAccountType = createAccountRequest.getTypeOfAccount().trim().toLowerCase();
        LOGGER.info("Requested account type:{} ", createAccountRequest.getTypeOfAccount());
        LOGGER.info("Requested account uid:{} ", uid);

        if (!requestedAccountType.equals("savings") && !requestedAccountType.equals("current")) {
            throw new IllegalArgumentsException("Invalid account type. Allowed values: Savings, Current.");
        }

        GetAccountResponse existingAccounts = getAccount(uid);
        boolean hasSavings = existingAccounts.getAccountsList().stream()
                .anyMatch(account -> account.getType().equalsIgnoreCase("savings"));
        boolean hasCurrent = existingAccounts.getAccountsList().stream()
                .anyMatch(account -> account.getType().equalsIgnoreCase("current"));

        if (hasSavings && hasCurrent) {
            throw new IllegalArgumentsException("You already have both Savings and Current accounts.");
        }

        if ((hasSavings && requestedAccountType.equals("savings")) ||
                (hasCurrent && requestedAccountType.equals("current"))) {
            throw new IllegalArgumentsException("You already have a " + requestedAccountType + " account.");
        }

        String accountId = generateNextAccountId();
        AccountEntity accountEntity = protoToEntityHelper.convertToAccountEntity(createAccountRequest);
        accountEntity = accountEntity.toBuilder().setAccountId(accountId).build();
        AccountEntity savedAccountEntity = accountFacade.createAccount(accountEntity).orElseThrow(() -> new IllegalArgumentsException("Account creation failed."));
        return entityToProtoHelper.convertToCreateAccountResponse(savedAccountEntity);
    }

    @Override
    public GetAccountResponse getAccount(String uid) {
        LOGGER.info("Fetching accounts endpoint called");
        List<AccountEntity> accountEntities = accountFacade.getAccounts(uid);
        return GetAccountResponse.newBuilder()
                .addAllAccounts(accountEntities)
                .build();
    }

}
