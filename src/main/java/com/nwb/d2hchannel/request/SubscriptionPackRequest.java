package com.nwb.d2hchannel.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class SubscriptionPackRequest {
    @NotEmpty
    String packName;
    @Min(value = 1)
    int months;
}
