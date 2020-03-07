package com.nwb.d2hchannel.services;

import com.nwb.d2hchannel.*;
import com.nwb.d2hchannel.exception.D2Exception;
import com.nwb.d2hchannel.persistence.*;
import com.nwb.d2hchannel.repository.*;
import com.nwb.d2hchannel.request.SubscriptionPackRequest;
import com.nwb.d2hchannel.response.UserChannelSubscriptionResponse;
import com.nwb.d2hchannel.response.UserCurrentSubscriptions;
import com.nwb.d2hchannel.response.UserServiceSubscriptionResponse;
import com.nwb.d2hchannel.response.UserSubscriptionPackResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SubscriptionService implements ISubscriptionPack {

    public static final String PLEASE_RECHARGE_YOU_DON_T_HAVE_SUFFICIENT_BALANCE = "Please recharge, you don't have sufficient balance";
    private final SubscriptionPackRepository subscriptionPackRepository;
    private final ChannelRepository channelSubscriptionRepositoy;
    private final ServiceSubscriptionRepository serviceSubscriptionRepository;

    private final UserChannelSubscriptionPackRepository userChannelSubscriptionPackRepository;
    private final UserServiceSubscriptionRepository userServiceSubscriptionRepository;
    private final UserChannelSubscriptionRepository userChannelRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionPackRepository subscriptionPackRepository,
                               ChannelRepository channelSubscriptionRepositoy,
                               ServiceSubscriptionRepository serviceSubscriptionRepository,
                               UserChannelSubscriptionPackRepository userChannelSubscriptionPackRepository,
                               UserServiceSubscriptionRepository userServiceSubscriptionRepository,
                               UserChannelSubscriptionRepository userChannelRepository,
                               UserRepository userRepository) {
        this.subscriptionPackRepository = subscriptionPackRepository;
        this.channelSubscriptionRepositoy = channelSubscriptionRepositoy;
        this.serviceSubscriptionRepository = serviceSubscriptionRepository;
        this.userChannelSubscriptionPackRepository = userChannelSubscriptionPackRepository;
        this.userServiceSubscriptionRepository = userServiceSubscriptionRepository;
        this.userChannelRepository = userChannelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Object getSubscriptionFor(User userByToken, SubscriptionType subscriptionType) {
        switch (subscriptionType) {
            case CHANNEL:
                return channelSubscriptionRepositoy.findAll();
            case PACK:
                return subscriptionPackRepository.findAll();
            case SERVICE:
                return serviceSubscriptionRepository.findAll();
            default:
                throw new D2Exception("subscription type not found for - " + subscriptionType.name());
        }
    }

    @Override
    public UserSubscriptionPackResponse subscribePacks(User userByToken,
                                                       SubscriptionPackRequest subscriptionPackRequest) {
        // check user already has not subscribed for subscription
        final List<UserChannelSubscriptionPack> existedPack =
                userChannelSubscriptionPackRepository
                        .findByUserIdAndChannelPackPackNameOrderByIdDesc(userByToken.getId(),
                                subscriptionPackRequest.getPackName(),
                                new Date());

        if (existedPack.size() > 0) {
            final UserChannelSubscriptionPack existedUserChannelSubscriptionPack;
            existedUserChannelSubscriptionPack = existedPack.get(0);
            throw new D2Exception(
                    "User has subscribed to - "
                            + existedUserChannelSubscriptionPack.getChannelPack().getPackName()
                            + ", valid up to "
                            + existedUserChannelSubscriptionPack.getExpiryDate());

        } else {
            // is new connection, add it
            return subscribeForChannelPack(userByToken, subscriptionPackRequest);
        }
    }

    private UserSubscriptionPackResponse subscribeForChannelPack(User user, SubscriptionPackRequest subscriptionPackRequest) {
        UserChannelSubscriptionPack userChannelSubscriptionPack = new UserChannelSubscriptionPack();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, subscriptionPackRequest.getMonths());

        userChannelSubscriptionPack.setExpiryDate(calendar.getTime());
        userChannelSubscriptionPack.setSubscribeDate(new Date());
        userChannelSubscriptionPack.setUser(user);

        final SubscriptionPack packByName =
                getPackByName(subscriptionPackRequest.getPackName());
        userChannelSubscriptionPack.setChannelPack(packByName);

        final long totalPrice =
                calculateSubscriptionPack(subscriptionPackRequest, packByName);

        final long totalDiscount = calculateDiscount(packByName, subscriptionPackRequest);

        final boolean userHasSufficientBalance =
                isUserHasSufficientBalance(user, totalPrice - totalDiscount);

        if (userHasSufficientBalance) {
            // save user channel subscription
            userChannelSubscriptionPackRepository.saveAndFlush(userChannelSubscriptionPack);
            user.setBalance(user.getBalance() - (totalPrice - totalDiscount));

            //save user balance
            userRepository.saveAndFlush(user);

            return makeResponseModel(user.getBalance(), packByName, subscriptionPackRequest);
        } else {
            throw new D2Exception(PLEASE_RECHARGE_YOU_DON_T_HAVE_SUFFICIENT_BALANCE);
        }
    }

    private UserSubscriptionPackResponse makeResponseModel(long currentBalance,
                                                           SubscriptionPack subscriptionPack,
                                                           SubscriptionPackRequest subscriptionPackRequest) {
        UserSubscriptionPackResponse userSubscriptionResponse = new UserSubscriptionPackResponse();

        final long totalPrice =
                calculateSubscriptionPack(subscriptionPackRequest, subscriptionPack);
        final long totalDiscount = calculateDiscount(subscriptionPack, subscriptionPackRequest);

        userSubscriptionResponse.setTotalPrice(totalPrice);
        userSubscriptionResponse.setTotalDiscount(totalDiscount);
        userSubscriptionResponse.setFinalPrice(totalPrice - totalDiscount);

        userSubscriptionResponse.setUserCurrentBalance(currentBalance);

        return userSubscriptionResponse;
    }

    private long calculateDiscount(SubscriptionPack subscriptionPack,
                                   SubscriptionPackRequest subscriptionPackRequest) {
        return (subscriptionPack.getPrice() / subscriptionPack.getDiscount())
                * subscriptionPackRequest.getMonths();
    }

    private boolean isUserHasSufficientBalance(User user,
                                               long finalPrice) {
        return user.getBalance() >= finalPrice;
    }

    private long calculateSubscriptionPack(SubscriptionPackRequest subscriptionPackRequest,
                                           SubscriptionPack packByName) {
        final int months = subscriptionPackRequest.getMonths();
        return packByName.getPrice() * months;
    }

    private SubscriptionPack getPackByName(String packName) {
        return subscriptionPackRepository
                .findByPackName(packName)
                .orElseThrow(() -> new D2Exception("pack name not found - " + packName));
    }


    @Override
    public UserChannelSubscriptionResponse subscribeForChannels(User userByToken,
                                                                List<String> channelNames) {
        final List<Channel> byChannelName =
                channelSubscriptionRepositoy.findByChannelName(channelNames);
        if (byChannelName.size() == 0) {
            throw new D2Exception("No channel found in System" + channelNames);
        } else {
            // has Already subscribed to channels
            final List<UserChannelSubscription> userChannelSubscriptions = hasAlreadySubscribedForChannels(userByToken, channelNames);
            if (userChannelSubscriptions.size() > 0) {
                final String alreadySubsChannel =
                        userChannelSubscriptions
                                .stream()
                                .map(x -> x.getChannel().getChannelName())
                                .collect(Collectors.joining(","));
                throw new D2Exception("user has already subscribed to - " + alreadySubsChannel);
            }
            // subscribe To Channels
            final Optional<Long> totalPrice = byChannelName
                    .stream()
                    .map(Channel::getPrice)
                    .reduce((x, y) -> x + y);

            final boolean userHasSufficientBalance =
                    isUserHasSufficientBalance(userByToken, totalPrice.get());

            if (userHasSufficientBalance) {
                // subscribe to channels

                List<UserChannelSubscription> channels = new ArrayList<>();
                byChannelName.forEach(addToChannel(userByToken, channels));

                userChannelRepository.saveAll(channels);
                userChannelRepository.flush();

                userByToken.setBalance(userByToken.getBalance() - totalPrice.get());
                final User savedUser = userRepository.saveAndFlush(userByToken);

                return UserChannelSubscriptionResponse
                        .builder()
                        .totalPrice(totalPrice.get())
                        .userCurrentBalance(savedUser.getBalance())
                        .build();
            } else {
                throw new D2Exception("You don't have sufficient balance, please recharge");
            }
        }
    }

    private List<UserChannelSubscription> hasAlreadySubscribedForChannels(User userByToken,
                                                                          List<String> channelNames) {
        return userChannelRepository
                .findByChannelName(channelNames, userByToken.getId(), new Date());
    }

    private Consumer<Channel> addToChannel(User userByToken, List<UserChannelSubscription> channels) {
        return channel -> {
            UserChannelSubscription userChannel = new UserChannelSubscription();
            userChannel.setUser(userByToken);
            userChannel.setChannel(channel);
            final Calendar instance = Calendar.getInstance();
            instance.add(Calendar.MONTH, 1);
            userChannel.setExpiryDate(instance.getTime());
            channels.add(userChannel);
        };
    }


    @Override
    public UserServiceSubscriptionResponse subscribeForService(User userByToken,
                                                               String serviceName) {
        // is already subscribed
        final boolean subscribedForService = hasAlreadySubscribeForService(userByToken, serviceName);
        if (subscribedForService) {
            throw new D2Exception("Has already subscribed for the service");
        } else {
            // subscribe For service
            final ServiceSubscription serviceSubscriptionPack =
                    getServiceSubscriptionPack(serviceName);
            final boolean userHasSufficientBalance =
                    isUserHasSufficientBalance(userByToken, serviceSubscriptionPack.getPrice());

            if (userHasSufficientBalance) {

                UserServiceSubscription userServiceSubscription = new UserServiceSubscription();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 1);
                userServiceSubscription.setExpiryDate(calendar.getTime());
                userServiceSubscription.setService(serviceSubscriptionPack);
                userServiceSubscription.setUser(userByToken);
                userByToken.setBalance(userByToken.getBalance() - serviceSubscriptionPack.getPrice());

                userServiceSubscriptionRepository.saveAndFlush(userServiceSubscription);
                final User savedUser = userRepository.saveAndFlush(userByToken);

                return UserServiceSubscriptionResponse
                        .builder()
                        .totalPrice(serviceSubscriptionPack.getPrice())
                        .userCurrentBalance(savedUser.getBalance())
                        .build();
            } else {
                throw new D2Exception("Please recharge, you don't have sufficient balance");
            }
        }
    }

    private ServiceSubscription getServiceSubscriptionPack(String serviceName) {
        return serviceSubscriptionRepository
                .findByServiceName(serviceName)
                .orElseThrow(() -> new D2Exception(serviceName + " - Service not found exception"));
    }

    private boolean hasAlreadySubscribeForService(User userByToken,
                                                  String serviceName) {
        final List<UserServiceSubscription> byServiceName =
                userServiceSubscriptionRepository
                        .findByServiceName(serviceName,
                                userByToken.getId(),
                                new Date());
        return byServiceName.size() > 0;
    }

    public UserCurrentSubscriptions getCurrentUserSubsSubscriptions(User userByToken) {

        final List<UserChannelSubscriptionPack> channelSubscriptionPacks =
                userChannelSubscriptionPackRepository.findChannelSubscriptionPacks(userByToken.getId(), new Date());
        final List<UserChannelSubscription> userChannelSubscriptions =
                userChannelRepository.findChannelSubscription(userByToken.getId(), new Date());
        final List<UserServiceSubscription> userServiceSubscriptions =
                userServiceSubscriptionRepository.findUserServiceSubscriptions(userByToken.getId(), new Date());

        return UserCurrentSubscriptions.builder()
                .channelSubscriptionPacks(channelSubscriptionPacks)
                .channelSubscriptions(userChannelSubscriptions)
                .serviceSubscriptions(userServiceSubscriptions)
                .build();
    }
}
