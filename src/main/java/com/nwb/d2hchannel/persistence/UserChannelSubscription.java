package com.nwb.d2hchannel.persistence;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "user_channel_subscriptions")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class UserChannelSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id", nullable = false)
    Channel channel;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    Date expiryDate;
}
