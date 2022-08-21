package ru.guzenko.vpn.vpncoffeebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
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

    public Customer getCustomer(Message message) {
        Customer customer;
        Optional<Customer> optionalCustomer = customerRepository.getCustomerByChatId(message.getChat().getId());
        if (optionalCustomer.isEmpty()) {
            customer = customerRepository.save(Customer.builder()
                    .chatId(message.getChat().getId())
                    .userName(message.getFrom().getUserName())
                    .regDate(OffsetDateTime.now())
                    .build());
        } else {
            customer = optionalCustomer.get();
        }
        return customer;
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
                return customerRepository.save(customer);
            }
        }
        return customer;
    }

    public Customer renewSubscription(Customer customer) {
        OffsetDateTime prevNextPaymentDate = customer.getNextPaymentDate();
        if (prevNextPaymentDate.isAfter(OffsetDateTime.now())) {
            customer.setNextPaymentDate(customer.getNextPaymentDate().plusDays(30));
        } else {
            customer.setNextPaymentDate(OffsetDateTime.now().plusDays(30));
        }
        return customerRepository.save(customer);
    }
}
