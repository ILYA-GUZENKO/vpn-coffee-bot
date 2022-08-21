package ru.guzenko.vpn.vpncoffeebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    private String userName;

    private String privateKey;

    private String publicKey;

    private String internalIpAddress;

    private OffsetDateTime regDate;

    private OffsetDateTime nextPaymentDate;

    //@Lob
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] configFile;

}
