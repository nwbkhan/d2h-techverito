package com.nwb.d2hchannel.services;

import com.nwb.d2hchannel.exception.BadCredentialsException;
import com.nwb.d2hchannel.exception.D2Exception;
import com.nwb.d2hchannel.exception.TokenNotFondException;
import com.nwb.d2hchannel.exception.UserAlreadyExistsException;
import com.nwb.d2hchannel.persistence.User;
import com.nwb.d2hchannel.persistence.UserToken;
import com.nwb.d2hchannel.repository.TokenRepository;
import com.nwb.d2hchannel.repository.UserRepository;
import com.nwb.d2hchannel.request.LoginRequest;
import com.nwb.d2hchannel.request.SignupRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public AuthService(UserRepository userRepository,
                       TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    // worked on SSO, OAuth 2.0, SAML
    // we can be able to implement with OAuth 2.0 | with core Spring security;
    // but for demo purpose i have implemented basic logic
    public String makeLogin(LoginRequest loginRequest) {
        @NotEmpty final String password = loginRequest.getPassword();
        try {
            final String encryptedPassword = getPassword(password);

            final Optional<User> byPhoneNoAndPassword =
                    userRepository.findByPhoneNoAndPassword(loginRequest.getUsername(), encryptedPassword);

            if (!byPhoneNoAndPassword.isPresent()) {
                throw new BadCredentialsException("User name or password are invalid");
            }

            final String token = getToken(byPhoneNoAndPassword.get());
            storeToken(byPhoneNoAndPassword.get(), token);
            return token;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        throw new D2Exception("Something went wrong");
    }

    @Async
    public void storeToken(User user, String token) {
        UserToken userToken = new UserToken();
        userToken.setToken(token);
        userToken.setUser(user);
        tokenRepository.deleteByUserId(user.getId());
        tokenRepository.saveAndFlush(userToken);
    }

    private String getPassword(@NotEmpty String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        final byte[] digest = messageDigest.digest(password.getBytes());
        return DatatypeConverter.printHexBinary(digest);
    }

    private String getToken(User user) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        final String stringToEncode = user.getPhoneNo() + user.getPassword() + new Date().getTime();
        final byte[] encodedToken = digest.digest(stringToEncode.getBytes());
        return DatatypeConverter.printHexBinary(encodedToken);
    }

    // Uses MD5 has to store password
    public void makeSignup(SignupRequest signupRequest) {

        final Optional<User> userExists =
                userRepository.findByPhoneNoOrEmail(signupRequest.getPhoneNo(), signupRequest.getEmail());

        userExists.ifPresent(x -> {
            throw new UserAlreadyExistsException("User already exist, please login");
        });
        try {
            User user = new User();
            BeanUtils.copyProperties(signupRequest, user);

            user.setPassword(getPassword(signupRequest.getPassword()));
            userRepository.saveAndFlush(user);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new D2Exception("Something went wrong");
        }
    }

    public void removeToken(String token) {
        final UserToken userToken =
                tokenRepository
                        .findByToken(token)
                        .orElseThrow(() -> new TokenNotFondException("Token not found - " + token));
        tokenRepository.delete(userToken);
    }
}
