package ru.guzenko.vpn.vpncoffeebot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        Customer customer = customerService.getCustomerByUserName(userName);
        return customerService.renewSubscription(customer);
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

}
