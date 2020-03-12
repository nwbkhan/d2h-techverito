package com.nwb.d2hchannel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
