package com.nwb.d2hchannel.repository;

import com.nwb.d2hchannel.persistence.SubscriptionPack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionPackRepository
        extends JpaRepository<SubscriptionPack, Long> {


    Optional<SubscriptionPack> findByPackName(String packName);
}