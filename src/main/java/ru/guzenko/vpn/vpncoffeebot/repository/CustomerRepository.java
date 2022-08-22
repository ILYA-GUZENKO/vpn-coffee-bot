package ru.guzenko.vpn.vpncoffeebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.guzenko.vpn.vpncoffeebot.model.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> getCustomerByChatId(Long chatId);

    Customer findTopByOrderByIdDesc();
}
