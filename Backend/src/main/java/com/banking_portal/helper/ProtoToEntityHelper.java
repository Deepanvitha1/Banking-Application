
package com.banking_portal.helper;

import com.banking_portal.constants.dto.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class ProtoToEntityHelper {

    private final PasswordEncoder passwordEncoder;

    public ProtoToEntityHelper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public UserEntity convertToEntity(SignUpRequestDTO signUpRequestDTO) {
        String password = passwordEncoder.encode(signUpRequestDTO.getPassword());
        return UserEntity.newBuilder()
                .setFirstName(signUpRequestDTO.getFirstName())
                .setLastName(signUpRequestDTO.getLastName())
                .setEmail(signUpRequestDTO.getEmail())
                .setPhoneNumber(signUpRequestDTO.getPhoneNumber())
                .setAadhar(signUpRequestDTO.getAadhar())
                .setAddress(signUpRequestDTO.getAddress())
                .setDob(signUpRequestDTO.getDob())
                .setPassword(password)
                .setRePasswordBytes(signUpRequestDTO.getRePasswordBytes())
                .build();
    }

    public AccountEntity convertToAccountEntity(CreateAccountRequest createAccountRequest) {
        return AccountEntity.newBuilder()
                .setUid(createAccountRequest.getUid())
                .setType(createAccountRequest.getTypeOfAccount())
                .setBalance(Double.parseDouble("100"))
                .build();

    }
    public
    PaymentEntity convertToPaymentEntity(PaymentRequestDTO paymentRequestDTO) {
        return PaymentEntity.newBuilder()
                .setUid(paymentRequestDTO.getUid())
                .setSenderAccountId(paymentRequestDTO.getSenderAccountId())
                .setReceiverAccountId(paymentRequestDTO.getReceiverAccountId())
                .setTransactionalNote(paymentRequestDTO.getTransactionalNote())
                .setAmount(paymentRequestDTO.getAmount())
                .build();

    }
    public TransactionEntity convertToTransactionEntity(PaymentRequestDTO paymentRequestDTO) {
        return TransactionEntity.newBuilder()
                .setSenderAccountId(paymentRequestDTO.getSenderAccountId())
                .setReceiverAccountId(paymentRequestDTO.getReceiverAccountId())
                .setTransactionalNote(paymentRequestDTO.getTransactionalNote())
                .setAmount(paymentRequestDTO.getAmount())
                .build();
    }
}