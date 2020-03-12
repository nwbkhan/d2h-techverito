package com.nwb.d2hchannel;

import com.nwb.d2hchannel.services.AuthService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nwb.d2hchannel.SubsTestUtil.buildSusbPackRequest;
import static com.nwb.d2hchannel.SubsTestUtil.getNoBalUserToken;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class SubscriptionTest extends AbstractTest {

    public static final String SUBS_pATH = "/subscription";
    public static final String POST_PACK_PATH = SUBS_pATH + "/pack";
    public static final String POST_CHANNEL_PATH = SUBS_pATH + "/channel";
    public static final String POST_SERVICE_PATH = SUBS_pATH + "/service";
    public static final String GET_CURRENT_PATH = SUBS_pATH + "/current";
    public static final String GET_SUBSCRIPTION_PACK_PATH = SUBS_pATH + "/pack";
    public static final String GET_CHANNEL_SUBSCRIPTION_PATH = SUBS_pATH + "/channel";
    public static final String GET_SERVICE_SUBSCRIPTION_PATH = SUBS_pATH + "/service";

    @Autowired
    private AuthService authService;

    public SubscriptionTest() {
        super(false);
    }

    @Test
    public void getChannelPackSubsTest() throws Exception {
        mockMvc
                .perform(
                        get(GET_SUBSCRIPTION_PACK_PATH)
                                .header("token", token)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data..packName", hasSize(2)))
                .andReturn();
    }


    @Test
    public void getChannelSubsTest() throws Exception {
        mockMvc
                .perform(
                        get(GET_CHANNEL_SUBSCRIPTION_PATH)
                                .header("token", token)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data..channelName", hasSize(5)))
                .andReturn();
    }


    @Test
    public void getServiceSubsTest() throws Exception {
        mockMvc
                .perform(
                        get(GET_SERVICE_SUBSCRIPTION_PATH)
                                .header("token", token)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data..serviceName", hasSize(2)))
                .andReturn();
    }

    // ------------------------------ PACK TEST ------------------------
    @Test
    @Transactional
    public void postChannelPackTest() throws Exception {
        final Map<String, String> request = buildSusbPackRequest();
        mockMvc
                .perform(
                        post(POST_PACK_PATH)
                                .header("token", token)
                                .contentType("application/json")
                                .content(objToString(request))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
    }

    // Insufficient Balance
    @Test
    @Transactional
    public void postPackFailBalanceTest() {
        final Map<String, String> request = buildSusbPackRequest();
        try {
            mockMvc
                    .perform(
                            post(POST_PACK_PATH)
                                    .header("token", getNoBalUserToken(authService))
                                    .contentType("application/json")
                                    .content(objToString(request))
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isInternalServerError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // ------------------------------ PACK TEST END------------------------


    // ------------------------------ CHANNEL TEST ------------------------
    @Test
    @Transactional
    public void postChannelSubsTest() throws Exception {
        final List<String> channels = new ArrayList<>();
        channels.add("Zee");
        channels.add("Sony");
        mockMvc
                .perform(
                        post(POST_CHANNEL_PATH)
                                .header("token", token)
                                .param("channelNames", channels.toArray(new String[0]))
                                .contentType("application/json")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
    }

    // Insufficient Balance
    @Test
    @Transactional
    public void postChannelFailBalanceTest() throws Exception {
        final List<String> channels = new ArrayList<>();
        channels.add("Zee");
        channels.add("Sony");
        try {
            mockMvc
                    .perform(
                            post(POST_CHANNEL_PATH)
                                    .header("token", getNoBalUserToken(authService))
                                    .param("channelNames", channels.toArray(new String[0]))
                                    .contentType("application/json")
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn();
        } catch (ServletException ex) {
            ex.printStackTrace();
        }
    }
    // ------------------------------ CHANNEL TEST END ------------------------

    // ------------------------------ SERVICE TEST ------------------------
    @Test
    @Transactional
    public void postServiceSubsTest() throws Exception {
        final List<String> services = new ArrayList<>();
        services.add("LearnEnglish");
        mockMvc
                .perform(
                        post(POST_SERVICE_PATH)
                                .header("token", token)
                                .param("serviceName", services.toArray(new String[0]))
                                .contentType("application/json")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @Transactional
    public void postServiceFailBalanceTest() throws Exception {
        // Insufficient Balance
        try {
            final List<String> services = new ArrayList<>();
            services.add("LearnEnglish");
            mockMvc
                    .perform(
                            post(POST_SERVICE_PATH)
                                    .header("token", getNoBalUserToken(authService))
                                    .param("serviceName", services.toArray(new String[0]))
                                    .contentType("application/json")
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isInternalServerError());
        } catch (ServletException ex) {
            ex.printStackTrace();
        }
    }
    // ------------------------------ SERVICE TEST END ------------------------


    @Test
    @Transactional
    public void getCurrentUserAllSubsTest() throws Exception {
        // Insufficient Balance
        try {
            mockMvc
                    .perform(
                            get(GET_CURRENT_PATH)
                                    .header("token", token)
                                    .contentType("application/json")
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.channelSubscriptionPacks").isArray())
                    .andExpect(jsonPath("$.data.channelSubscriptions").isArray())
                    .andExpect(jsonPath("$.data.serviceSubscriptions").isArray());
        } catch (ServletException ex) {
            ex.printStackTrace();
        }
    }


}
