package ru.guzenko.vpn.vpncoffeebot.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.guzenko.vpn.vpncoffeebot.bot.keyboards.InlineKeyboardMaker;
import ru.guzenko.vpn.vpncoffeebot.bot.keyboards.ReplyKeyboardMaker;
import ru.guzenko.vpn.vpncoffeebot.constant.BotMessageEnum;
import ru.guzenko.vpn.vpncoffeebot.constant.ButtonNameEnum;
import ru.guzenko.vpn.vpncoffeebot.constant.InlineButtonNameEnum;
import ru.guzenko.vpn.vpncoffeebot.model.Customer;
import ru.guzenko.vpn.vpncoffeebot.service.CustomerService;

import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

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

    private final CustomerService customerService;
    private final ReplyKeyboardMaker replyKeyboardMaker;
    private final InlineKeyboardMaker inlineKeyboardMaker;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            processCallbackQuery(update.getCallbackQuery());
        } else {
            Message message = update.getMessage();
            if (message != null) {
                answerMessage(update.getMessage());
            }
        }
    }

    private void answerMessage(Message message) {
        Customer customer = customerService.getCustomer(message);
        String inputText = message.getText();
        String chatId = message.getChatId().toString();

        if (inputText == null) {
            throw new IllegalArgumentException();
        } else if (inputText.equals("/start")) {
            sendApiMethodAsync(getStartMessage(chatId));
        } else if (inputText.equals(ButtonNameEnum.BUY_SUB_BUTTON.getButtonName())) {
            sendApiMethodAsync(getBuySubscriptionMessage(customer));
        } else if (inputText.equals(ButtonNameEnum.HOW_IT_WORKS.getButtonName())) {
            sendApiMethodAsync(getHowItWorksMessage(chatId));
        } else if (inputText.equals(ButtonNameEnum.GET_MY_SUBSCRIPTION.getButtonName())) {
            sendApiMethodAsync(getMySubscriptionInfoMessage(chatId));
        } else if (inputText.equals(ButtonNameEnum.HELP_BUTTON.getButtonName())) {
            SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getMessage());
            sendMessage.enableMarkdown(true);
            sendApiMethodAsync(sendMessage);
        } else {
            sendApiMethodAsync(new SendMessage(chatId, BotMessageEnum.NON_COMMAND_MESSAGE.getMessage()));
        }
    }

    private void processCallbackQuery(CallbackQuery buttonQuery) {
        String data = buttonQuery.getData();
        InlineButtonNameEnum buttonName = InlineButtonNameEnum.of(data);
        if (InlineButtonNameEnum.SUBSCRIPTION_INFO_SET.contains(buttonName)) {
            processSubscriptionInfoCallbackQuery(buttonQuery);
        } else if (InlineButtonNameEnum.HOW_IT_WORKS_CALLBACKS_SET.contains(buttonName)) {
            processHowItWorksCallbackQuery(buttonQuery);
        }

    }

    private void processSubscriptionInfoCallbackQuery(CallbackQuery buttonQuery) {
        final String chatId = buttonQuery.getMessage().getChatId().toString();
        String data = buttonQuery.getData();
        Customer customer = customerService.getCustomer(buttonQuery.getMessage());
        if (customer.getNextPaymentDate() == null) {
            sendApiMethodAsync(new SendMessage(chatId, "У вас нет активной подписки \uD83D\uDE22"));
        }
        if (data.equals(InlineButtonNameEnum.NEXT_PAYMENT_DATE.getButtonName())) {
            sendApiMethodAsync(new SendMessage(chatId, customer.getNextPaymentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        } else if (data.equals(InlineButtonNameEnum.GET_CONFIG_FILE.getButtonName())) {
            try {
                execute(new SendDocument(chatId, new InputFile(new ByteArrayInputStream(customer.getConfigFile()), customer.getUserName() + "_wg.conf")));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            sendApiMethodAsync(new SendMessage(chatId, BotMessageEnum.NON_COMMAND_MESSAGE.getMessage()));
        }
    }

    private void processHowItWorksCallbackQuery(CallbackQuery buttonQuery) {

    }

    private SendMessage getBuySubscriptionMessage(Customer customer) {
        OffsetDateTime nextPaymentDate = customer.getNextPaymentDate();
        SendMessage message = new SendMessage();
        message.setChatId(customer.getChatId().toString());
        message.enableMarkdown(true);
        if (nextPaymentDate == null) {
            Customer updatedCustomer = customerService.regSubscription(customer);
            if (updatedCustomer.getConfigFile() != null) {
                message.setText(BotMessageEnum.SUCCESS_SUBSCRIBED.getMessage());
            } else {
                message.setText("Не удалось создать подписку, попробуйте позже.");
            }
        } else {
            Customer updatedCustomer = customerService.renewSubscription(customer);
            if (updatedCustomer.getNextPaymentDate().isAfter(nextPaymentDate)) {
                message.setText(BotMessageEnum.SUCCESS_RENEW_SUBSCRIBED.getMessage());
            } else {
                message.setText("Не удалось создать подписку, попробуйте позже.");
            }
        }
        return message;
    }

    private SendMessage getHowItWorksMessage(String chatId) {
        //TODO инлайн клавиаутра с гайдом под каждую ОС
        SendMessage sendMessage = new SendMessage(chatId, "пока не муею");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getInlineButtonsForHowItWorks());
        return sendMessage;
    }

    private SendMessage getMySubscriptionInfoMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Выбирите действие");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getInlineButtonsForSubscriptionInfo());
        return sendMessage;
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        return sendMessage;
    }


}
