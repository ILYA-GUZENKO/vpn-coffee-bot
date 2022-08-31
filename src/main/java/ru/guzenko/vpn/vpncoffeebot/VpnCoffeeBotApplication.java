package ru.guzenko.vpn.vpncoffeebot;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.guzenko.vpn.vpncoffeebot.service.ConfigFileService;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class VpnCoffeeBotApplication {


    @SneakyThrows
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VpnCoffeeBotApplication.class, args);

        //ConfigFileService configFileService = context.getBean(ConfigFileService.class);

        //configFileService.deletePeer("bozhanovalina2");
        //configFileService.updatePeerName("feedfest777", "feedfest777666", "W0YxAOKntgQjDN4LhmCH3rKoko880Z+Okf9ecuA4bQA=", "10.230.46.165/32");
    }
}
