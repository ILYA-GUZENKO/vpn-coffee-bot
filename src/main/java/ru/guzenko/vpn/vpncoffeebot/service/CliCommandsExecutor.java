package ru.guzenko.vpn.vpncoffeebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CliCommandsExecutor {

    @Value("${config.cli.key-gen-command}")
    private String genKeyCommand;

    @Value("${config.cli.work-dir}")
    private String workDir;

    /**
     * # Basic syntax:
     * # This will run command_2 after command_1 regardless of whether command_1
     * # completes successfully or not:
     * command_1; command_2
     * # This will run command_2 after command_1 if command_1 completes successfully:
     * command_1 && command_2
     * # This will run command_2 after command_1 if command_1 fails:
     * command_1 || command_2
     * # This will pass (pipe) the standard output of command_1 to command_2:
     * command_1 | command_2
     */
    public boolean makeDir(String userName) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", "mkdir " + userName);
        builder.directory(new File(workDir));
        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (new String(process.getErrorStream().readAllBytes()).contains("File exists"))
                return true;
            if (exitCode != 0) {
                log.error(MessageFormat.format("ERROR! {0} exitCode is {1}", builder.command().toString(), exitCode));
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void restartWg() {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", "systemctl restart wg-quick@wg0.service");
        builder.directory(new File(workDir));
        executeVoid(builder);
    }

    public void startWg() {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", "systemctl start wg-quick@wg0.service");
        builder.directory(new File(workDir));
        executeVoid(builder);
    }

    public List<String> statusWg() {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", "systemctl status wg-quick@wg0.service");
        builder.directory(new File(workDir));
        List<String> result = executeAndGetLines(builder);
        //todo парсить
        result.forEach(System.out::println);
        return result;
    }

    public List<String> wgShow() {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", "wg show");
        builder.directory(new File(workDir));
        List<String> result = executeAndGetLines(builder);
        //todo парсить к листу объектов? надо еще будет в базу идти по приват ключу(индекс?)
        result.forEach(System.out::println);
        return result;
    }

    public Pair<String, String> genKeysAndGet(String userName) {
        String formattedGenKeyCommand = MessageFormat.format(genKeyCommand, userName);
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", formattedGenKeyCommand);
        builder.directory(new File(workDir + "/" + userName));
        boolean keyGenerated = executeVoid(builder);
        if (!keyGenerated) throw new RuntimeException("keyGenerated false");

        ProcessBuilder catPublicKeyBuilder = new ProcessBuilder();
        catPublicKeyBuilder.command("sh", "-c", "cat " + userName + "_publickey");
        catPublicKeyBuilder.directory(new File(workDir + "/" + userName));

        ProcessBuilder catPrivateKeyBuilder = new ProcessBuilder();
        catPrivateKeyBuilder.command("sh", "-c", "cat " + userName + "_privatekey");
        catPrivateKeyBuilder.directory(new File(workDir + "/" + userName));

        return Pair.of(Objects.requireNonNull(executeAndGetString(catPublicKeyBuilder)), Objects.requireNonNull(executeAndGetString(catPrivateKeyBuilder)));
    }

    private boolean executeVoid(ProcessBuilder builder) {
        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            log.error(new String(process.getErrorStream().readAllBytes()));
            if (exitCode != 0) {
                log.error(MessageFormat.format("ERROR! {0} exitCode is {1}", builder.command().toString(), exitCode));
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String executeAndGetString(ProcessBuilder builder) {
        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0)
                throw new RuntimeException(MessageFormat.format("ERROR! {0} exitCode is {1}", builder.command().toString(), exitCode));
            BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return buf.lines().collect(Collectors.joining());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> executeAndGetLines(ProcessBuilder builder) {
        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0)
                throw new RuntimeException(MessageFormat.format("ERROR! {0} exitCode is {1}", builder.command().toString(), exitCode));
            BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return buf.lines().collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return List.of();
    }
}
