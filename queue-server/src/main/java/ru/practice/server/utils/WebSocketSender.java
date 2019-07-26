package ru.practice.server.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketSender {
    private static SimpMessagingTemplate template;

    @Autowired
    WebSocketSender(SimpMessagingTemplate template){
        WebSocketSender.template = template;
    }

    public static void send(String message){
        template.convertAndSend("/results", message);
    }
}
