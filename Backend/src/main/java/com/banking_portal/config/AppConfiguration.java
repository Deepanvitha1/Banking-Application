package com.banking_portal.config;

import com.banking_portal.constants.dto.*;
import com.google.protobuf.util.JsonFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

@Configuration
public class AppConfiguration {

    @Bean
    public JsonFormat.TypeRegistry typeRegistry() {
        return JsonFormat.TypeRegistry.newBuilder()
                .add(SignUpRequestDTO.getDescriptor())
                .add(LoginRequestDTO.getDescriptor())
                .add(LoginResponseDTO.getDescriptor())
                .add(SignUpResponseDTO.getDescriptor())
                .add(CreateAccountRequest.getDescriptor())
                .add(CreateAccountResponse.getDescriptor())
                .add(GetAccountResponse.getDescriptor())
                .add(PaymentRequestDTO.getDescriptor())
                .add(PaymentResponseDTO.getDescriptor())
                .add(Account.getDescriptor())
                .add(HistoryList.getDescriptor())
                .build();
    }

    @Bean
    public ProtobufJsonFormatHttpMessageConverter protobufJsonFormatHttpMessageConverter(JsonFormat.TypeRegistry typeRegistry) {
        JsonFormat.Printer printer = JsonFormat.printer()
                .usingTypeRegistry(typeRegistry)
                .preservingProtoFieldNames()
                .includingDefaultValueFields()
                .omittingInsignificantWhitespace();

        JsonFormat.Parser parser = JsonFormat.parser()
                .usingTypeRegistry(typeRegistry)
                .ignoringUnknownFields();

        return new ProtobufJsonFormatHttpMessageConverter(parser, printer);
    }

}
