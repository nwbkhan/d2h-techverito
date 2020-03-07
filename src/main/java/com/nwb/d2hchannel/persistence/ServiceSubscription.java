package com.nwb.d2hchannel.persistence;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@Table(name = "service_subscriptions")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ServiceSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // LearnEnglish | LearnCooking
    @Column(name = "service_name")
    String serviceName;

    Long price;
}
