package com.banking_portal.services;

import com.banking_portal.constants.dto.HistoryList;
import com.banking_portal.constants.dto.PaymentRequestDTO;
import com.banking_portal.constants.dto.PaymentResponseDTO;

public interface IPaymentService {
    PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequestDTO);

    HistoryList getTransactionHistory(String uid, String account_type, int page, int size);
}
