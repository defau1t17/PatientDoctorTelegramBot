package org.telegramchat.chat.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.telegramchat.chat.entity.TelegramBotAuthentication;
import org.telegramchat.chat.service.AuthenticationService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthenticationEndpoint.class)
class AuthenticationEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService mockService;

    private TelegramBotAuthentication telegramBotAuthentication;

    private final int pageSize = 1;

    private final int pageNumber = 0;

    @BeforeEach
    void setUp() {
        telegramBotAuthentication = spy(TelegramBotAuthentication.class);
    }


    @Test
    void getAuthenticationPageTest() throws Exception {
        Page<TelegramBotAuthentication> expectedPage = new PageImpl<>(List.of(spy(TelegramBotAuthentication.class)), PageRequest.of(pageNumber, pageSize), pageNumber);

        when(mockService.findAll(pageNumber, pageSize)).thenReturn(expectedPage);

        MvcResult mvcResult = mockMvc
                .perform(get("/authenticate")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(expectedPage), mvcResult.getResponse().getContentAsString());
        verify(mockService).findAll(pageNumber, pageSize);
    }

    @Test
    void getAuthenticationByChatIDTest() throws Exception {
        when(mockService.findByChatID(anyLong())).thenReturn(Optional.of(telegramBotAuthentication));
        MvcResult mvcResult = mockMvc
                .perform(get("/authenticate/" + anyLong()))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(telegramBotAuthentication), mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getAuthenticationByChatIDTestFailure() throws Exception {
        when(mockService.findByChatID(anyLong())).thenReturn(Optional.empty());
        MvcResult mvcResult = mockMvc
                .perform(get("/authenticate/" + anyLong()))
                .andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }


    @Test
    void testAuthenticate() throws Exception {
        when(mockService.findByChatID(anyLong())).thenReturn(Optional.empty());
        when(mockService.findAuthenticationByToken(any())).thenReturn(Optional.of(telegramBotAuthentication));
        when(mockService.authenticate(anyLong(), any(TelegramBotAuthentication.class))).thenReturn(telegramBotAuthentication);
        when(mockService.update(any())).thenReturn(telegramBotAuthentication);
        MvcResult mvcResult = mockMvc.perform(patch("/authenticate")
                        .param("chatID", String.valueOf(0))
                        .param("token", anyString())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(telegramBotAuthentication), mvcResult.getResponse().getContentAsString());
    }

    @Test
    void authenticationTestFailure() throws Exception {
        when(mockService.findByChatID(anyLong())).thenReturn(Optional.empty());
        when(mockService.findAuthenticationByToken(anyString())).thenReturn(Optional.empty());
        MvcResult mvcResult = mockMvc.perform(patch("/authenticate")
                        .param("chatID", String.valueOf(0))
                        .param("token", anyString())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }


}
