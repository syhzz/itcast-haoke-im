package cn.itcast.haoke.im.pojo;

import java.util.HashMap;

public class UserData {
    public static final HashMap<Long, User> USER_MAP = new HashMap<>();

    static {
        USER_MAP.put(1001L, User.builder().id(1001L).userName("zhangsan").build());
        USER_MAP.put(1002L, User.builder().id(1002L).userName("lisi").build());
        USER_MAP.put(1003L, User.builder().id(1003L).userName("wangwu").build());
        USER_MAP.put(1004L, User.builder().id(1004L).userName("zhaoliu").build());
        USER_MAP.put(1005L, User.builder().id(1005L).userName("sunqi").build());

    }
}
