package ru.guzenko.vpn.vpncoffeebot;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.guzenko.vpn.vpncoffeebot.service.CliCommandsExecutor;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class VpnCoffeeBotApplication {


    @SneakyThrows
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VpnCoffeeBotApplication.class, args);
    }
}
