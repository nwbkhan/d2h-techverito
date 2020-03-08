package com.nwb.d2hchannel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.nwb.d2hchannel.exception.D2Exception;

public enum SubscriptionType {
    channel, pack, service;

    @JsonCreator
    public static SubscriptionType forValue(String value) {
        for (SubscriptionType subscriptionType : SubscriptionType.values()) {
            if (value.equalsIgnoreCase(subscriptionType.name())) {
                return subscriptionType;
            }
        }
        throw new D2Exception("unable to resolve " + value);
    }
}
