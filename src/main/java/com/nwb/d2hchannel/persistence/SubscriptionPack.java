package com.nwb.d2hchannel.persistence;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Table(name = "subscription_packs")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SubscriptionPack implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "pack_name", unique = true)
    String packName;

    Long price;

    Long discount;

    @OneToMany
    @JoinTable(name = "subscription_pack_channels",
            joinColumns = @JoinColumn(name = "subscription_pack_type", referencedColumnName = "pack_name"),
            inverseJoinColumns = @JoinColumn(name = "channel_id", referencedColumnName = "id"))
    List<Channel> channels;
}
