package com.nwb.d2hchannel.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserChannelSubscriptionResponse {
    long totalPrice;
    long userCurrentBalance;
}
