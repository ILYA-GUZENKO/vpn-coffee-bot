package ru.guzenko.vpn.vpncoffeebot.constant;

/**
 * Названия кнопок основной клавиатуры
 */
public enum ButtonNameEnum {
    BUY_SUB_BUTTON("Купить/продлить подписку"),
    HOW_IT_WORKS("Как это работает?"),
    GET_MY_SUBSCRIPTION("Посмотреть свою подписку"),
    REF_PROGRAM("Реферальная программа"),
    HELP_BUTTON("Помощь");

    private final String buttonName;

    ButtonNameEnum(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonName() {
        return buttonName;
    }
}