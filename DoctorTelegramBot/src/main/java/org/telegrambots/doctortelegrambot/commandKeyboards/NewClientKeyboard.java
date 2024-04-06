package org.telegrambots.doctortelegrambot.commandKeyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class NewClientKeyboard {


    public InlineKeyboardMarkup getKeyboardOnVerificationState(int page, int maxPage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = null;

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("EDIT NAME");
        inlineKeyboardButton.setCallbackData("EDIT_NAME");
        keyboardButtonsRow.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("EDIT SECOND NAME");
        inlineKeyboardButton.setCallbackData("EDIT_SECOND_NAME");
        keyboardButtonsRow.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("EDIT DISEASE");
        inlineKeyboardButton.setCallbackData("EDIT_DISEASE");
        keyboardButtonsRow.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("EDIT STATE");
        inlineKeyboardButton.setCallbackData("EDIT_STATE");
        keyboardButtonsRow2.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("EDIT STATE");
        inlineKeyboardButton.setCallbackData("EDIT_STATE");
        keyboardButtonsRow2.add(inlineKeyboardButton);


        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("EDIT CHAMBER");
        inlineKeyboardButton.setCallbackData("EDIT_CHAMBER");
        keyboardButtonsRow2.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("EDIT DESCRIPTION");
        inlineKeyboardButton.setCallbackData("EDIT_DESCRIPTION");
        keyboardButtonsRow2.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("SAVE");
        inlineKeyboardButton.setCallbackData("SAVE");
        keyboardButtonsRow2.add(inlineKeyboardButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getStatesKeyboard(int page, int maxPage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = null;
        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("STABLE");
        inlineKeyboardButton.setCallbackData("STABLE");
        keyboardButtonsRow.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("HARD");
        inlineKeyboardButton.setCallbackData("HARD");
        keyboardButtonsRow.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("CRITICAL");
        inlineKeyboardButton.setCallbackData("CRITICAL");
        keyboardButtonsRow.add(inlineKeyboardButton);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }




}
