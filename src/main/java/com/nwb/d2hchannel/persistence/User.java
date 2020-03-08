package com.nwb.d2hchannel.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "users")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String firstName;
    @Column(nullable = false)
    String lastName;

    @Column(nullable = false)
    String phoneNo;

    @Column(nullable = false)
    String email;

    @JsonIgnore
    @Column(nullable = false)
    String password;

    long balance;

    @CreatedDate
    Date createDate;
}
