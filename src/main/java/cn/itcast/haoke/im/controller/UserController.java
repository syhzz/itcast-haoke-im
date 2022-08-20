package cn.itcast.haoke.im.controller;

import cn.itcast.haoke.im.pojo.Message;
import cn.itcast.haoke.im.pojo.User;
import cn.itcast.haoke.im.pojo.UserData;
import cn.itcast.haoke.im.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("user")
public class UserController {
    @Autowired
    private MessageService messageService;

    @GetMapping
    public List<Map<String, Object>> queryUserList(@RequestParam("fromId") Long fromId) {
        List<Map<String, Object>> res = new ArrayList<>();

        for (Map.Entry<Long, User> entry : UserData.USER_MAP.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", entry.getValue().getId());
            map.put("avatar", "http://itcast-haoke.oss...");
            map.put("from_user", fromId);
            map.put("info_type", null);
            map.put("to_user", map.get("id"));
            map.put("username", entry.getValue().getUserName());
            List<Message> messages = this.messageService.queryMessageList(fromId, entry.getValue().getId(), 1, 1);
            if (messages != null && !messages.isEmpty()) {
                Message message = messages.get(0);
                map.put("chat_msg", message.getMsg());
                map.put("chat_time", message.getSendDate().getTime());
            }
            res.add(map);
        }
        return res;
    }
}
