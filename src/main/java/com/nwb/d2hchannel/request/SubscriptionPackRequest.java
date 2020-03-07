package com.nwb.d2hchannel.request;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class SubscriptionPackRequest {
    String packName;
    @Min(value = 1)
    int months;
}
