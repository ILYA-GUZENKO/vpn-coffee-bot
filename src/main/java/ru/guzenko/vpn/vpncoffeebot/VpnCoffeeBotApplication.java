package ru.guzenko.vpn.vpncoffeebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.util.Pair;
import ru.guzenko.vpn.vpncoffeebot.service.CliCommandsExecutor;
import ru.guzenko.vpn.vpncoffeebot.service.ConfigFileService;

@SpringBootApplication
public class VpnCoffeeBotApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(VpnCoffeeBotApplication.class, args);

        CliCommandsExecutor cliCommandsExecutor = applicationContext.getBean(CliCommandsExecutor.class);
        /*boolean makeDir = cliCommandsExecutor.makeDir("balagur");
        Pair<String, String> pair = cliCommandsExecutor.genKeysAndGet("balagur");
        System.out.println(pair.toString());
        boolean addPeer = cliCommandsExecutor.addPeer("balagur", pair.getFirst(), "10.0.0.14/32");*/

        ConfigFileService configFileService = applicationContext.getBean(ConfigFileService.class);
        configFileService.deletePeer("balagur");
    }
}
