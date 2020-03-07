package com.nwb.d2hchannel.persistence;


import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "user_service_subscription")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class UserServiceSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    ServiceSubscription service;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    Date expiryDate;
}
