package ru.guzenko.vpn.vpncoffeebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    private Long id;

    private Long chatId;

    private String userName;

    private String privateKey;

    private String publicKey;

    private String internalIpAddress;

    private OffsetDateTime regDate;

    private OffsetDateTime nextPaymentDate;

    @Lob
    private byte[] configFile;

}
