package com.nwb.d2hchannel.response;

import com.nwb.d2hchannel.persistence.UserChannelSubscription;
import com.nwb.d2hchannel.persistence.UserChannelSubscriptionPack;
import com.nwb.d2hchannel.persistence.UserServiceSubscription;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserCurrentSubscriptions {
    List<UserChannelSubscriptionPack> channelSubscriptionPacks;
    List<UserChannelSubscription> channelSubscriptions;
    List<UserServiceSubscription> serviceSubscriptions;
}
