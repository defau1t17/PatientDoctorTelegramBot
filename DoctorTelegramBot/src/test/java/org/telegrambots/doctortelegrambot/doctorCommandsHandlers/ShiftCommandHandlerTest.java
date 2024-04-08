package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.dto.DoctorDTO;
import org.telegrambots.doctortelegrambot.dto.UserDTO;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.ShiftRequestService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftCommandHandlerTest {

    @Mock
    private ShiftRequestService mockRequestService;

    private ShiftCommandHandler shiftCommandHandlerUnderTest;

    private Update update;

    @BeforeEach
    void setUp() {
        update = new Update();
        shiftCommandHandlerUnderTest = new ShiftCommandHandler(mockRequestService);
    }

    @Test
    void testSendResponse() {
        Message message = new Message();
        message.setText("some-text");
        message.setChat(new Chat(1L, "test"));

        update.setMessage(message);

        DoctorDTO doctorDTO = new DoctorDTO("test", 11, "test", "OPENED", 1, 0, spy(UserDTO.class));

        when(mockRequestService.changeDoctorShiftStatus(anyLong())).thenReturn(Optional.of(doctorDTO));

        final SendMessage result = shiftCommandHandlerUnderTest.sendResponse(update);

        assertNotEquals(result, null);
        assertEquals(result.getText(), "Your shift was OPENED successfully");
    }

    @Test
    void testSendResponse_ShiftRequestServiceReturnsAbsent() {
        Message message = new Message();
        message.setText("some-text");
        message.setChat(new Chat(1L, "test"));

        update.setMessage(message);
        
        when(mockRequestService.changeDoctorShiftStatus(anyLong())).thenReturn(Optional.empty());
        final SendMessage result = shiftCommandHandlerUnderTest.sendResponse(update);
        assertNotEquals(result, null);
        assertEquals(TelegramBotResponses.SOME_ERROR.getDescription(), result.getText());
    }
}
