package ru.guzenko.vpn.vpncoffeebot.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.guzenko.vpn.vpncoffeebot.service.CustomerService;

@Component
@RequiredArgsConstructor
@Slf4j
public class VpnCoffeeBot extends TelegramLongPollingBot {

    @Getter
    @Value("${config.bot.name}")
    private String botUsername;

    @Getter
    @Value("${config.bot.token}")
    private String botToken;

    @Value("${config.cli.key-gen-command}")
    private String genKeyCommand;

    private final CustomerService customerService;

    @Override
    public void onUpdateReceived(Update update) {
        log.info(String.valueOf(update.getMessage().getChatId()));
        update.getMessage().getChat();
        SendMessage message = SendMessage.builder().chatId(String.valueOf(update.getMessage().getChatId())).text("Привет!").build();
        message.enableMarkdown(true);
        //вариант1
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        //вариант 2
        sendApiMethodAsync(message);
    }

}
