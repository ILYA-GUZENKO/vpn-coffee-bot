package ru.guzenko.vpn.vpncoffeebot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.guzenko.vpn.vpncoffeebot.service.ConfigFileService;
import ru.guzenko.vpn.vpncoffeebot.service.CustomerService;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerScheduler {

    private final CustomerService customerService;
    private final ConfigFileService configFileService;

    @Async
    @Scheduled(fixedDelay = 100000)
    public void deleteExpiredSubscriptions() {
        log.info("deleteExpiredSubscriptions job started");
        log.info("deleteExpiredSubscriptions job finished");
    }

    @Async
    @Scheduled(fixedDelay = 100000)
    public void notifyOneDayLeft() {
        log.info("notifyOneDayLeft job started");
        log.info("notifyOneDayLeft job finished");
    }

    @Async
    @Scheduled(fixedDelay = 100000)
    public void proposeSubscription() {
        log.info("proposeSubscription job started");
        log.info("proposeSubscription job finished");
    }
}
