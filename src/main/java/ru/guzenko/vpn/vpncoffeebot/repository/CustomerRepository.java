package ru.guzenko.vpn.vpncoffeebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.guzenko.vpn.vpncoffeebot.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
