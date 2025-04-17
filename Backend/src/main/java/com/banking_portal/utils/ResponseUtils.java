package com.banking_portal.utils;
import com.banking_portal.api.ApiStatusCode;
import com.banking_portal.constants.api.ApiResponse;
import com.banking_portal.constants.api.ApiResponseHeader;
import com.google.protobuf.Any;
import com.google.protobuf.Message;

public class ResponseUtils {
    public static ApiResponse buildResponse(ApiStatusCode headers, Message data) {
        ApiResponse.Builder builder = ApiResponse.newBuilder()
                .setHeaders(buildApiResponseHeader(headers));
        if (data != null) {
            builder.setData(Any.pack(data));
        }
        return builder.build();
    }

    public static ApiResponseHeader buildApiResponseHeader(ApiStatusCode headers) {
        return ApiResponseHeader.newBuilder()
                .setStatusCode(headers.getStatusCode())
                .setMessage(headers.getMessage())
                .build();
    }


}
