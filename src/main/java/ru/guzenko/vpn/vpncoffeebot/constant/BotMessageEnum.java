package ru.guzenko.vpn.vpncoffeebot.constant;

/**
 * Текстовые сообщения, посылаемые ботом
 */
public enum BotMessageEnum {
    //ответы на команды с клавиатуры

    HELP_MESSAGE(Constants.TEST_MODE_APPEND_STRING + """
            👋 Привет, я VPN-бот coffeeVPN, и я помогу Вам получить стабильное и совсем недорогое VPN соединение.
            ❗️Стоимость подписки всего лишь 200 руб/месяц, это даже меньше, чем капучино с собой❗️
            Преимущества coffeeVPN:
            ✅ Стабильное, защищенное и быстрое соединение за счет протокола WireGuard
            ✅ Реферальная программа - приведи друга и получи месяц бесплатно!
            ❗️ Скоро:
            ✅ Создание приватного VPN-сервера с неограниченным количеством клиентов в 1 клик
            ✅ Доступ на выбранное количество дней вместо месяца с гибкой оплатой
            ✅ Переключение между серверами(пока только Нидерланды)
            ✅ Конфигурация по QR-коду
                                                                        
            Воспользуйтесь клавиатурой, чтобы начать работу👇
            """ + Constants.TEST_MODE_APPEND_STRING),

    SUCCESS_SUBSCRIBED("Подписка создана успешно, что бы получить файл для подключения нажмите" +
            Constants.NEW_LINE +
            "✅*Посмотреть свою подписку*" +
            Constants.NEW_LINE +
            "Для просмотра инструкции нажмите " +
            Constants.NEW_LINE +
            "✅*Как это работает?*"),

    SUCCESS_RENEW_SUBSCRIBED("""
            Подписка создана успешно🥳
                        
            Чтобы получить файл для подключения, нажмите:
                        
            ✅Посмотреть свою подписку
                        
            ❓Для просмотра инструкции нажмите
            ✅Как это работает?
            """),

    HOW_IT_WORKS("""
            После оплаты вы получите файл, который нужно будет положить в приложение WireGuard
            И на этом все! Больше никогда ничего не нужно будет делать и париться, просто включайте VPN и пользуйтесь нужными ресурсами на высокой скорости.
            Даже если решите взять паузу с подпиской, то после возобновления ваш WireGuard заработает сразу без настройки.
            Скачать приложение можно в магазине вашего устройства или по прямым ссылкам:
            ✅ Скачать для IOS https://apps.apple.com/us/app/wireguard/id1441195209?ls=1
            ✅ Скачать для Android https://play.google.com/store/apps/details?id=com.wireguard.android&hl=ru&gl=US
            ✅ Скачать для Mac https://apps.apple.com/us/app/wireguard/id1451685025?ls=1&mt=12
            ✅ Скачать для Windows https://download.wireguard.com/windows-client/wireguard-installer.exe"""),

    NON_COMMAND_MESSAGE("Пожалуйста, воспользуйтесь клавиатурой\uD83D\uDC47" + Constants.NEW_LINE + "или отправьте /start"),

    //ошибки
    EXCEPTION_BAD_TRY_GEN_IP("Нежиданная ошибка при попытке получить ip address. Попробуйте еще раз."),
    EXCEPTION_BAD_TRY_SEND_INVOICE("Нежиданная ошибка при попытке выставить счет. Попробуйте еще раз."),

    //прочие ошибки
    EXCEPTION_WHAT_THE_FUCK("Что-то пошло не так. Обратитесь к программисту");

    private final String message;

    BotMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}