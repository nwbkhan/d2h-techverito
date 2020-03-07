package com.nwb.d2hchannel.services;

import com.nwb.d2hchannel.SubscriptionType;
import com.nwb.d2hchannel.response.UserChannelSubscriptionResponse;
import com.nwb.d2hchannel.response.UserServiceSubscriptionResponse;
import com.nwb.d2hchannel.response.UserSubscriptionPackResponse;
import com.nwb.d2hchannel.persistence.User;
import com.nwb.d2hchannel.request.SubscriptionPackRequest;

import java.util.List;

public interface ISubscriptionPack {
    Object getSubscriptionFor(User userByToken, SubscriptionType subscriptionType);

    UserSubscriptionPackResponse subscribePacks(User userByToken,
                                                SubscriptionPackRequest subscriptionPackRequest);

    UserChannelSubscriptionResponse subscribeForChannels(User userByToken,
                                                         List<String> channelNames);

    UserServiceSubscriptionResponse subscribeForService(User userByToken,
                                                        String serviceName);
}
