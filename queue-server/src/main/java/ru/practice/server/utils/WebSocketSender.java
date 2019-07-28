package ru.practice.server.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Класс, позволяющий отправить результаты выполнения задач клиентскому приложению
 */
@Component
public class WebSocketSender {
    /** Объект для отправки сообщений */
    private static SimpMessagingTemplate template;

    /**
     * Конструктор класса
     * @param template объект для отправки сообщений
     */
    @Autowired
    WebSocketSender(SimpMessagingTemplate template){
        WebSocketSender.template = template;
    }

    /**
     * Метод для отправки сообщения клиентскому приложению
     * @param message сообщение
     */
    public static void send(String message){
        template.convertAndSend("/results", message);
    }
}
