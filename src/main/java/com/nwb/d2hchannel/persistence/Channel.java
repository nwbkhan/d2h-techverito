package com.nwb.d2hchannel.persistence;


import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "channels")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Channel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String channelName;

    Long price;
}
