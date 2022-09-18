package ru.guzenko.vpn.vpncoffeebot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.guzenko.vpn.vpncoffeebot.bot.VpnCoffeeBot;
import ru.guzenko.vpn.vpncoffeebot.model.Customer;
import ru.guzenko.vpn.vpncoffeebot.service.ConfigFileService;
import ru.guzenko.vpn.vpncoffeebot.service.CustomerService;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerScheduler {

    private final CustomerService customerService;
    private final ConfigFileService configFileService;
    private final VpnCoffeeBot vpnCoffeeBot;

    //этот должен срабатывать первым
    //todo cron
    @Async
    @Scheduled(fixedDelay = 100000, initialDelay = 20000)
    public void deleteExpiredSubscriptions() {
        log.info("deleteExpiredSubscriptions job started");

        var deletedPeers = new ArrayList<Customer>();
        customerService.getAll().stream()
                .filter(customer -> customer.getNextPaymentDate() != null)
                .forEach(customer -> {
                    if (customer.getNextPaymentDate().isBefore(OffsetDateTime.now())) {
                        try {
                            configFileService.deletePeer(customer.getUserName());
                            vpnCoffeeBot.execute(SendMessage.builder()
                                    .chatId(customer.getChatId())
                                    .text("Ваша подписка закончилась, для возобноваления нажмите 'Купить/продлить подписку'")
                                    .build()
                            );
                            customer.setNextPaymentDate(null);
                            customerService.save(customer);
                            deletedPeers.add(customer);
                        } catch (TelegramApiException e) {
                            log.error("Не удалось отправить уведомление юзеру " + customer.getUserName());
                            e.printStackTrace();
                        }
                    }
                });
        if (!deletedPeers.isEmpty()) {
            deletedPeers.forEach(customer -> log.info("Пользавтельский пир удален. userName={}, lastName={}, firstName={}", customer.getUserName(), customer.getLastName(), customer.getFirstName()));
        } else {
            log.info("Ни один пир не удален сегодня");
        }

        log.info("deleteExpiredSubscriptions job finished");
    }

    //этот должен срабатывать  вторым
    //todo cron
    @Async
    @Scheduled(fixedDelay = 100000, initialDelay = 60000)
    public void notifyOneDayLeft() {
        log.info("notifyOneDayLeft job started");

        customerService.getAll().stream()
                .filter(customer -> customer.getNextPaymentDate() != null)
                .forEach(customer -> {
                    Duration difference = Duration.between(OffsetDateTime.now(), customer.getNextPaymentDate());
                    long hours = difference.toHours();
                    if (hours < 25) {
                        log.info("Подписка пользователя {} заканчивается через {} часов, отправляю уведомление", customer, hours);
                        try {
                            vpnCoffeeBot.execute(SendMessage.builder()
                                    .chatId(customer.getChatId())
                                    .text("Ваша подписка закончиться через 24 часа, для возобноваления нажмите 'Купить/продлить подписку'")
                                    .build()
                            );
                        } catch (TelegramApiException e) {
                            log.error("Не удалось отправить уведомление юзеру " + customer.getUserName());
                            e.printStackTrace();
                        }
                    }
                });

        log.info("notifyOneDayLeft job finished");
    }

    //этот должен срабатывать третим
    //todo cron
    //предлагать оплатить раз в неделю?
    @Async
    //@Scheduled(fixedDelay = 100000)
    public void proposeSubscription() {
        log.info("proposeSubscription job started");

        customerService.getAll().stream()
                .filter(customer -> customer.getNextPaymentDate() == null)
                .forEach(customer -> {
                    try {
                        vpnCoffeeBot.execute(SendMessage.builder()
                                .chatId(customer.getChatId())
                                .text("Ваша подписка не активна, а кнопка 'Купить' работает \uD83D\uDC47")
                                .build()
                        );
                    } catch (TelegramApiException e) {
                        log.error("Не удалось отправить предложение о подписке юзеру " + customer.getUserName());
                        e.printStackTrace();
                    }
                });

        log.info("proposeSubscription job finished");
    }
}
