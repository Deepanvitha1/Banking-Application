package com.banking_portal.helper;
import com.banking_portal.constants.dto.*;
import org.springframework.stereotype.Component;

@Component
public class EntityToProtoHelper {
    public SignUpResponseDTO convertToProto(UserEntity userEntity) {
        return SignUpResponseDTO.newBuilder()
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setEmail(userEntity.getEmail())
                .setPhoneNumber(userEntity.getPhoneNumber())
                .setAadhar(userEntity.getAadhar())
                .setAddress(userEntity.getAddress())
                .setDob(userEntity.getDob())
                .setPassword(userEntity.getPassword())
                .setRePassword(userEntity.getRePassword())
                .build();
    }
    public LoginResponseDTO convertToLoginResponseDTO(UserEntity userEntity) {
        return LoginResponseDTO.newBuilder()
                .setUid(userEntity.getUid())
                .setPhoneNumber(userEntity.getPhoneNumber())
                .setName(userEntity.getFirstName())
                .build();
    }

    public CreateAccountResponse convertToCreateAccountResponse(AccountEntity savedAccountEntity) {
        return CreateAccountResponse.newBuilder()
                .setUid(savedAccountEntity.getUid())
                .setAccountId(savedAccountEntity.getAccountId())
                .setTypeOfAccount(savedAccountEntity.getType())
                .setBalance(Double.parseDouble("100"))
                .build();
    }

    public PaymentResponseDTO convertToPaymentResponseDTO(TransactionEntity savedPaymentEntity) {
        return PaymentResponseDTO.newBuilder()
                .setTransactionId(String.valueOf(savedPaymentEntity.getTransactionId()))
                .setSenderAccountId(savedPaymentEntity.getSenderAccountId())
                .setReceiverAccountId(savedPaymentEntity.getReceiverAccountId())
                .setTransactionalNote(savedPaymentEntity.getTransactionalNote())
                .setAmount(savedPaymentEntity.getAmount())
                .build();
    }
}









