package com.banking_portal.services;

import com.banking_portal.constants.dto.*;
import com.banking_portal.exception.AccountNotFoundException;
import com.banking_portal.exception.InsufficientFundsException;
import com.banking_portal.facade.AccountFacade;
import com.banking_portal.facade.PaymentFacade;
import com.banking_portal.helper.EntityToProtoHelper;
import com.banking_portal.helper.ProtoToEntityHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements IPaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentFacade paymentFacade;
    private final AccountFacade accountFacade;
    private final ProtoToEntityHelper protoToEntityHelper;
    private final EntityToProtoHelper entityToProtoHelper;

    public PaymentServiceImpl(PaymentFacade paymentFacade, ProtoToEntityHelper protoToEntityHelper, EntityToProtoHelper entityToProtoHelper, AccountFacade accountFacade) {
        this.paymentFacade = paymentFacade;
        this.accountFacade = accountFacade;
        this.protoToEntityHelper = protoToEntityHelper;
        this.entityToProtoHelper = entityToProtoHelper;
    }
    @Override
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequestDTO) {
        LOGGER.info("Service1");


        if (!paymentFacade.accountExists(paymentRequestDTO.getReceiverAccountId())) {
            throw new AccountNotFoundException("Receiver account does not exist");
        }

        double senderBalance = paymentFacade.getBalance(paymentRequestDTO.getSenderAccountId());
        if (senderBalance < paymentRequestDTO.getAmount()) {
            throw new InsufficientFundsException("Sender account balance is not enough");
        }

        PaymentEntity paymentEntity= protoToEntityHelper.convertToPaymentEntity(paymentRequestDTO);
        PaymentEntity savedPaymentEntity =paymentFacade.executePayment(paymentEntity).orElseThrow(() -> new InsufficientFundsException("Insufficient funds"));
        TransactionEntity transactionEntity=protoToEntityHelper.convertToTransactionEntity(paymentRequestDTO);
        TransactionEntity savedTransactionEntity=paymentFacade.insertTransaction(transactionEntity).orElseThrow(()->new InsufficientFundsException("Transaction failed"));
        return entityToProtoHelper.convertToPaymentResponseDTO(savedTransactionEntity);
    }



    public HistoryList getTransactionHistory(String uid, String account_type, int page, int size) {
        LOGGER.info("Get Transaction History Request Called");

        List<AccountEntity> accountEntities = accountFacade.getAccounts(uid);
        String account_id = accountEntities.stream()
                .filter(account -> account.getType().equalsIgnoreCase(account_type))
                .map(AccountEntity::getAccountId)
                .findFirst()
                .orElse(null);

        List<TransactionEntity> transactionEntities = paymentFacade.getTransactionHistory(account_id,page,size);
        int totalTransactions=paymentFacade.getTotalTransactionCount(account_id);
        return HistoryList.newBuilder()
                .addAllItems(transactionEntities)
                .setLoggedInUserAccountNumber(account_id)
                .setTotalPages((int) Math.ceil((double) totalTransactions / size))
                .setTotalElements(totalTransactions)
                .build();
    }

}
