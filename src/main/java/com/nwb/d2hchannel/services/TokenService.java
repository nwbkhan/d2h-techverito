package com.nwb.d2hchannel.services;

import com.nwb.d2hchannel.exception.TokenNotFondException;
import com.nwb.d2hchannel.persistence.User;
import com.nwb.d2hchannel.persistence.UserToken;
import com.nwb.d2hchannel.repository.TokenRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public User getUserByToken(String token) {

        final UserToken userToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() -> new TokenNotFondException("token not found"));
        return userToken.getUser();
    }

}
