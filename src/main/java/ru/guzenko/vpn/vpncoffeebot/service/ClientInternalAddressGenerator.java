package ru.guzenko.vpn.vpncoffeebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientInternalAddressGenerator {

    /**
     * 10.0.0.0–10.255.255.255
     */
    public String generate() {
        var a = ThreadLocalRandom.current().nextInt(255);
        var b = ThreadLocalRandom.current().nextInt(255);
        var c = ThreadLocalRandom.current().nextInt(255);
        return "10." + a + "." + b + "." + c + "/32";
    }
    /*@SneakyThrows
    public String generate() {
        var customer = customerRepository.findTopByOrderByIdDesc();
        String startIP = customer.getInternalIpAddress();
        String[] split = startIP.split("\\.");
        int A = Integer.parseInt(split[0]);
        int B = Integer.parseInt(split[1]);
        int C = Integer.parseInt(split[2]);
        int D = Integer.parseInt(split[3].substring(0, split[3].length() - 3));
        if (255 > D) {
            D++;
        } else {
            D = 0;
            if (255 > C) {
                C++;
            } else {
                C = 0;
                if (255 > B) {
                    B++;
                } else {
                    B = 0;
                    if (255 > A) {
                        A++;
                    } else {
                        throw new Exception("IP LIMIT EXCEEDED !!!!!");
                    }
                }
            }
        }
        String IP = A + "." + B + "." + C + "." + D + "/32";
        System.out.println("IP:" + IP);
        return IP;
    }*/


    /*@SneakyThrows
    public String generate() {
        //TODO IMPLEMENTATION
        int totalIP = 5000;//Set the total IPs to be generated
        String startIP= "10.10.1.1"; // Set the IP start range
        int A = Integer.parseInt(startIP.split("\\.")[0]);
        int B = Integer.parseInt(startIP.split("\\.")[1]);
        int C = Integer.parseInt(startIP.split("\\.")[2]);
        int D = Integer.parseInt(startIP.split("\\.")[3]);
        int total=0;
        while (total<=totalIP) {
            total++;
            if(255>D) {D++;}
            else {D=0;
                if(255>C) {C++;}
                else {C=0;
                    if(255>B) {B++;}
                    else {B=0;
                        if(255>A) {A++;}
                        else {
                            throw new Exception("IP LIMIT EXCEEDED !!!!!");
                        }
                    }
                }
            }
            String IP=A+"."+B+"."+C+"."+D;
            System.out.println("IP:"+IP);
        }
        return "10.0.0.13/32";
    }*/
}
/*
class IPAddress {

    private final int value;

    public IPAddress(int value) {
        this.value = value;
    }

    public IPAddress(String stringValue) {
        String[] parts = stringValue.split("\\.");
        if( parts.length != 4 ) {
            throw new IllegalArgumentException();
        }
        value =
                (Integer.parseInt(parts[0], 10) << (8*3)) & 0xFF000000 |
                        (Integer.parseInt(parts[1], 10) << (8*2)) & 0x00FF0000 |
                        (Integer.parseInt(parts[2], 10) << (8*1)) & 0x0000FF00 |
                        (Integer.parseInt(parts[3], 10) << (8*0)) & 0x000000FF;
    }

    public int getOctet(int i) {

        if( i<0 || i>=4 ) throw new IndexOutOfBoundsException();

        return (value >> (i*8)) & 0x000000FF;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for(int i=3; i>=0; --i) {
            sb.append(getOctet(i));
            if( i!= 0) sb.append(".");
        }

        return sb.toString();

    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof IPAddress ) {
            return value==((IPAddress)obj).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value;
    }

    public int getValue() {
        return value;
    }

    public IPAddress next() {
        return new IPAddress(value+1);
    }


}

public class App13792784 {

    */
/**
 * @param args
 *//*

    public static void main(String[] args) {


        IPAddress ip1 = new IPAddress("192.168.0.1");

        System.out.println("ip1 = " + ip1);

        IPAddress ip2 = new IPAddress("192.168.0.255");

        System.out.println("ip2 = " + ip2);

        System.out.println("Looping:");

        do {

            ip1 = ip1.next();

            System.out.println(ip1);

        } while(!ip1.equals(ip2));


    }

}*/
