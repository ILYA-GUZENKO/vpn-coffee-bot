package ru.guzenko.vpn.vpncoffeebot.bot.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.guzenko.vpn.vpncoffeebot.constant.InlineButtonNameEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Клавиатуры, формируемые в ленте Telegram для получения файлов
 */
@Component
public class InlineKeyboardMaker {

    public InlineKeyboardMarkup getInlineButtonsForSubscriptionInfo() {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(getButton("Когда закончится?", InlineButtonNameEnum.NEXT_PAYMENT_DATE.getButtonName()));
        rowList.add(getButton("Получить файл для доступа", InlineButtonNameEnum.GET_CONFIG_FILE.getButtonName()));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboard getInlineButtonsForHowItWorks() {
        return null;
    }

    private List<InlineKeyboardButton> getButton(String buttonName, String buttonCallBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setCallbackData(buttonCallBackData);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(button);
        return keyboardButtonsRow;
    }
}
