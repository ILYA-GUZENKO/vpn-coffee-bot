package ru.guzenko.vpn.vpncoffeebot.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.guzenko.vpn.vpncoffeebot.bot.keyboards.InlineKeyboardMaker;
import ru.guzenko.vpn.vpncoffeebot.bot.keyboards.ReplyKeyboardMaker;
import ru.guzenko.vpn.vpncoffeebot.constant.BotMessageEnum;
import ru.guzenko.vpn.vpncoffeebot.constant.ButtonNameEnum;
import ru.guzenko.vpn.vpncoffeebot.constant.InlineButtonNameEnum;
import ru.guzenko.vpn.vpncoffeebot.model.Customer;
import ru.guzenko.vpn.vpncoffeebot.service.AdminService;
import ru.guzenko.vpn.vpncoffeebot.service.CustomerService;

import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private final AdminService adminService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasPreCheckoutQuery()) {
            PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
            log.info(preCheckoutQuery.toString());
            sendApiMethodAsync(AnswerPreCheckoutQuery.builder()
                    .preCheckoutQueryId(preCheckoutQuery.getId())
                    .ok(true)
                    .build());
        } else if (update.hasCallbackQuery()) {
            processCallbackQuery(update.getCallbackQuery());
            log.info(update.getCallbackQuery().toString());
        } else {
            Message message = update.getMessage();
            if (message != null) {
                log.info(message.toString());
                answerMessage(message);
            }
        }
    }

    private void answerMessage(Message message) {
        Customer customer = customerService.getCustomer(message.getFrom(), message.getChatId());
        String inputText = message.getText();
        String chatId = message.getChatId().toString();

        if (message.hasSuccessfulPayment()) {
            SuccessfulPayment successfulPayment = message.getSuccessfulPayment();
            log.info(successfulPayment.toString());
            //todo successfulPayment.getProviderPaymentChargeId(); сохранять в базу, кажется надо вообще весь successfulPayment сохранять + чатИд в новую таблицу
            sendApiMethodAsync(getBuySubscriptionMessage(customer));
        } else if (inputText == null) {
            throw new IllegalArgumentException();
        } else if (inputText.equals("/start")) {
            tryRegTrail(customer);
            sendApiMethodAsync(getStartMessage(chatId));
        } else if (inputText.startsWith("@")) {
            SendMessage sendMessage = customerService.tryActivateRef(customer, inputText.substring(1));
            sendApiMethodAsync(sendMessage);
            if (sendMessage.getText().equals(CustomerService.REF_SUCCESS_MSG)) {
                Customer refCustomer = customerService.getCustomerByUserName(inputText.substring(1)).get();
                sendApiMethodAsync(SendMessage.builder()
                        .chatId(refCustomer.getChatId())
                        .text("Подписка продлена на 14 дней за рекомендацию пользователю " + customer.getUserName())
                        .build());
            }
        } else if (inputText.equals(ButtonNameEnum.REF_PROGRAM.getButtonName())) {
            SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.REF_PROGRAM.getMessage());
            sendMessage.enableMarkdown(true);
            sendApiMethodAsync(sendMessage);
        } else if (inputText.equals(ButtonNameEnum.BUY_SUB_BUTTON.getButtonName())) {
            sendInvoice(chatId);
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
        Customer customer = customerService.getCustomer(buttonQuery.getFrom(), buttonQuery.getMessage().getChatId());
        if (customer.getNextPaymentDate() == null) {
            sendApiMethodAsync(new SendMessage(chatId, "У вас нет активной подписки \uD83D\uDE22"));
        } else if (data.equals(InlineButtonNameEnum.NEXT_PAYMENT_DATE.getButtonName())) {
            sendApiMethodAsync(new SendMessage(chatId, customer.getNextPaymentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        } else if (data.equals(InlineButtonNameEnum.GET_CONFIG_FILE.getButtonName())) {
            try {
                execute(new SendDocument(chatId, new InputFile(new ByteArrayInputStream(customer.getConfigFile()), "client_wg.conf")));
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
        try {
            if (nextPaymentDate == null) {
                Customer updatedCustomer = customerService.regSubscription(customer);
                if (updatedCustomer.getConfigFile() != null) {
                    message.setText(BotMessageEnum.SUCCESS_SUBSCRIBED.getMessage());
                } else {
                    message.setText("Не удалось создать подписку, попробуйте позже.");
                }
            } else {
                Customer updatedCustomer = customerService.renewSubscription(customer, 30);
                if (updatedCustomer.getNextPaymentDate().isAfter(nextPaymentDate)) {
                    message.setText(BotMessageEnum.SUCCESS_RENEW_SUBSCRIBED.getMessage());
                } else {
                    message.setText("Не удалось продлить подписку, попробуйте позже.");
                }
            }
        } catch (Exception e) {
            message.setText(BotMessageEnum.EXCEPTION_BAD_TRY_GEN_IP.getMessage());
        }

        return message;
    }

    private void sendInvoice(String chatId) {
        try {
            //todo вариант с ю-касса(или только ю-касса)
            execute(SendInvoice.builder()
                    .chatId(chatId)
                    .title("Оплата подписки 30 дней")
                    .description("Доступ к серверу на 30 дней")
                    .payload("Order for user" + chatId)
                    .providerToken("381764678:TEST:41545")
                    .currency("RUB")
                    .startParameter("test")
                    .prices(List.of(LabeledPrice.builder().label("Руб").amount(20000).build()))
                    .build()
            );
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            sendApiMethodAsync(new SendMessage(chatId, BotMessageEnum.EXCEPTION_BAD_TRY_SEND_INVOICE.getMessage()));
        }
    }

    private SendMessage getHowItWorksMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.HOW_IT_WORKS.getMessage());
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getInlineButtonsForHowItWorks());
        //TODO обрабатывать колбек и давать ссылку на скачивание + фото-гайд
        return sendMessage;
    }

    private SendMessage getMySubscriptionInfoMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Выберите действие");
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

    private void tryRegTrail(Customer customer) {
        if (customer.getNextPaymentDate() == null) {
            sendApiMethodAsync(SendMessage.builder().chatId(customer.getChatId()).text("Вы получили пробный доступ на 7 дней!").build());
            sendApiMethodAsync(getBuySubscriptionMessage(customer));
        }
    }
}
