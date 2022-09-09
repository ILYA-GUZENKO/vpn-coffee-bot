package ru.guzenko.vpn.vpncoffeebot.constant;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Названия кнопок основной клавиатуры
 */
public enum InlineButtonNameEnum {
    NEXT_PAYMENT_DATE("/nextPaymentDate"),
    GET_CONFIG_FILE("/getConfigFile"),
    GET_QR_CODE("/getQrCode"),
    NONE("nonel");

    private final String buttonName;

    InlineButtonNameEnum(String buttonName) {
        this.buttonName = buttonName;
    }

    public static Set<InlineButtonNameEnum> SUBSCRIPTION_INFO_SET = Set.of(NEXT_PAYMENT_DATE, GET_CONFIG_FILE, GET_QR_CODE);
    public static Set<InlineButtonNameEnum> HOW_IT_WORKS_CALLBACKS_SET = Set.of();

    public static InlineButtonNameEnum of(String data) {
        if (data == null) {
            return NONE;
        }
        return Stream.of(values())
                .filter(value -> value.buttonName.equalsIgnoreCase(data))
                .findFirst()
                .orElse(NONE);
    }

    public String getButtonName() {
        return buttonName;
    }
}