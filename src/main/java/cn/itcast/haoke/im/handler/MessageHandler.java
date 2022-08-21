package cn.itcast.haoke.im.handler;

import cn.itcast.haoke.im.dao.MessageDao;
import cn.itcast.haoke.im.pojo.Message;
import cn.itcast.haoke.im.pojo.UserData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RocketMQMessageListener(topic = "haoke-im-send-message-topic",
consumerGroup = "haoke-im-consumer",
messageModel = MessageModel.BROADCASTING,
selectorExpression = "SEND_MSG")
public class MessageHandler extends TextWebSocketHandler implements RocketMQListener<String> {
    @Autowired
    private MessageDao messageDao;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Map<Long, WebSocketSession> SESSION_MAP = new HashMap<>();

    @Autowired
    private RocketMQTemplate mqTemplate;

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
        } else {
            org.springframework.messaging.Message<String> mqMsg = MessageBuilder.withPayload(msg).build();
            this.mqTemplate.syncSend("haoke-im-send-message-topic:SEND_MSG", mqMsg);
        }
    }

    /**
     * 接受到消息后的处理
     * @param msg
     */
    @Override
    public void onMessage(String msg) {
        try {
            JsonNode jsonNode = mapper.readTree(msg);
            long toId = jsonNode.get("to").get("id").longValue();
            WebSocketSession session = SESSION_MAP.get(toId);
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(msg));
                this.messageDao.updateMessageState(new ObjectId(jsonNode.get("id").asText()), 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
