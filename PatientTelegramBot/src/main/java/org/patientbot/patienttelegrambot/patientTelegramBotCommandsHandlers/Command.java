package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    SendMessage sendResponse(Update update);
}
