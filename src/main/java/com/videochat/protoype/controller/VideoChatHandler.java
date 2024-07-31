
package com.videochat.protoype.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VideoChatHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new HashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String sessionId = (String) session.getAttributes().get("sessionId");

        Map<String, Object> msg = objectMapper.readValue(message.getPayload(), Map.class);
        msg.put("sender", sessionId);

        // Broadcast the message to other clients
        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            if (!entry.getKey().equals(sessionId)) {
                entry.getValue().sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        session.getAttributes().put("sessionId", sessionId);
        sessions.put(sessionId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = (String) session.getAttributes().get("sessionId");
        sessions.remove(sessionId);
    }
}

// package com.videochat.protoype.controller;

// import java.io.IOException;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.web.socket.CloseStatus;
// import org.springframework.web.socket.TextMessage;
// import org.springframework.web.socket.WebSocketSession;
// import org.springframework.web.socket.handler.TextWebSocketHandler;

// public class VideoChatHandler extends TextWebSocketHandler {

//     private static final Map<String, WebSocketSession> sessions = new HashMap<>();

//     @Override
//     public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//         String sessionId = (String) session.getAttributes().get("sessionId");

//         // Broadcast the message to other clients
//         for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
//             System.out.println(message);
//             if (!entry.getKey().equals(sessionId)) {
//                 entry.getValue().sendMessage(message);
//             }
//         }
//     }

//     @Override
//     public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//         // System.out.println(session);
//         String sessionId = session.getId();
//         System.out.println("SessionId:: " + sessionId);
//         session.getAttributes().put("sessionId", sessionId);
//         sessions.put(sessionId, session);
//     }
// @Override
//     public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//         String sessionId = (String) session.getAttributes().get("sessionId");
//         sessions.remove(sessionId);
//     }
// }