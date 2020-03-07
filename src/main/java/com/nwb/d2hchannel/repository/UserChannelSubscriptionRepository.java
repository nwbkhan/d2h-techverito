package com.nwb.d2hchannel.repository;

import com.nwb.d2hchannel.persistence.UserChannelSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface UserChannelSubscriptionRepository
        extends JpaRepository<UserChannelSubscription, Long> {

    @Query("select p " +
            "from UserChannelSubscription p " +
            "where p.channel.channelName in (?1) " +
            "and p.user.id = (?2) " +
            "and p.expiryDate >= (?3)")
    List<UserChannelSubscription> findByChannelName(List<String> channelName,
                                                    Long id,
                                                    Date expiredDate);

    @Query("select p " +
            "from UserChannelSubscription p " +
            "where p.user.id = (?2) " +
            "and p.expiryDate >= (?3)")
    List<UserChannelSubscription> findChannelSubscription(Long id,
                                                          Date expiryDate);
}