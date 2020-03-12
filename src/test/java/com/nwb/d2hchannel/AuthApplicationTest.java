package com.nwb.d2hchannel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nwb.d2hchannel.exception.D2Exception;
import com.nwb.d2hchannel.repository.UserRepository;
import com.nwb.d2hchannel.request.LoginRequest;
import com.nwb.d2hchannel.services.AuthService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import java.util.HashMap;
import java.util.Map;

import static com.nwb.d2hchannel.AuthTestUtil.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class AuthApplicationTest extends AbstractTest {
    private final static String SIGNUP_PATH_REQ = "/auth/" + PathConstants.SIGNUP_PATH;
    private final static String LOGIN_PATH_REQ = "/auth/" + PathConstants.LOGIN_PATH;
    private final static String LOGOUT_PATH_REQ = "/auth/" + PathConstants.LOGOUT_PATH;
    public static final String signupPhoneNo = "7038080585";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    public AuthApplicationTest() {
        super(false);
    }

    @Test
    public void loginSuccessTest() throws Exception {
        Map<String, String> loginREquest = buildLoginRequest(false);
        final MvcResult mvcResult = mockMvc
                .perform(
                        post(LOGIN_PATH_REQ)
                                .contentType("application/json")
                                .content(objToString(loginREquest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andReturn();

        checkForTokenExists(tokenRepository, mvcResult);
    }


    // The servlet behaves like this, so we need to catch them else we can build our own ExceptionHandlers
    // to handle for catching those exception from servlet
    @Test
    public void loginWrongUserNameFailTest() throws Exception {
        Map<String, String> loginREquest = buildLoginRequest(true);
        try {

            mockMvc.perform(
                    post(LOGIN_PATH_REQ)
                            .contentType("application/json")
                            .content(objToString(loginREquest))
            )
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType("application/json"));
        } catch (ServletException ex) {
            ex.printStackTrace();
        }
    }


    @Test
    public void signupSuccessTest() throws Exception {
        Map<String, String> signupRequest = new HashMap<>();

        buildSignupRequest(false, signupRequest);


        mockMvc.perform(
                post(SIGNUP_PATH_REQ)
                        .contentType("application/json")
                        .content(objToString(signupRequest))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        userRepository
                .findByPhoneNoOrEmail(signupPhoneNo, signupPhoneNo)
                .orElseThrow(() -> new D2Exception("User not saved in the system"));
    }


    @Test
    public void signupWithEmptyFirstNameLastNameFailTest() throws Exception {
        Map<String, String> signupRequest = new HashMap<>();
        buildSignupRequest(true, signupRequest);

        mockMvc.perform(
                post(SIGNUP_PATH_REQ)
                        .contentType("application/json")
                        .content(objToString(signupRequest))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void userLogoutTest() throws Exception {
        String token = getUserToken(authService, buildLoginRequest(false));
        mockMvc.perform(
                post(LOGOUT_PATH_REQ)
                        .contentType("application/json")
                        .header("token", token)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    static String getUserToken(AuthService authService, Map<String, String> buildLoginRequest) throws JsonProcessingException {
        final LoginRequest loginRequest =
                objectMapper.readValue(objToString(buildLoginRequest), LoginRequest.class);
        return authService.makeLogin(loginRequest);
    }

}
