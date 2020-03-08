package com.nwb.d2hchannel.repository;

import com.nwb.d2hchannel.persistence.ServiceSubscription;
import com.nwb.d2hchannel.persistence.SubscriptionPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceSubscriptionRepository
        extends JpaRepository<ServiceSubscription, Long> {

    @Query("select p from ServiceSubscription p where p.serviceName in (?1)")
    List<ServiceSubscription> findByServiceName(List<String> serviceName);
}
