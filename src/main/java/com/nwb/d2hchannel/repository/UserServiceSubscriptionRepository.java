package com.nwb.d2hchannel.repository;


import com.nwb.d2hchannel.persistence.UserChannelSubscription;
import com.nwb.d2hchannel.persistence.UserServiceSubscription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface UserServiceSubscriptionRepository
        extends JpaRepository<UserServiceSubscription, Long> {

    @Query("select p " +
            "from UserServiceSubscription p " +
            "where p.service.serviceName = (?1) " +
            "and p.user.id = (?2) " +
            "and p.expiryDate >= (?3) order by id desc")
    List<UserServiceSubscription> findByServiceName(String serviceName,
                                                    Long id,
                                                    Date expiredDate);

    @Query("select p " +
            "from UserServiceSubscription p " +
            "where p.user.id = (?2) " +
            "and p.expiryDate >= (?3)")
    List<UserServiceSubscription> findUserServiceSubscriptions(Long id,
                                                               Date date);

}
