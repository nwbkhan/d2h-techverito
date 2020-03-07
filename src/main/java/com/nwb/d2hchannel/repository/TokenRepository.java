package com.nwb.d2hchannel.repository;


import com.nwb.d2hchannel.persistence.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByToken(String token);

    @Transactional
    Optional<UserToken> deleteByUserId(Long userId);
}
