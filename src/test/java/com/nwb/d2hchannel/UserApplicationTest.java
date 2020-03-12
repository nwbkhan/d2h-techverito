package com.nwb.d2hchannel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nwb.d2hchannel.persistence.User;
import com.nwb.d2hchannel.persistence.UserToken;
import com.nwb.d2hchannel.repository.TokenRepository;
import com.nwb.d2hchannel.repository.UserRepository;
import com.nwb.d2hchannel.services.AuthService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletException;
import java.util.*;

import static com.nwb.d2hchannel.AbstractTest.objToString;
import static com.nwb.d2hchannel.AuthApplicationTest.getUserToken;
import static com.nwb.d2hchannel.AuthTestUtil.*;
import static org.hamcrest.collection.ArrayMatching.hasItemInArray;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class UserApplicationTest extends AbstractTest{
    private final static String USER_PATH_REQ = PathConstants.USER_PATH;
    private final static String POST_PATH_REQ = USER_PATH_REQ;
    private final static String GET_PATH_REQ = USER_PATH_REQ;
    private final static String RECHARGE_PATH_REQ = USER_PATH_REQ + "/recharge";

    public UserApplicationTest() {
        super(false);
    }

    @Test
    public void getUserTest() throws Exception {
        mockMvc
                .perform(
                        get(GET_PATH_REQ)
                                .header("token", token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.firstName").isNotEmpty())
                .andReturn();
    }

    @Test
    @Transactional
    public void postUserTest() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("phoneNo", "7464923233");
        data.put("email", "das@gmail.com");
        mockMvc.perform(
                post(POST_PATH_REQ)
                        .contentType("application/json")
                        .header("token", token)
                        .content(objToString(data))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.firstName").isNotEmpty());
    }


    @Test
    public void postUserFailTest() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("phoneNo", "");
        data.put("email", "");
        try {
            mockMvc.perform(
                    post(POST_PATH_REQ)
                            .contentType("application/json")
                            .header("token", token)
                            .content(objToString(objToString(data)))
            )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        } catch (ServletException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void userRechargeTest() throws Exception {

        final Optional<UserToken> byToken = tokenRepository.findByToken(token);
        Assert.notNull(byToken.get(), "Unexpected error, User not found");
        final User oldUser = byToken.get().getUser();
        final long oldBalence = oldUser.getBalance();
        try {
            final long recharge = 100;
            mockMvc.perform(
                    post(RECHARGE_PATH_REQ)
                            .param("rechargeAmt", String.valueOf(recharge))
                            .header("token", token)
                            .contentType("application/json")
            )
                    .andDo(print())
                    .andExpect(status().isOk());
            final Optional<User> newUpdatedUser =
                    userRepository.findByPhoneNoOrEmail(oldUser.getPhoneNo(), oldUser.getPhoneNo());

            Assert.notNull(newUpdatedUser.get());
            Assert.isTrue(newUpdatedUser.get().getBalance() == oldBalence + recharge, "Recharge or unsuccessful");
        } catch (ServletException ex) {
            ex.printStackTrace();
        }

    }

}
