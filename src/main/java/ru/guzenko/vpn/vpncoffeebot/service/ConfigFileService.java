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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigFileService {

    @Value("${config.cli.work-dir}")
    private String workDir;

    String newLine = System.getProperty("line.separator");

    /**
     * # BEGIN_PEER olga
     * [Peer] #olga
     * PublicKey = jsO5PQ9zOj/jLCFOiuVH+bvP3/0dVQzyYvn+uqNhkQA=
     * AllowedIPs = 10.0.0.12/32
     * # END_PEER olga
     */
    //10.0.0.0–10.255.255.255
    public boolean addPeer(String userName, String publicKey, String ip) {
        var str = new StringBuilder()
                .append(newLine)
                .append("# BEGIN_PEER ").append(userName)
                .append(newLine)
                .append("[Peer] #").append(userName)
                .append(newLine)
                .append("PublicKey = ").append(publicKey)
                .append(newLine)
                .append("AllowedIPs = ").append(ip)
                .append(newLine)
                .append("# END_PEER ").append(userName)
                .append(newLine)
                .toString();
        Path path = Paths.get(workDir + "/wg0.conf");
        byte[] strToBytes = str.getBytes();
        try {
            Files.write(path, strToBytes, StandardOpenOption.APPEND, StandardOpenOption.SYNC);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void deletePeer(String userName) {
        var peerStartLine = "# BEGIN_PEER " + userName;
        Path path = Paths.get(workDir + "/wg0.conf");
        try {
            List<String> strings = Files.readAllLines(path);
            List<String> toDelete = new ArrayList<>();
            AtomicInteger peerCounter = new AtomicInteger();
            strings.forEach(s -> {
                if (peerStartLine.equals(s.trim()) || peerCounter.get() > 0) {
                    if (peerCounter.get() < 5) {
                        toDelete.add(s);
                        peerCounter.getAndIncrement();
                    }
                }
            });
            strings.removeAll(toDelete);
            String join = String.join(newLine, strings);
            System.out.println(join);
            Files.delete(path);
            Files.write(path, join.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW, StandardOpenOption.SYNC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
