package com.messaging_service.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public String handleMessage(String message) {
        logger.info("Mensaje recibido en el servidor: {}", message);
        System.out.println("Mensaje recibido en el servidor: " + message);
        return message;
    }
}
