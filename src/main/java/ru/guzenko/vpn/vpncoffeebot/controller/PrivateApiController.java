package ru.guzenko.vpn.vpncoffeebot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.guzenko.vpn.vpncoffeebot.bot.VpnCoffeeBot;
import ru.guzenko.vpn.vpncoffeebot.model.Customer;
import ru.guzenko.vpn.vpncoffeebot.service.CliCommandsExecutor;
import ru.guzenko.vpn.vpncoffeebot.service.CustomerService;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@Slf4j
public class PrivateApiController {

    private final CustomerService customerService;
    private final CliCommandsExecutor cliCommandsExecutor;
    private final VpnCoffeeBot vpnCoffeeBot;

    @GetMapping("/users")
    public List<Customer> getAllUsers() {
        return customerService.getAll();
    }

    @GetMapping("/newSub")
    public Customer newSub(String userName, Long fakeChatId) {
        Customer customer = customerService.save(Customer.builder().chatId(fakeChatId).userName(userName).regDate(OffsetDateTime.now()).build());
        return customerService.regSubscription(customer);
    }

    @GetMapping("/renewSub")
    public Customer renewSub(String userName) {
        Customer customer = customerService.getCustomerByUserName(userName).orElseThrow(() -> new RuntimeException("Нет такого пользователя"));
        return customerService.renewSubscription(customer, 30);
    }

    @GetMapping("/startWg")
    public ResponseEntity<Void> startWg() {
        cliCommandsExecutor.startWg();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/restartWg")
    public ResponseEntity<Void> restartWg() {
        cliCommandsExecutor.restartWg();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statusWg")
    public List<String> statusWg() {
        return cliCommandsExecutor.statusWg();
    }

    @GetMapping("/wgShow")
    public List<String> wgShow() {
        return cliCommandsExecutor.wgShow();
    }

    @PostMapping(value = "/sendMessage")
    public ResponseEntity<Void> sendMessageForAll(@RequestBody String message) {
        customerService.getAll().forEach(customer -> {
            try {
                vpnCoffeeBot.execute(SendMessage.builder()
                        .chatId(customer.getChatId())
                        .text(message)
                        .build()
                );
            } catch (TelegramApiException e) {
                log.error("Не удалось отправить уведомление юзеру " + customer.getUserName());
                e.printStackTrace();
            }
        });
        return ResponseEntity.ok().build();
    }

    /**
     * пока тут только кейс переезда на новый сервак
     * всем у кого есть файлы заменяем адрес сервака в файле(в папке) и сохраняем его в базу юзеру
     */
    @PostMapping("/genNewSubForAll")
    public ResponseEntity<Void> genNewSubForAll() {
        //todo генерим и шлем всем новые файлы
        return ResponseEntity.ok().build();
    }

}
