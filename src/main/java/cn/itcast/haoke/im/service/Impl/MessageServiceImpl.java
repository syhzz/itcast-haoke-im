package cn.itcast.haoke.im.service.Impl;

import cn.itcast.haoke.im.dao.MessageDao;
import cn.itcast.haoke.im.pojo.Message;
import cn.itcast.haoke.im.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageDao messageDao;
    @Override
    public List<Message> queryMessageList(Long fromId, Long toId, Integer page, Integer rows) {
        List<Message> messages = this.messageDao.findListByFromAndTo(fromId, toId, page, rows);
        for (Message message : messages) {
            if (message.getStatus() == 1) {
                this.messageDao.updateMessageState(message.getId(), 2);
            }
        }
        return messages;
    }
}
