package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.services.EmergencyRequestService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class EmergencyCommandHandler implements Command {
    private long CHAT_ID = 0;

    private final EmergencyRequestService requestService;

    @Override

    public SendMessage sendResponse(Update update) {
        if (update.hasMessage()) {
            CHAT_ID = update.getMessage().getChatId();
        }
        boolean resultOfEmergencyHelpRequest = requestService.sendEmergencyHelpRequest(CHAT_ID);

        return new SendMessage(String.valueOf(CHAT_ID), resultOfEmergencyHelpRequest ?
                "Your request has been successfully delivered to the doctors. Stand by, someone will be with you shortly" :
                "Your request was not successfully delivered");
    }
}
