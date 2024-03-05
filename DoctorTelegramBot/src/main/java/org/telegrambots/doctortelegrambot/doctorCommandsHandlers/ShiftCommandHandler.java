package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.Doctor;
import org.telegrambots.doctortelegrambot.exceptions.RestTemplateExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@Component
@RequiredArgsConstructor
public class ShiftCommandHandler implements Command {
    private final SendMessage sendMessage = new SendMessage();

    private final RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateExceptionHandler()).build();

    private String responseMessage = "";

    @Override
    public SendMessage sendResponse(Update update) {
        sendMessage.setChatId(update.getMessage().getChatId());
        Doctor doctorShiftUpdate = shiftManipulation(update.getMessage().getChatId());
        if (doctorShiftUpdate != null)
            responseMessage = "Your shift was %s successfully".formatted(doctorShiftUpdate.getShiftStatus().toString());
        else responseMessage = "Error happened while executing. We are working about it";
        sendMessage.setText(responseMessage);
        return sendMessage;
    }

    private Doctor shiftManipulation(long chatID) {
        ResponseEntity<Doctor> shiftStatusUpdate = restTemplate.postForEntity("http://localhost:8080/api/v1/doctor?chatID={id}", new Doctor(), Doctor.class, chatID);
        return shiftStatusUpdate.getStatusCode().equals(OK) ?
                shiftStatusUpdate.getBody() :
                null;
    }


}
