package com.banking_portal.facade;

import com.banking_portal.constants.dto.PaymentEntity;
import com.banking_portal.constants.dto.TransactionEntity;
import com.banking_portal.dao.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentFacade.class);
    private final PaymentRepository paymentRepository;

    public PaymentFacade(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Optional<PaymentEntity> executePayment(PaymentEntity paymentEntity) {
        LOGGER.info("Executing payment in Facade");
        return paymentRepository.transferFunds(paymentEntity);
    }

    public Optional<TransactionEntity> insertTransaction(TransactionEntity transactionEntity) {
        LOGGER.info("Saving transaction for payment");
        return paymentRepository.insertTransaction(transactionEntity);
    }

    public List<TransactionEntity> getTransactionHistory(String accountId, int page, int size) {
        LOGGER.info("Getting transaction history");
        return paymentRepository.getTransactionHistory(accountId,page,size);
    }

    public int getTotalTransactionCount(String accountId) {
        return paymentRepository.getTotalTransactionCount(accountId);
    }

    public boolean accountExists(String uid) {
        return paymentRepository.accountExists(uid);
    }

    public double getBalance(String accountId) {
        return paymentRepository.getBalance(accountId);
    }


}
