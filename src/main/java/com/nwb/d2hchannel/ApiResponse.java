package com.nwb.d2hchannel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    Object data;

    public static ApiResponse of(Object data) {
        return ApiResponse
                .builder()
                .data(data)
                .build();
    }
}
