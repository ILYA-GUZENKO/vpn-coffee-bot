package ru.guzenko.vpn.vpncoffeebot.bot.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.guzenko.vpn.vpncoffeebot.constant.ButtonNameEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Основная клавиатура, расположенная под строкой ввода текста в Telegram
 */
@Component
public class ReplyKeyboardMaker {

    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        KeyboardRow row1 = new KeyboardRow();
        //row1.add(new KeyboardButton(ButtonNameEnum.BUY_SUB_BUTTON.getButtonName()));
        row1.add(new KeyboardButton(ButtonNameEnum.HOW_IT_WORKS.getButtonName()));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(ButtonNameEnum.GET_MY_SUBSCRIPTION.getButtonName()));
        row2.add(new KeyboardButton(ButtonNameEnum.HELP_BUTTON.getButtonName()));

       //KeyboardRow row3 = new KeyboardRow();
        //row3.add(new KeyboardButton(ButtonNameEnum.REF_PROGRAM.getButtonName()));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        //keyboard.add(row3);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }
}