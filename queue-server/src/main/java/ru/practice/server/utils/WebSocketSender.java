package ru.practice.server.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

@Service
public class WebSocketSender {
    private SimpMessagingTemplate template;

    @Autowired
    WebSocketSender(SimpMessagingTemplate template){
        this.template = template;
    }

    @EventListener
    public void send(String message){
        template.convertAndSend("/results", message);
    }
}
