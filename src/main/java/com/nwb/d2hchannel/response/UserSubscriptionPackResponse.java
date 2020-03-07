package com.nwb.d2hchannel.response;

import lombok.Data;

@Data
public class UserSubscriptionPackResponse {
    long totalPrice;
    long totalDiscount;
    long finalPrice;
    long userCurrentBalance;
}
