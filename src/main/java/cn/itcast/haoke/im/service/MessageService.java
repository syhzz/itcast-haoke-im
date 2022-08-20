package cn.itcast.haoke.im.service;

import cn.itcast.haoke.im.pojo.Message;

import java.util.List;

public interface MessageService {

    List<Message> queryMessageList(Long fromId, Long toId, Integer page, Integer rows);
}
