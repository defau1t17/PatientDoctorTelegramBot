package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.dto.DoctorDTO;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.ShiftRequestService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ShiftCommandHandler implements Command {
    private final SendMessage sendMessage = new SendMessage();

    private final ShiftRequestService requestService;
    private String responseMessage = "";

    private long CHAT_ID = 0;

    @Override
    public SendMessage sendResponse(Update update) {
        CHAT_ID = update.getMessage().getChatId();

        Optional<DoctorDTO> doctorDTO = requestService.changeDoctorShiftStatus(CHAT_ID);
        if (doctorDTO.isPresent()) {
            this.responseMessage = "Your shift was %s successfully".formatted(doctorDTO.get().getShiftStatus().toString());
        } else {
            this.responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
        }
        sendMessage.setChatId(CHAT_ID);
        sendMessage.setText(responseMessage);
        return sendMessage;
    }


}
