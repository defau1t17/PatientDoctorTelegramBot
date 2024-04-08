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
import org.telegrambots.doctortelegrambot.commandKeyboards.NewPatientKeyboard;
import org.telegrambots.doctortelegrambot.dto.NewPatientDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.ChatStateRequestService;
import org.telegrambots.doctortelegrambot.services.PatientRequestService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewPatientStateResolverTest {

    @Mock
    private PatientRequestService mockRequestService;
    @Mock
    private ChatStateRequestService mockChatStateRequestService;
    @Mock
    private NewPatientKeyboard mockNewPatientKeyboard;

    private NewPatientStateResolver newPatientStateResolverUnderTest;

    private Update update;

    private Message message;

    private ChatState chatState;

    NewPatientDTO newPatientDTO;


    @BeforeEach
    void setUp() {
        message = new Message();
        update = new Update();
        newPatientDTO = spy(NewPatientDTO.class);
        chatState = new ChatState(1L);
        newPatientStateResolverUnderTest = new NewPatientStateResolver(mockRequestService, mockChatStateRequestService,
                mockNewPatientKeyboard);
    }

    @Test
    void testResponseOnStateWhenChatStateIsDefault() {
        message.setText("some-message");
        message.setChat(new Chat(1L, "test"));
        update.setMessage(message);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "Please write patient name below : ");
        verify(mockChatStateRequestService).updateChatState(anyLong(), any());
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForName() {
        message.setText("some-message");
        message.setChat(new Chat(1L, "test"));
        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOR_NAME);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "Please write patient second name below : ");
        verify(mockChatStateRequestService).moveChatStateToNextState(anyLong());
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForSecondName() {
        message.setText("some-message");
        message.setChat(new Chat(1L, "test"));
        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOR_SECONDNAME);


        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "Please describe patient disease : ");
        verify(mockChatStateRequestService).moveChatStateToNextState(anyLong());
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForDisease() {
        message.setText("some-text");
        message.setChat(new Chat(1L, "test"));

        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOR_DISEASE);


        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "Please choose one of the states : ");
        verify(mockChatStateRequestService).moveChatStateToNextState(anyLong());
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForPatientStateSuccess() {
        message.setChat(new Chat(1L, "test"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(PatientState.STABLE.name());
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOR_PATIENT_STATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "Please input the chamber number in range [10-999] : ");
        verify(mockChatStateRequestService).moveChatStateToNextState(anyLong());
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForPatientStateFailure() {
        message.setChat(new Chat(1L, "test"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("some-data");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOR_PATIENT_STATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), TelegramBotResponses.SYNTAX_ERROR.getDescription());
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForChamberSuccess() {
        message.setText("1");
        message.setChat(new Chat(1L, "test"));
        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOR_CHAMBER_NUMBER);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "You may add some description(if not just print '-') : ");
        verify(mockChatStateRequestService).moveChatStateToNextState(anyLong());
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForChamberFailureBecauseOfBadSyntax() {
        message.setText("L");
        message.setChat(new Chat(1L, "test"));
        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOR_CHAMBER_NUMBER);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), TelegramBotResponses.SYNTAX_ERROR.getDescription()
                .concat("Please input the chamber number in range [10-999] : "));
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForChamberFailureBecauseOfWrongRange() {
        message.setText("110");
        message.setChat(new Chat(1L, "test"));
        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOR_CHAMBER_NUMBER);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), TelegramBotResponses.SYNTAX_ERROR.getDescription()
                .concat("Please input the chamber number in range [10-999] : "));
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForDescription() {
        message.setText("some-message");
        message.setChat(new Chat(1L, "test"));
        update.setMessage(message);

        newPatientDTO.setDescription(message.getText());

        chatState.setChatStates(ChatStates.WAITING_FOR_DESCRIPTION);
        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "New patient to be created : \n%s".formatted(newPatientDTO.toString()));
        verify(mockChatStateRequestService).moveChatStateToNextState(anyLong());
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForNameUpdate() {
        message.setText("some-message");
        message.setChat(new Chat(1L, "test"));
        update.setMessage(message);

        newPatientDTO.setName(message.getText());

        chatState.setChatStates(ChatStates.WAITING_FOF_NAME_UPDATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "New patient to be created : \n%s"
                .formatted(newPatientDTO.toString()));
        verify(mockChatStateRequestService).updateChatState(anyLong(), any());
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForSecondNameUpdate() {
        message.setText("some-message");
        message.setChat(new Chat(1L, "test"));
        update.setMessage(message);

        newPatientDTO.setSecondName(message.getText());

        chatState.setChatStates(ChatStates.WAITING_FOF_SECONDNAME_UPDATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "New patient to be created : \n%s"
                .formatted(newPatientDTO.toString()));
        verify(mockChatStateRequestService).updateChatState(anyLong(), any());
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForDiseaseUpdate() {
        message.setText("some-message");
        message.setChat(new Chat(1L, "test"));
        update.setMessage(message);

        newPatientDTO.setDisease(message.getText());

        chatState.setChatStates(ChatStates.WAITING_FOF_DISEASE_UPDATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "New patient to be created : \n%s"
                .formatted(newPatientDTO.toString()));
        verify(mockChatStateRequestService).updateChatState(anyLong(), any());
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForStateUpdateSuccess() {
        message.setChat(new Chat(1L, "test"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(PatientState.STABLE.name());
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        newPatientDTO.setPatientState(PatientState.valueOf(callbackQuery.getData()));

        chatState.setChatStates(ChatStates.WAITING_FOF_STATE_UPDATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "New patient to be created : \n%s"
                .formatted(newPatientDTO.toString()));
        verify(mockChatStateRequestService).updateChatState(anyLong(), any());
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForStateUpdateFailure() {
        message.setChat(new Chat(1L, "test"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("some-state");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOF_STATE_UPDATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "Please select new patient state : ");
    }


    @Test
    void testResponseOnStateWhenChatStateIsWaitingForChamberUpdateSuccess() {
        message.setChat(new Chat(1L, "test"));
        message.setText("1");
        update.setMessage(message);

        newPatientDTO.setChamberNumber(Integer.parseInt(message.getText()));

        chatState.setChatStates(ChatStates.WAITING_FOF_CHAMBER_UPDATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "New patient to be created : \n%s"
                .formatted(newPatientDTO.toString()));
        verify(mockChatStateRequestService).updateChatState(anyLong(), any());
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForChamberUpdateFailureBecauseOfRange() {
        message.setChat(new Chat(1L, "test"));
        message.setText("110");
        update.setMessage(message);


        chatState.setChatStates(ChatStates.WAITING_FOF_CHAMBER_UPDATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "The number you wrote is incorrect!\nPlease input new chamber number in range [10-999] : ");
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForChamberUpdateFailureBecauseOfSyntax() {
        message.setChat(new Chat(1L, "test"));
        message.setText("L");
        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOF_CHAMBER_UPDATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "The number you wrote is incorrect!\nPlease input new chamber number in range [10-999] : ");

    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForDescriptionUpdate() {
        message.setChat(new Chat(1L, "test"));
        message.setText("some-message");
        update.setMessage(message);

        newPatientDTO.setDescription(message.getText());

        chatState.setChatStates(ChatStates.WAITING_FOF_DESCRIPTION_UPDATE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "New patient to be created : \n%s"
                .formatted(newPatientDTO.toString()));
        verify(mockChatStateRequestService).updateChatState(anyLong(), any());
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForApproveAndCallbackNotSaveOrClear() {
        message.setChat(new Chat(1L, "test"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("EDIT_NAME");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOF_APPROVE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "Please input new name below: ");

        verify(mockChatStateRequestService).updateChatState(anyLong(), any());

    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForApproveAndCallbackIsSave() {
        message.setChat(new Chat(1L, "test"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("SAVE");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOF_APPROVE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "New patient successfully saved");
        verify(mockChatStateRequestService).rollBackChatStateToDefault(anyLong());
    }

    @Test
    void testResponseOnStateWhenChatStateIsWaitingForApproveAndCallbackIsClear() {
        message.setChat(new Chat(1L, "test"));
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("CLEAR");
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        chatState.setChatStates(ChatStates.WAITING_FOF_APPROVE);

        SendMessage result = newPatientStateResolverUnderTest.responseOnState(chatState, update);
        assertNotEquals(result, null);
        assertEquals(result.getText(), "New patient successfully removed");
        verify(mockChatStateRequestService).rollBackChatStateToDefault(anyLong());
    }


}
