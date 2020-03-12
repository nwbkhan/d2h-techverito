package com.nwb.d2hchannel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nwb.d2hchannel.repository.*;
import com.nwb.d2hchannel.services.AuthService;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static com.nwb.d2hchannel.AuthApplicationTest.getUserToken;
import static com.nwb.d2hchannel.AuthTestUtil.buildLoginRequest;

public class AbstractTest {

    static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    UserRepository userRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserChannelSubscriptionPackRepository userChannelSubscriptionPackRepository;
    @Autowired
    private UserChannelSubscriptionRepository userChannelSubscriptionRepository;
    @Autowired
    private UserServiceSubscriptionRepository userServiceSubscriptionRepository;

    MockMvc mockMvc;
    String token;

    private boolean isAuth;

    public AbstractTest(boolean isAuth) {
        this.isAuth = isAuth;
    }

    @Before
    public void setup() throws JsonProcessingException {
        mockMvc =
                MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        if (!isAuth) {
            Map<String, String> loginREquest = buildLoginRequest(false);
            token = getUserToken(authService, loginREquest);
            // delete them all
            // will be in rollback (tests), no need to worry
            userChannelSubscriptionPackRepository.deleteAll();
            userChannelSubscriptionRepository.deleteAll();
            userServiceSubscriptionRepository.deleteAll();
        }
    }

    static String objToString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}
