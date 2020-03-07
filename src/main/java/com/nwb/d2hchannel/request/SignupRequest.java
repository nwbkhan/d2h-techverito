package com.nwb.d2hchannel.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SignupRequest {

    @NotEmpty
    String firstName;
    @NotEmpty
    String lastName;
    @NotEmpty
    String phoneNo;
    @NotEmpty
    String email;
    @NotEmpty
    String password;
}
