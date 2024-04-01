package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class DoctorsKeyboard {

    public InlineKeyboardMarkup getDoctorsKeyboard(int page, int maxPage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = null;
        if (page > 0) {
            inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("PREVIOUS");
            inlineKeyboardButton.setCallbackData("BACK");
            keyboardButtonsRow.add(inlineKeyboardButton);
        }
        if (page < maxPage) {
            inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("NEXT");
            inlineKeyboardButton.setCallbackData("NEXT");
            keyboardButtonsRow.add(inlineKeyboardButton);
        }

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("STOP");
        inlineKeyboardButton.setCallbackData("STOP");
        keyboardButtonsRow2.add(inlineKeyboardButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
