package com.nwb.d2hchannel.persistence;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "user_channel_packs")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class UserChannelSubscriptionPack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "pack_id", referencedColumnName = "id", nullable = false)
    SubscriptionPack channelPack;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    Date subscribeDate;

    Date expiryDate;
}
