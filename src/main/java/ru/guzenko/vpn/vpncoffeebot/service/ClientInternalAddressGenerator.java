package ru.guzenko.vpn.vpncoffeebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientInternalAddressGenerator {

    public String generate() {
        //TODO IMPLEMENTATION
        return "10.0.0.13/32";
    }
}
