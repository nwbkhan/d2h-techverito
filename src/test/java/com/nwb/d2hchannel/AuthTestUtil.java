package com.nwb.d2hchannel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nwb.d2hchannel.exception.D2Exception;
import com.nwb.d2hchannel.repository.TokenRepository;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.nwb.d2hchannel.AuthApplicationTest.signupPhoneNo;

public class AuthTestUtil {

    static ObjectMapper objectMapper = new ObjectMapper();

    static {

    }

    static ApiResponse constructForApiResponse(String data) throws JsonProcessingException {
        return objectMapper.readValue(data, ApiResponse.class);
    }


    static void checkForTokenExists(TokenRepository tokenRepository,
                                    MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {

        final String contentAsString = mvcResult.getResponse().getContentAsString();

        final ApiResponse apiResponse = constructForApiResponse(contentAsString);
        // find the token here
        tokenRepository
                .findByToken(apiResponse.getData().toString())
                .orElseThrow(() -> new D2Exception("Token does not exist in the system"));

    }

    static Map<String, String> buildLoginRequest(boolean fake) {
        Map<String, String> loginREquest = new HashMap<>();
        loginREquest.put("username", fake ? "374832748" : "7038080686");
        loginREquest.put("password", "abcd1234");
        return loginREquest;
    }

    static void buildSignupRequest(boolean fake, Map<String, String> signupRequest) {
        signupRequest.put("firstName", fake ? "" : "Nawab1");
        signupRequest.put("lastName", fake ? "" : "Khan1");
        signupRequest.put("phoneNo", signupPhoneNo);
        signupRequest.put("email", "nawab@gmail.com");
        signupRequest.put("password", "abcd1234");
    }
}
