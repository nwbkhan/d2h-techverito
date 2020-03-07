package com.nwb.d2hchannel.api;


import com.nwb.d2hchannel.*;
import com.nwb.d2hchannel.persistence.User;
import com.nwb.d2hchannel.repository.UserRepository;
import com.nwb.d2hchannel.request.SubscriptionPackRequest;
import com.nwb.d2hchannel.response.UserChannelSubscriptionResponse;
import com.nwb.d2hchannel.response.UserCurrentSubscriptions;
import com.nwb.d2hchannel.response.UserServiceSubscriptionResponse;
import com.nwb.d2hchannel.response.UserSubscriptionPackResponse;
import com.nwb.d2hchannel.services.SubscriptionService;
import com.nwb.d2hchannel.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nwb.d2hchannel.PathConstants.*;

@RestController
@RequestMapping(PathConstants.SUBSCRIPTION_PATH)
public class SubscriptionApplciationServiceController {


    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final SubscriptionService subscriptionService;

    public SubscriptionApplciationServiceController(UserRepository userRepository,
                                                    TokenService tokenService,
                                                    SubscriptionService subscriptionService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping(SUBSCRIPTION_TYPE_PATH)
    public ResponseEntity<Object> getSubscriptions(@RequestHeader("token") String token,
                                                   @PathVariable SubscriptionType subscriptionType) {
        final User userByToken = tokenService.getUserByToken(token);

        Object subscription = subscriptionService.getSubscriptionFor(userByToken, subscriptionType);

        return ResponseEntity.ok(subscription);
    }


    @PostMapping(PACK_PATH)
    public ResponseEntity<UserSubscriptionPackResponse> subscribePacks(@RequestHeader("token") String token,
                                                                       @Validated @RequestBody SubscriptionPackRequest subscriptionPackRequest) {
        final User userByToken = tokenService.getUserByToken(token);

        final UserSubscriptionPackResponse userChannelSubscriptionPack =
                subscriptionService.subscribePacks(userByToken, subscriptionPackRequest);

        return ResponseEntity.ok(userChannelSubscriptionPack);
    }


    @PostMapping(CHANNEL_PATH)
    public ResponseEntity<UserChannelSubscriptionResponse> subscribeForChannels(@RequestHeader("token") String token,
                                                                                @RequestParam List<String> channelNames) {
        final User userByToken = tokenService.getUserByToken(token);

        final UserChannelSubscriptionResponse userChannelSubscriptionPack =
                subscriptionService.subscribeForChannels(userByToken, channelNames);

        return ResponseEntity.ok(userChannelSubscriptionPack);
    }

    @PostMapping(SERVICE_PATH)
    public ResponseEntity<UserServiceSubscriptionResponse> subscribeForServices(@RequestHeader("token") String token,
                                                                                @RequestParam String serviceName) {
        final User userByToken = tokenService.getUserByToken(token);

        final UserServiceSubscriptionResponse userChannelSubscriptionPack =
                subscriptionService.subscribeForService(userByToken, serviceName);

        return ResponseEntity.ok(userChannelSubscriptionPack);
    }

    @GetMapping(CURRENT_PATH)
    public ResponseEntity<UserCurrentSubscriptions> getCurrentUserSubscription(@RequestHeader("token") String token) {
        final User userByToken = tokenService.getUserByToken(token);

        final UserCurrentSubscriptions userChannelSubscriptionPack =
                subscriptionService.getCurrentUserSubsSubscriptions(userByToken);

        return ResponseEntity.ok(userChannelSubscriptionPack);
    }
}
