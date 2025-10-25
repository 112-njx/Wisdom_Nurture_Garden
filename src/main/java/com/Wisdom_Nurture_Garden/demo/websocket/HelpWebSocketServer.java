package com.Wisdom_Nurture_Garden.demo.websocket;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

public class HelpWebSocketServer extends TextWebSocketHandler {

    // 保存所有在线用户连接（key = childId）
    private static final ConcurrentHashMap<Integer, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer userId = getUserIdFromParam(session);
        if (userId != null) {
            sessions.put(userId, session);
            System.out.println("用户 " + userId + " 已连接 WebSocket");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer userId = getUserIdFromParam(session);
        if (userId != null) {
            sessions.remove(userId);
            System.out.println("用户 " + userId + " 已断开连接");
        }
    }

    // 推送消息给指定子女
    public static void sendToChild(Integer childId, String message) {
        WebSocketSession session = sessions.get(childId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("子女 " + childId + " 未在线，无法发送消息");
        }
    }

    // 从 URL 参数中解析 userId
    private Integer getUserIdFromParam(WebSocketSession session) {
        String query = session.getUri().getQuery(); // e.g. userId=5
        if (query != null && query.startsWith("userId=")) {
            try {
                return Integer.parseInt(query.split("=")[1]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}