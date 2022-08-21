package ru.guzenko.vpn.vpncoffeebot.constant;

/**
 * Текстовые сообщения, посылаемые ботом
 */
public enum BotMessageEnum {
    //ответы на команды с клавиатуры
    HELP_MESSAGE("""
            \uD83D\uDC4B Привет, я VPN-бот coffeeVPN, и я помогу Вам получить стабильное и совсем не дорогое VPN соединение.
            ❗ *Цена всего лишь 200р/месяц, это даже меньше чем капучно с собой!*
            ✅ Стабильное, защищенное и быстро соедние за счет протокола WireGuard!
            ✅ Реферальная программа - приведи друга и получи месяц бесплатно!
            ❗ *Скоро:*
            ✅ Создание приватного VPN-сервера с неограниченным количеством клиентов в 1 клик
            ✅ Доступ на выбранное количество дней вместо месяца с гибкой оплатой
            ✅ Переключение между серверами(пока только Нидерланды)
            Воспользуйтесь клавиатурой, чтобы начать работу\uD83D\uDC47"""),
    SUCCESS_SUBSCRIBED("Подписка создана успешно, что бы получить файл для подключения нажмите" +
            Constants.NEW_LINE +
            "✅*Посмотреть свою подписку*" +
            Constants.NEW_LINE +
            "Для просмотра инструкции нажмите " +
            Constants.NEW_LINE +
            "✅*Как это работает?*"),
    SUCCESS_RENEW_SUBSCRIBED("Подписка продлена успешно, что бы получить файл для подключения нажмите" +
            Constants.NEW_LINE +
            "✅*Посмотреть свою подписку*" +
            Constants.NEW_LINE +
            "Для просмотра инструкции нажмите " +
            Constants.NEW_LINE +
            "✅*Как это работает?*"),
    NON_COMMAND_MESSAGE("Пожалуйста, воспользуйтесь клавиатурой\uD83D\uDC47"),

    //результаты загрузки словаря
    SUCCESS_UPLOAD_MESSAGE("\uD83D\uDC4D Словарь успешно загружен"),
    EXCEPTION_TELEGRAM_API_MESSAGE("Ошибка при попытку получить файл из API Telegram"),
    EXCEPTION_TOO_LARGE_DICTIONARY_MESSAGE("В словаре больше 1 000 слов. Едва ли такой большой набор словарных " +
            "слов действительно нужен, ведь я работаю для обучения детей"),
    EXCEPTION_BAD_FILE_MESSAGE("Файл не может быть обработан. Вы шлёте мне что-то не то, балуетесь, наверное"),

    //ошибки при обработке callback-ов
    EXCEPTION_BAD_BUTTON_NAME_MESSAGE("Неверное значение кнопки. Крайне странно. Попробуйте позже"),
    EXCEPTION_DICTIONARY_NOT_FOUND_MESSAGE("Словарь не найден"),
    EXCEPTION_DICTIONARY_WTF_MESSAGE("Нежиданная ошибка при попытке получить словарь. Сам в шоке"),
    EXCEPTION_TASKS_WTF_MESSAGE("Нежиданная ошибка при попытке получить задания. Сам в шоке"),
    EXCEPTION_TEMPLATE_WTF_MESSAGE("Нежиданная ошибка при попытке получить шаблон. Сам в шоке"),

    //прочие ошибки
    EXCEPTION_ILLEGAL_MESSAGE("Нет, к такому меня не готовили! Я не понимаю ваше сообщение"),
    EXCEPTION_WHAT_THE_FUCK("Что-то пошло не так. Обратитесь к программисту");

    private final String message;

    BotMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}