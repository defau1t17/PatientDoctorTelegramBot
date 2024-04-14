package org.telegramchat.chat.endpoints;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.telegramchat.chat.entity.TelegramBotAuthentication;
import org.telegramchat.chat.repository.TelegramBotAuthenticationRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TokenEndpoint.class)
public class TokenEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TelegramBotAuthenticationRepository authenticationRepository;

    @Test
    public void generateTokenTest() throws Exception {
        TelegramBotAuthentication telegramBotAuthentication = spy(TelegramBotAuthentication.class);
        when(authenticationRepository.save(any())).thenReturn(telegramBotAuthentication);
        MvcResult mvcResult = mockMvc.perform(get("/token")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        String resultToken = telegramBotAuthentication.getToken();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(resultToken, mvcResult.getResponse().getContentAsString());

    }


}
