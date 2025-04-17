package com.banking_portal.dao;

import com.banking_portal.constants.dto.PaymentEntity;
import com.banking_portal.constants.dto.TransactionEntity;

import java.util.List;
import java.util.Optional;


public interface PaymentRepository {
    Optional<PaymentEntity> transferFunds(PaymentEntity paymentEntity);

    List<TransactionEntity> getTransactionHistory(String accountId, int page, int size);

    Optional<TransactionEntity> insertTransaction(TransactionEntity transactionEntity);

    int getTotalTransactionCount(String accountId);

    boolean accountExists(String uid);

    double getBalance(String accountId);
}
