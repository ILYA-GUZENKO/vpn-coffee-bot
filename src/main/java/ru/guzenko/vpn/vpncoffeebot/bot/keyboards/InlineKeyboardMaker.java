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
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(getUrlButton("iOS", "https://apps.apple.com/us/app/wireguard/id1441195209?ls=1"));
        rowList.add(getUrlButton("Android", "https://play.google.com/store/apps/details?id=com.wireguard.android&hl=ru&gl=US"));
        rowList.add(getUrlButton("Mac", "https://apps.apple.com/us/app/wireguard/id1451685025?ls=1&mt=12"));
        rowList.add(getUrlButton("Windows", "https://download.wireguard.com/windows-client/wireguard-installer.exe"));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> getButton(String buttonName, String buttonCallBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setCallbackData(buttonCallBackData);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(button);
        return keyboardButtonsRow;
    }

    private List<InlineKeyboardButton> getUrlButton(String buttonName, String url) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setUrl(url);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(button);
        return keyboardButtonsRow;
    }
}
