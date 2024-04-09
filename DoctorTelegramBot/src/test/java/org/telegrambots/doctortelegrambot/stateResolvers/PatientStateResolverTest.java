package org.telegrambots.doctortelegrambot.stateResolvers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegrambots.doctortelegrambot.commandKeyboards.PatientsKeyboard;
import org.telegrambots.doctortelegrambot.dto.PaginatedPatientsDTO;
import org.telegrambots.doctortelegrambot.dto.PatientDTO;
import org.telegrambots.doctortelegrambot.dto.UserDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.ChatStateRequestService;
import org.telegrambots.doctortelegrambot.services.PatientRequestService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientStateResolverTest {

    @Mock
    private PatientRequestService mockRequestService;
    @Mock
    private PatientsKeyboard mockPatientsKeyboard;
    @Mock
    private ChatStateRequestService mockChatStateRequestService;

    private PatientStateResolver patientStateResolverUnderTest;

    private Update update;
    private Message message;

    private ChatState chatState;

    @BeforeEach
    void setUp() {
        update = new Update();
        chatState = new ChatState(1L);
        message = new Message();
        patientStateResolverUnderTest = new PatientStateResolver(mockRequestService, mockPatientsKeyboard,
                mockChatStateRequestService);
    }

    @Test
    void testResponseOnStateWhenChatStateIsDefaultAndCommandIsPatient() {
        message.setChat(new Chat(1L, "Long"));
        message.setText("/patient");
        update.setMessage(message);

        when(mockChatStateRequestService.updateChatState(anyLong(), any(ChatStates.class))).thenReturn(Optional.of(chatState));

        final SendMessage result = patientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "Please, enter ID of the patient below : ");
    }

    @Test
    void testResponseOnStateWhenChatStateIsDefaultAndCommandIsPatients() {
        message.setChat(new Chat(1L, "Long"));
        message.setText("/patients");
        update.setMessage(message);
        PaginatedPatientsDTO paginatedPatientsDTO = new PaginatedPatientsDTO(List.of(new PatientDTO(0, "test", PatientState.STABLE, 1, "-", spy(UserDTO.class))), 0);

        when(mockRequestService.getPaginatedPatients(anyInt(), anyInt())).thenReturn(paginatedPatientsDTO);
        when(mockChatStateRequestService.updateChatState(anyLong(), any(ChatStates.class))).thenReturn(Optional.of(chatState));

        final SendMessage result = patientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), paginatedPatientsDTO.toString());

    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForPatientID() {
        message.setText("1");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOR_PATIENT_ID);

        PatientDTO patientDTO = new PatientDTO(0, "test", PatientState.STABLE, 1, "-", spy(UserDTO.class));

        when(mockRequestService.getPatientByID(anyLong())).thenReturn(Optional.of(patientDTO));

        final SendMessage result = patientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), patientDTO.toString());

        verify(mockChatStateRequestService).moveChatStateToNextState(anyLong());

    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForPatientIDBadNumber() {
        message.setText("L");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOR_PATIENT_ID);

        final SendMessage result = patientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), TelegramBotResponses.SYNTAX_ERROR.getDescription());
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForNextOrPreviousCommandAndCallbackIsNext() {
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("NEXT");
        callbackQuery.setMessage(message);

        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND);

        PaginatedPatientsDTO paginatedPatientsDTO = new PaginatedPatientsDTO(List.of(new PatientDTO(0, "test", PatientState.STABLE, 1, "-", spy(UserDTO.class))), 0);

        when(mockRequestService.getPaginatedPatients(anyInt(), anyInt())).thenReturn(paginatedPatientsDTO);

        final SendMessage result = patientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), paginatedPatientsDTO.toString());
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForNextOrPreviousCommandAndCallbackIsBack() {
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("BACK");
        callbackQuery.setMessage(message);

        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND);

        PaginatedPatientsDTO paginatedPatientsDTO = new PaginatedPatientsDTO(List.of(new PatientDTO(0, "test", PatientState.STABLE, 1, "-", spy(UserDTO.class))), 0);

        when(mockRequestService.getPaginatedPatients(anyInt(), anyInt())).thenReturn(paginatedPatientsDTO);

        final SendMessage result = patientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), paginatedPatientsDTO.toString());
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForNextOrPreviousCommandAndCallbackIsStop() {
        message.setChat(new Chat(1L, "Long"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("STOP");
        callbackQuery.setMessage(message);

        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND);

        final SendMessage result = patientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "The operation is suspended. \nIf you would like to learn about patients in the hospital, press --> '/patients'");
    }

}
