package com.nwb.d2hchannel.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    @NotEmpty
    String phoneNo;

    @NotEmpty
    String email;
}
