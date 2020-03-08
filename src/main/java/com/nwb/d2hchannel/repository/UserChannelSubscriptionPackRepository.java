package com.nwb.d2hchannel.repository;

import com.nwb.d2hchannel.persistence.UserChannelSubscription;
import com.nwb.d2hchannel.persistence.UserChannelSubscriptionPack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserChannelSubscriptionPackRepository
        extends JpaRepository<UserChannelSubscriptionPack, Long> {

    @Query("select p " +
            "from UserChannelSubscriptionPack p " +
            "where p.user.id = (?1) " +
            "and p.channelPack.packName = (?2) " +
            "and p.expiryDate >= (?3) order by id desc ")
    List<UserChannelSubscriptionPack> findByUserIdAndChannelPackPackNameOrderByIdDesc(Long userId,
                                                                                      String packName,
                                                                                      Date date);

    @Query("select p " +
            "from UserChannelSubscriptionPack p " +
            "where p.user.id = (?1) " +
            "and p.expiryDate >= (?2)")
    List<UserChannelSubscriptionPack> findChannelSubscriptionPacks(Long id,
                                                                   Date expiryDate);
}
