package cn.itcast.haoke.im.handler;

import cn.itcast.haoke.im.dao.MessageDao;
import cn.itcast.haoke.im.pojo.Message;
import cn.itcast.haoke.im.pojo.UserData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageHandler extends TextWebSocketHandler {
    @Autowired
    private MessageDao messageDao;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Map<Long, WebSocketSession> SESSION_MAP = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long uid = (Long) session.getAttributes().get("uid");
        SESSION_MAP.put(uid, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long uid = (Long) session.getAttributes().get("uid");
        JsonNode jsonNode = mapper.readTree(message.getPayload());
        long toId = jsonNode.get("toId").asLong();
        String msg = jsonNode.get("msg").asText();
        Message message1 = Message.builder().from(UserData.USER_MAP.get(uid))
                .to(UserData.USER_MAP.get(toId))
                .msg(msg)
                .build();
        message1 = this.messageDao.saveMessage(message1);
        WebSocketSession toSession = SESSION_MAP.get(toId);
        if (toSession != null && toSession.isOpen()) {
            toSession.sendMessage(new TextMessage(mapper.writeValueAsBytes(message)));
            this.messageDao.updateMessageState(message1.getId(), 1);
        }
    }
}
