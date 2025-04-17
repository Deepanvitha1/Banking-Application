
package com.banking_portal.controller;

import com.banking_portal.constants.URLConstants;
import com.banking_portal.constants.api.ApiResponse;
import com.banking_portal.constants.dto.*;
import com.banking_portal.exception.CommonStatusCode;
import com.banking_portal.services.IPaymentService;
import com.banking_portal.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(URLConstants.PATH_TRANSACTION)
public class PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);
    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/send-money")
    public ApiResponse transferFunds(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        LOGGER.info("Send money request");
        PaymentResponseDTO paymentResponseDTO = paymentService.processPayment(paymentRequestDTO);
        return ResponseUtils.buildResponse(CommonStatusCode.SUCCESS, paymentResponseDTO);
    }

    @GetMapping("/history")
    public ApiResponse transactionHistory(@RequestParam("uid") String uid,
                                          @RequestParam("account_type") String account_type,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        LOGGER.info("Fetching transaction history for user: {}", uid);
        HistoryList transactionHistory=paymentService.getTransactionHistory(uid,account_type,page,size);
        return ResponseUtils.buildResponse(CommonStatusCode.SUCCESS, transactionHistory);
    }
}

