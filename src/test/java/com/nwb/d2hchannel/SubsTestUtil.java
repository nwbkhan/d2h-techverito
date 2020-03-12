package com.nwb.d2hchannel;

import com.nwb.d2hchannel.request.LoginRequest;
import com.nwb.d2hchannel.services.AuthService;

import java.util.HashMap;
import java.util.Map;

public class SubsTestUtil {

    static Map<String, String> buildSusbPackRequest() {
        Map<String, String> request = new HashMap<>();
        request.put("packName", "Gold");
        request.put("months", "3");
        return request;
    }

    static String getNoBalUserToken(AuthService authService) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("abcd1234");
        loginRequest.setUsername("3423423");
        return authService.makeLogin(loginRequest);
    }
}
