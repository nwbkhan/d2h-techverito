package com.nwb.d2hchannel.repository;

import com.nwb.d2hchannel.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNoAndPassword(String username, String password);

    Optional<User> findByPhoneNoOrEmail(String phoneNo, String email);
}
