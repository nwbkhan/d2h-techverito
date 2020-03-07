package com.nwb.d2hchannel.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequest {
    @NotEmpty
    String username;
    @NotEmpty
    String password;
}
