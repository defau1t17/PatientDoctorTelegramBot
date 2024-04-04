package org.telegramchat.chat.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.telegramchat.chat.entity.ChatState;
import org.telegramchat.chat.entity.ChatStates;
import org.telegramchat.chat.service.StateService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ChatStateEndpoint.class)
public class ChatStateEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StateService stateService;

    private final int pageNumber = 0;
    private final int pageSize = 1;

    private ChatState chatState;

    @BeforeEach
    void setUp() {
        chatState = spy();
    }

    @Test
    void findAllChatStates_Success() throws Exception {
        Page<ChatState> expectedPage = new PageImpl<>(List.of(spy(ChatState.class)), PageRequest.of(pageNumber, pageSize), 1L);
        when(stateService.findAll(pageNumber, pageSize)).thenReturn(expectedPage);

        MvcResult mvcResult = mockMvc.perform(get("/chatstate")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(expectedPage), mvcResult.getResponse().getContentAsString());
        verify(stateService).findAll(pageNumber, pageSize);
    }

    @Test
    void findAllChatStates_EmptyPage() throws Exception {
        Page<ChatState> expectedPage = new PageImpl<>(List.of(), PageRequest.of(pageNumber, pageSize), 0);
        when(stateService.findAll(pageNumber, pageSize)).thenReturn(expectedPage);
        MvcResult mvcResult = mockMvc.perform(get("/chatstate")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(expectedPage), mvcResult.getResponse().getContentAsString());
        verify(stateService).findAll(pageNumber, pageSize);
    }

    @Test
    void findChatStateByChatID_Exists() throws Exception {
        when(stateService.findByChatID(anyLong())).thenReturn(Optional.of(chatState));
        MvcResult mvcResult = mockMvc.perform(get("/chatstate/{chatID}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(chatState), mvcResult.getResponse().getContentAsString());
        verify(stateService).findByChatID(anyLong());
    }

    @Test
    void findChatStateByChatID_NotFound() throws Exception {
        when(stateService.findByChatID(anyLong())).thenReturn(Optional.empty());
        MvcResult mvcResult = mockMvc.perform(get("/chatstate/{chatID}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
        verify(stateService).findByChatID(anyLong());
    }

    @Test
    void createNewChatState_Success() throws Exception {
        when(stateService.validateBeforeSave(any(ChatState.class))).thenReturn(true);
        when(stateService.create(any())).thenReturn(chatState);

        MvcResult mvcResult = mockMvc.perform(post("/chatstate")
                        .param("chatID", String.valueOf(1))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(chatState), mvcResult.getResponse().getContentAsString());
        verify(stateService).validateBeforeSave(any());
        verify(stateService).create(any());
    }

    @Test
    void createNewChatState_ValidationError() throws Exception {
        when(stateService.validateBeforeSave(any())).thenReturn(false);
        MvcResult mvcResult = mockMvc.perform(post("/chatstate")
                        .param("chatID", String.valueOf(1))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        verify(stateService).validateBeforeSave(any());
        verify(stateService, never()).create(any());
    }

    @Test
    void updateChatState_Success() throws Exception {
        when(stateService.findByChatID(any())).thenReturn(Optional.of(chatState));
        chatState.updateChatState(ChatStates.DEFAULT);
        when(stateService.update(any())).thenReturn(chatState);
        MvcResult mvcResult = mockMvc.perform(post("/chatstate/{chatID}/update", anyLong())
                        .param("state", ChatStates.DEFAULT.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(chatState), mvcResult.getResponse().getContentAsString());
        verify(stateService, times(2)).findByChatID(anyLong());
        verify(stateService).update(any());
    }

    @Test
    void updateChatState_ChatStateNotFound() throws Exception {
        when(stateService.findByChatID(anyLong())).thenReturn(Optional.empty());
        MvcResult mvcResult = mockMvc.perform(post("/chatstate/{chatID}/update", anyLong())
                        .param("state", ChatStates.DEFAULT.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        verify(stateService, times(1)).findByChatID(anyLong());
    }

    @Test
    void moveChatState_Success() throws Exception {
        when(stateService.findByChatID(anyLong())).thenReturn(Optional.of(chatState));
        when(stateService.moveState(any(), anyString())).thenReturn(chatState);
        chatState.updateChatState(ChatStates.WAITING_FOR_SECONDNAME);
        when(stateService.update(any())).thenReturn(chatState);
        MvcResult mvcResult = mockMvc.perform(post("/chatstate/{chatID}/move", 1L)
                        .param("move", anyString())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(chatState), mvcResult.getResponse().getContentAsString());
        verify(stateService, times(2)).findByChatID(anyLong());
        verify(stateService).moveState(any(), anyString());
    }

    @Test
    void moveChatState_ChatStateNotFound() throws Exception {
        when(stateService.findByChatID(anyLong())).thenReturn(Optional.empty());
        MvcResult mvcResult = mockMvc.perform(post("/chatstate/{chatID}/move", anyLong())
                        .param("move", anyString())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
        verify(stateService).findByChatID(anyLong());
        verify(stateService, never()).moveState(any(ChatState.class), anyString());
    }
}
