package online.fadai.msghandler.service.impl;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import online.fadai.msghandler.config.WebSocketClient;
import online.fadai.msghandler.config.WebSocketMap;
import online.fadai.pojo.Msg;
import online.fadai.service.MsgService;
import online.fadai.type.SetTypeReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@DubboService
@Service
public class MsgServiceImpl implements MsgService {
    public static final String REDIS_HASH_ONLINE_NETTY = "NETTY_ONLINE";
    @Resource
    private RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

    @Override
    public int sendMsg(Msg msg) {
        if (msg.getType() == 101) {
            String ipaddr = (String) redisTemplate.opsForHash().get(REDIS_HASH_ONLINE_NETTY, msg.getMsgReceiver());
            WebSocketClient webSocketClient = WebSocketMap.getWebSocketClientMap().get(ipaddr);
            if (webSocketClient == null) {
                // todo 交付给MQ做离校消息处理
                return 1;
            }
            String jsonString = JSON.toJSONString(msg);
            try {
                webSocketClient.sendMessage(jsonString);
            } catch (IOException e) {
                // todo 发送消息失败，投入MQ等待重试
            }
            return 1;
        } else if (msg.getType() == 102) {
            Set<Long> receivers = JSON.parseObject(msg.getMsgReceiver(), SetTypeReference.getSetTypeReference().getType());
            for (Long receiver : receivers) {
                String ipaddr = (String) redisTemplate.opsForHash().get(REDIS_HASH_ONLINE_NETTY, receiver);
                WebSocketClient webSocketClient = WebSocketMap.getWebSocketClientMap().get(ipaddr);
                if (webSocketClient == null) {
                    // todo 交付给MQ做离校消息处理
                    continue;
                }
                Msg groupFriendMsg = new Msg(msg.getType(), msg.getMsgSender(), String.valueOf(receiver), msg.getMsg());
                String jsonString = JSON.toJSONString(groupFriendMsg);
                try {
                    webSocketClient.sendMessage(jsonString);
                } catch (IOException e) {
                    // todo 发送消息失败，投入MQ等待重试
                }
            }
            return 1;
        } else {
            return 1;
        }
    }
}
