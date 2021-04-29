package com.grad.prjct.controller;

import com.grad.prjct.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketBroadcastController {

    @GetMapping("/stomp-broadcast")
    public String getWebSocketBroadcast() {
        return "stomp-broadcast";
    }
    
    @GetMapping("/sockjs-broadcast")
    public String getWebSocketWithSockJsBroadcast() {
        return "sockjs-broadcast";
    }
    
    @MessageMapping("/broadcast")
    @SendTo("/topic/broadcast")
    public ChatMessage send(ChatMessage chatMessage) throws Exception {
        return new ChatMessage(chatMessage.getFrom(), chatMessage.getText(), "ALL");
    }
}
