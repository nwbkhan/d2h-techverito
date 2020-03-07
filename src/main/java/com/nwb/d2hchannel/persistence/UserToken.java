package com.nwb.d2hchannel.persistence;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@Table(name = "user_tokens")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class UserToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

}
