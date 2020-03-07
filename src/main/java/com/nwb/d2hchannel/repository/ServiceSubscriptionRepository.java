package com.nwb.d2hchannel.repository;

import com.nwb.d2hchannel.persistence.ServiceSubscription;
import com.nwb.d2hchannel.persistence.SubscriptionPack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceSubscriptionRepository
        extends JpaRepository<ServiceSubscription, Long> {

    Optional<ServiceSubscription> findByServiceName(String serviceName);
}
