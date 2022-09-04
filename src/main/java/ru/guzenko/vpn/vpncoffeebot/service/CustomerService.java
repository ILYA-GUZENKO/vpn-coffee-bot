package ru.guzenko.vpn.vpncoffeebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.guzenko.vpn.vpncoffeebot.model.Customer;
import ru.guzenko.vpn.vpncoffeebot.repository.CustomerRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ConfigFileService configFileService;
    private final ClientConfigFileCreator clientConfigFileCreator;
    private final CliCommandsExecutor cliCommandsExecutor;
    private final ClientInternalAddressGenerator clientInternalAddressGenerator;

    public static final String REF_SUCCESS_MSG = "Пробный период на 14 дней успешно активирован!";

    public Customer getCustomer(User user, Long chatId) {
        Customer customer;
        Optional<Customer> optionalCustomer = customerRepository.getCustomerByChatId(chatId);
        if (optionalCustomer.isEmpty()) {
            var userName = user.getUserName() == null ? "user" + chatId : user.getUserName();
            customer = customerRepository.save(Customer.builder()
                    .chatId(chatId)
                    .userName(userName)
                    .regDate(OffsetDateTime.now())
                    .build());
        } else {
            customer = optionalCustomer.get();
            if (!user.getUserName().equals(customer.getUserName())) {
                return renameCustomer(customer, user.getUserName());
            }
        }
        return customer;
    }

    public Optional<Customer> getCustomerByUserName(String userName) {
        return customerRepository.findByUserName(userName);
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> getByChatId(Long chatId) {
        return customerRepository.getCustomerByChatId(chatId);
    }

    public Customer regSubscription(Customer customer) {
        String userName = customer.getUserName();
        boolean makeDir;
        makeDir = cliCommandsExecutor.makeDir(userName);
        if (makeDir) {
            Pair<String, String> keyPair = cliCommandsExecutor.genKeysAndGet(userName);
            String internalIp = clientInternalAddressGenerator.generate();
            boolean addPeer = configFileService.addPeer(userName, keyPair.getFirst(), internalIp);
            if (addPeer) {
                byte[] fileBytes = clientConfigFileCreator.create(userName, keyPair.getSecond(), internalIp);
                customer.setPublicKey(keyPair.getFirst());
                customer.setPrivateKey(keyPair.getSecond());
                customer.setInternalIpAddress(internalIp);
                customer.setConfigFile(fileBytes);
                customer.setNextPaymentDate(OffsetDateTime.now().plusDays(30));
                cliCommandsExecutor.restartWg();
                log.info(cliCommandsExecutor.statusWg().toString());
                return customerRepository.save(customer);
            }
        }
        return customer;
    }

    public Customer renewSubscription(Customer customer, int days) {
        OffsetDateTime prevNextPaymentDate = customer.getNextPaymentDate();
        if (prevNextPaymentDate.isAfter(OffsetDateTime.now())) {
            customer.setNextPaymentDate(customer.getNextPaymentDate().plusDays(days));
        } else {
            customer.setNextPaymentDate(OffsetDateTime.now().plusDays(days));
        }
        cliCommandsExecutor.restartWg();
        log.info(cliCommandsExecutor.statusWg().toString());
        return customerRepository.save(customer);
    }

    private Customer renameCustomer(Customer customer, String newUserName) {
        boolean result = cliCommandsExecutor.renameUserDirAndFiles(customer.getUserName(), newUserName);
        if (result) {
            boolean updatePeerName = configFileService.updatePeerName(customer.getUserName(), newUserName, customer.getPublicKey(), customer.getInternalIpAddress());
            if (updatePeerName) {
                customer.setUserName(newUserName);
                return customerRepository.save(customer);
            } else {
                throw new RuntimeException("У пользователя сменился userName, боту не удалось его переименовать!");
            }
        } else {
            throw new RuntimeException("У пользователя сменился userName, боту не удалось его переименовать!");
        }
    }

    public SendMessage tryActivateRef(Customer customer, String refUserName) {
        if (customer.getRefUsername() == null) {
            Optional<Customer> optionalCustomer = getCustomerByUserName(refUserName);
            if (optionalCustomer.isEmpty()) {
                return SendMessage.builder()
                        .chatId(customer.getChatId())
                        .text("Пользователь с именем " + refUserName + " не найден")
                        .build();
            }
            Customer refCustomer = optionalCustomer.get();
            renewSubscription(refCustomer, 14);
            renewSubscription(customer, 14);
            return SendMessage.builder()
                    .chatId(customer.getChatId())
                    .text(REF_SUCCESS_MSG)
                    .build();
        } else {
            return SendMessage.builder()
                    .chatId(customer.getChatId())
                    .text("Вы уже использовали реферальную программу")
                    .build();
        }
    }
}
