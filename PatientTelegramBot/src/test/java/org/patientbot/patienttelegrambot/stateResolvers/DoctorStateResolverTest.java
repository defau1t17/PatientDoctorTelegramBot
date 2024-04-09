package org.patientbot.patienttelegrambot.stateResolvers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.dtos.DoctorDTO;
import org.patientbot.patienttelegrambot.dtos.PagebleDoctorDTO;
import org.patientbot.patienttelegrambot.entity.ChatState;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers.DoctorsKeyboard;
import org.patientbot.patienttelegrambot.services.ChatStateRequestService;
import org.patientbot.patienttelegrambot.services.DoctorRequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorStateResolverTest {

    @Mock
    private DoctorRequestService doctorRequestService;

    @Mock
    private ChatStateRequestService chatStateRequestService;

    @Mock
    private DoctorsKeyboard doctorsKeyboard;

    private DoctorStateResolver doctorStateResolverUnderTest;

    private Update update;

    private Message message;

    private ChatState chatState;

    @BeforeEach
    void setUp() {
        message = new Message();
        chatState = new ChatState(1L);
        update = new Update();
        doctorStateResolverUnderTest = new DoctorStateResolver(doctorRequestService, chatStateRequestService, doctorsKeyboard);
    }

    @Test
    void testSendResponseWhenCommandIsDoctorID() {
        message.setText("/doctor_id");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);


        when(chatStateRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));

        SendMessage sendMessage = doctorStateResolverUnderTest.responseOnState(chatState, update);

        assertEquals(sendMessage.getText(), TelegramBotResponses.INPUT_DOCTOR_ID.getDescription());

    }

    @Test
    void testSendResponseWhenCommandIsDoctors() {
        message.setText("/doctors");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);

        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO(List.of(new DoctorDTO("test", 1, "test", "test", 1, 0, new DoctorDTO.UserDTO("test", "test"))), 0);


        when(chatStateRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));
        when(doctorRequestService.getDoctors(anyInt(), anyInt())).thenReturn(Optional.of(pagebleDoctorDTO));

        SendMessage sendMessage = doctorStateResolverUnderTest.responseOnState(chatState, update);

        assertEquals(sendMessage.getText(), pagebleDoctorDTO.toString());

    }


    @Test
    void testSendResponseWhenStateIsWaitingForDoctorID() {
        chatState.setChatStates(ChatStates.WAITING_FOR_DOCTOR_ID);
        message.setText("0");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);
        DoctorDTO doctorDTO = new DoctorDTO("test", 11, "test", "test", 1, 0, new DoctorDTO.UserDTO("test", "test"));

        when(doctorRequestService.getDoctorByID(anyLong())).thenReturn(Optional.of(doctorDTO));

        SendMessage result = doctorStateResolverUnderTest.responseOnState(chatState, update);


        assertEquals(result.getText(), doctorDTO.toString());

    }


    @Test
    void testSendResponseWhenStateIsWaitingForDoctorIDFailure() {
        message.setText("0");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);
        chatState.setChatStates(ChatStates.WAITING_FOR_DOCTOR_ID);

        when(doctorRequestService.getDoctorByID(anyLong())).thenReturn(Optional.empty());

        SendMessage result = doctorStateResolverUnderTest.responseOnState(chatState, update);

        assertEquals(result.getText(), TelegramBotResponses.NO_DOCTOR_WITH_SUCH_ID.getDescription());

    }


    @Test
    void testSendResponseWhenStateIsWaitingForNextPreviousCommandNextCallback() {
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("NEXT");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS);

        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO(List.of(new DoctorDTO("test", 1, "test", "test", 1, 0, new DoctorDTO.UserDTO("test", "test"))), 0);

        when(doctorRequestService.getDoctors(anyInt(), anyInt())).thenReturn(Optional.of(pagebleDoctorDTO));

        SendMessage result = doctorStateResolverUnderTest.responseOnState(chatState, update);

        assertEquals(result.getText(), pagebleDoctorDTO.toString());
    }


    @Test
    void testSendResponseWhenStateIsWaitingForNextPreviousCommandBackCallback() {
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("BACK");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS);

        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO(List.of(new DoctorDTO("test", 1, "test", "test", 1, 0, new DoctorDTO.UserDTO("test", "test"))), 0);

        when(doctorRequestService.getDoctors(anyInt(), anyInt())).thenReturn(Optional.of(pagebleDoctorDTO));

        SendMessage result = doctorStateResolverUnderTest.responseOnState(chatState, update);

        assertEquals(result.getText(), pagebleDoctorDTO.toString());
    }

    @Test
    void testSendResponseWhenStateIsWaitingForNextPreviousCommandStopCallback() {
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("STOP");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);
        chatState.setChatStates(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS);

        when(chatStateRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));

        SendMessage result = doctorStateResolverUnderTest.responseOnState(chatState, update);

        assertEquals(result.getText(), "Operation '/doctors' has stopped");

    }

}
