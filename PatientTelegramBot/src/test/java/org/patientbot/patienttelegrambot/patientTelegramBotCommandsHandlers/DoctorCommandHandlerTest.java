package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.dtos.DoctorDTO;
import org.patientbot.patienttelegrambot.dtos.DoctorDTO.UserDTO;

import org.patientbot.patienttelegrambot.dtos.PagebleDoctorDTO;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.services.DoctorRequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorCommandHandlerTest {

    @Mock
    private DoctorRequestService mockDoctorRequestService;
    @Mock
    private DoctorsKeyboard mockDoctorsKeyboard;

    private DoctorCommandHandler doctorCommandHandlerUnderTest;

    private Update update;

    private ChatStateDTO chatState;

    @BeforeEach
    void setUp() {
        update = new Update();
        doctorCommandHandlerUnderTest = new DoctorCommandHandler(mockDoctorRequestService, mockDoctorsKeyboard);
    }

    @Test
    void testSendResponseWhenCommandIsDoctorID() {
        chatState = new ChatStateDTO(ChatStates.DEFAULT.name(), 1L);
        Message message = new Message();
        message.setText("/doctor_id");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);

        when(mockDoctorRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(mockDoctorRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));

        SendMessage sendMessage = doctorCommandHandlerUnderTest.sendResponse(update);

        assertEquals(sendMessage.getText(), TelegramBotResponses.INPUT_DOCTOR_ID.getDescription());

    }

    @Test
    void testSendResponseWhenCommandIsDoctorIDAndErrorHappened() {
        chatState = new ChatStateDTO(ChatStates.DEFAULT.name(), 1L);
        Message message = new Message();
        message.setText("/doctor_id");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);

        when(mockDoctorRequestService.getChatState(anyLong())).thenReturn(Optional.empty());

        SendMessage sendMessage = doctorCommandHandlerUnderTest.sendResponse(update);

        assertEquals(sendMessage.getText(), TelegramBotResponses.SOME_ERROR.getDescription());
    }


    @Test
    void testSendResponseWhenCommandIsDoctors() {
        chatState = new ChatStateDTO(ChatStates.DEFAULT.name(), 1L);
        Message message = new Message();
        message.setText("/doctors");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);

        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO(List.of(new DoctorDTO("test", 1, "test", "test", 1, 0, new DoctorDTO.UserDTO("test", "test"))), 0);


        when(mockDoctorRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(mockDoctorRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));
        when(mockDoctorRequestService.getDoctors(anyInt(), anyInt())).thenReturn(Optional.of(pagebleDoctorDTO));

        SendMessage sendMessage = doctorCommandHandlerUnderTest.sendResponse(update);

        assertEquals(sendMessage.getText(), pagebleDoctorDTO.toString());

    }


    @Test
    void testSendResponseWhenStateIsWaitingForDoctorID() {
        final Message message = new Message();
        message.setText("0");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);
        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_DOCTOR_ID.name(), 1L);
        DoctorDTO doctorDTO = new DoctorDTO("test", 11, "test", "test", 1, 0, new UserDTO("test", "test"));

        when(mockDoctorRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(mockDoctorRequestService.getDoctorByID(anyLong())).thenReturn(Optional.of(doctorDTO));

        SendMessage result = doctorCommandHandlerUnderTest.sendResponse(update);

        assertEquals(result.getText(), doctorDTO.toString());

    }


    @Test
    void testSendResponseWhenStateIsWaitingForDoctorIDFailure() {
        final Message message = new Message();
        message.setText("0");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);
        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_DOCTOR_ID.name(), 1L);

        when(mockDoctorRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(mockDoctorRequestService.getDoctorByID(anyLong())).thenReturn(Optional.empty());

        SendMessage result = doctorCommandHandlerUnderTest.sendResponse(update);

        assertEquals(result.getText(), TelegramBotResponses.NO_DOCTOR_WITH_SUCH_ID.getDescription());

    }


    @Test
    void testSendResponseWhenStateIsWaitingForNextPreviousCommandNextCallback() {
        final Message message = new Message();
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("NEXT");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS.name(), 1L);
        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO(List.of(new DoctorDTO("test", 1, "test", "test", 1, 0, new DoctorDTO.UserDTO("test", "test"))), 0);

        when(mockDoctorRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(mockDoctorRequestService.getDoctors(anyInt(), anyInt())).thenReturn(Optional.of(pagebleDoctorDTO));

        SendMessage result = doctorCommandHandlerUnderTest.sendResponse(update);

        assertEquals(result.getText(), pagebleDoctorDTO.toString());
    }


    @Test
    void testSendResponseWhenStateIsWaitingForNextPreviousCommandBackCallback() {
        final Message message = new Message();
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("BACK");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS.name(), 1L);
        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO(List.of(new DoctorDTO("test", 1, "test", "test", 1, 0, new DoctorDTO.UserDTO("test", "test"))), 0);

        when(mockDoctorRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(mockDoctorRequestService.getDoctors(anyInt(), anyInt())).thenReturn(Optional.of(pagebleDoctorDTO));

        SendMessage result = doctorCommandHandlerUnderTest.sendResponse(update);

        assertEquals(result.getText(), pagebleDoctorDTO.toString());
    }

    @Test
    void testSendResponseWhenStateIsWaitingForNextPreviousCommandStopCallback() {
        final Message message = new Message();
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("STOP");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);
        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS.name(), 1L);

        when(mockDoctorRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(mockDoctorRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));

        SendMessage result = doctorCommandHandlerUnderTest.sendResponse(update);

        assertEquals(result.getText(), "Operation '/doctors' has stopped");

    }


    @Test
    void testResponseOnStateWhenChatStateIsDefaultAndCommandIsDoctorID() {
        chatState = new ChatStateDTO(ChatStates.DEFAULT.name(), 1L);
        Message message = new Message();
        message.setText("/doctor_id");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);

        when(mockDoctorRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));

        doctorCommandHandlerUnderTest.responseOnState(ChatStates.valueOf(chatState.getChatStates()), update);

        try {
            Field responseMessageField = doctorCommandHandlerUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(doctorCommandHandlerUnderTest);
            assertEquals(responseMessage, TelegramBotResponses.INPUT_DOCTOR_ID.getDescription());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testResponseOnStateWhenChatStateIsDefaultAndCommandIsDoctors() {
        chatState = new ChatStateDTO(ChatStates.DEFAULT.name(), 1L);
        Message message = new Message();
        message.setText("/doctors");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);

        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO(List.of(new DoctorDTO("test", 1, "test", "test", 1, 0, new DoctorDTO.UserDTO("test", "test"))), 0);


        when(mockDoctorRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));
        when(mockDoctorRequestService.getDoctors(anyInt(), anyInt())).thenReturn(Optional.of(pagebleDoctorDTO));

        doctorCommandHandlerUnderTest.responseOnState(ChatStates.valueOf(chatState.getChatStates()), update);

        try {
            Field responseMessageField = doctorCommandHandlerUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(doctorCommandHandlerUnderTest);
            assertEquals(responseMessage, pagebleDoctorDTO.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForDoctorID() {
        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_DOCTOR_ID.name(), 1L);
        Message message = new Message();
        message.setText("0");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);

        DoctorDTO doctorDTO = new DoctorDTO("test", 11, "test", "test", 1, 0, new UserDTO("test", "test"));

        when(mockDoctorRequestService.getDoctorByID(anyLong())).thenReturn(Optional.of(doctorDTO));

        doctorCommandHandlerUnderTest.responseOnState(ChatStates.valueOf(chatState.getChatStates()), update);

        try {
            Field responseMessageField = doctorCommandHandlerUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(doctorCommandHandlerUnderTest);
            assertEquals(responseMessage, doctorDTO.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForNextOrPreviousCommandAndCallbackIsNext() {
        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS.name(), 1L);
        Message message = new Message();
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setMessage(message);
        callbackQuery.setData("NEXT");
        update.setCallbackQuery(callbackQuery);

        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO(List.of(new DoctorDTO("test", 1, "test", "test", 1, 0, new DoctorDTO.UserDTO("test", "test"))), 0);

        when(mockDoctorRequestService.getDoctors(anyInt(), anyInt())).thenReturn(Optional.of(pagebleDoctorDTO));

        doctorCommandHandlerUnderTest.responseOnState(ChatStates.valueOf(chatState.getChatStates()), update);

        try {
            Field responseMessageField = doctorCommandHandlerUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(doctorCommandHandlerUnderTest);
            assertEquals(responseMessage, pagebleDoctorDTO.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForNextOrPreviousCommandAndCallbackIsBack() {
        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS.name(), 1L);
        Message message = new Message();
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setMessage(message);
        callbackQuery.setData("BACK");
        update.setCallbackQuery(callbackQuery);

        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO(List.of(new DoctorDTO("test", 1, "test", "test", 1, 0, new DoctorDTO.UserDTO("test", "test"))), 0);

        when(mockDoctorRequestService.getDoctors(anyInt(), anyInt())).thenReturn(Optional.of(pagebleDoctorDTO));

        doctorCommandHandlerUnderTest.responseOnState(ChatStates.valueOf(chatState.getChatStates()), update);

        try {
            Field responseMessageField = doctorCommandHandlerUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(doctorCommandHandlerUnderTest);
            assertEquals(responseMessage, pagebleDoctorDTO.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForNextOrPreviousCommandAndCallbackIsStop() {
        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS.name(), 1L);
        Message message = new Message();
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setMessage(message);
        callbackQuery.setData("STOP");
        update.setCallbackQuery(callbackQuery);


        when(mockDoctorRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));

        doctorCommandHandlerUnderTest.responseOnState(ChatStates.valueOf(chatState.getChatStates()), update);

        try {
            Field responseMessageField = doctorCommandHandlerUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(doctorCommandHandlerUnderTest);
            assertEquals(responseMessage,"Operation '/doctors' has stopped");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testMoveChatState() {
        doctorCommandHandlerUnderTest.moveChatState(anyLong());
        verify(mockDoctorRequestService).moveChatStateToNextState(anyLong());
    }
}
