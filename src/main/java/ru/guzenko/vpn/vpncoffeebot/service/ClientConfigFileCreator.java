package ru.guzenko.vpn.vpncoffeebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static ru.guzenko.vpn.vpncoffeebot.constant.Constants.NEW_LINE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientConfigFileCreator {

    @Value("${config.cli.work-dir}")
    private String workDir;

    @Value("${config.file.dns}")
    private String dns;
    @Value("${config.file.public-key}")
    private String publicKey;
    @Value("${config.file.allowed-ips}")
    private String allowedIps;
    @Value("${config.file.persistent-keepalive}")
    private String persistentKeepalive;
    @Value("${config.file.endpoint}")
    private String endpoint;

    /**
     * [Interface]
     * PrivateKey = yPv8ay1RbDbR6sVN1Tm/73a2x9wd/IR45x65S9ZSnlw=
     * Address = 10.0.0.13/32
     * DNS = 8.8.8.8
     * <p>
     * [Peer]
     * PublicKey = iSAk7NSZQTub+ZI4z53cK2dNhXZyEhaiESWLC6WYnVU=
     * Endpoint = 103.136.41.156:51830
     * AllowedIPs = 0.0.0.0/0
     * PersistentKeepalive = 20
     */
    public byte[] create(String userName, String privateKey, String address) {
        var str = new StringBuilder()
                .append("[Interface]")
                .append(NEW_LINE)
                .append("PrivateKey = ").append(privateKey)
                .append(NEW_LINE)
                .append("Address = ").append(address)
                .append(NEW_LINE)
                .append("DNS = ").append(dns)
                .append(NEW_LINE)
                .append(NEW_LINE)
                .append("[Peer]")
                .append(NEW_LINE)
                .append("PublicKey = ").append(publicKey)
                .append(NEW_LINE)
                .append("Endpoint = ").append(endpoint)
                .append(NEW_LINE)
                .append("AllowedIPs = ").append(allowedIps)
                .append(NEW_LINE)
                .append("PersistentKeepalive = ").append(persistentKeepalive)
                .append(NEW_LINE)
                .toString();

        var dir = workDir + "/" + userName + "/" + "client_wg.conf";
        Path path = Paths.get(dir);
        try {
            Files.deleteIfExists(path);
            byte[] strToBytes = str.getBytes();
            Files.write(path, strToBytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW, StandardOpenOption.SYNC);
            return strToBytes;
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
