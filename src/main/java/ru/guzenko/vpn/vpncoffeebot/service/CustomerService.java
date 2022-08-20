package ru.guzenko.vpn.vpncoffeebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.guzenko.vpn.vpncoffeebot.repository.CustomerRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ConfigFileService configFileService;

}
