package ru.guzenko.vpn.vpncoffeebot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.guzenko.vpn.vpncoffeebot.model.Customer;
import ru.guzenko.vpn.vpncoffeebot.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@Slf4j
public class PrivateApiController {

    private final CustomerService customerService;

    @GetMapping("/users")
    public List<Customer> getAllUsers() {
        return customerService.getAll();
    }

}
