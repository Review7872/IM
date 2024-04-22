package online.fadai.msghandler.service.impl;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import online.fadai.msghandler.websocket.WebSocketClient;
import online.fadai.msghandler.websocket.WebSocketMap;
import online.fadai.pojo.Msg;
import online.fadai.service.MsgService;
import online.fadai.type.SetTypeReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@DubboService
@Service
@Slf4j
public class MsgServiceImpl implements MsgService {
    public static final String REDIS_HASH_ONLINE_NETTY = "NETTY_ONLINE";
    @Resource
    private RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public int sendMsg(Msg msg) {
        if (msg.getType() == 101) {
            String ipaddr = (String) redisTemplate.opsForHash().get(REDIS_HASH_ONLINE_NETTY, Long.parseLong(msg.getMsgReceiver()));
            WebSocketClient webSocketClient = WebSocketMap.getWebSocketClientMap().get(ipaddr);
            String jsonString = JSON.toJSONString(msg);
            if (webSocketClient == null) {
                rocketMQTemplate.asyncSend("offline",jsonString,new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.error("发送普通离线消息{}失败，原因如下：{}",jsonString,e.getMessage());
                    }
                });
                return 1;
            }
            try {
                webSocketClient.sendMessage(jsonString);
            } catch (IOException e) {
                log.error("发送普通消息：{}失败，原因如下：{}",jsonString,e.getMessage());
            }
            rocketMQTemplate.asyncSend("store",jsonString,new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                }

                @Override
                public void onException(Throwable e) {
                    log.error("普通消息存储失败{}失败，原因如下：{}",jsonString,e.getMessage());
                }
            });
            return 1;
        } else if (msg.getType() == 102) {
            Set<Long> receivers = JSON.parseObject(msg.getMsgReceiver(), SetTypeReference.getSetTypeReference().getType());
            for (Long receiver : receivers) {
                String ipaddr = (String) redisTemplate.opsForHash().get(REDIS_HASH_ONLINE_NETTY, receiver);
                WebSocketClient webSocketClient = WebSocketMap.getWebSocketClientMap().get(ipaddr);
                Msg groupFriendMsg = new Msg(msg.getType(), msg.getMsgSender(), String.valueOf(receiver), msg.getMsg());
                String jsonString = JSON.toJSONString(groupFriendMsg);
                if (webSocketClient == null) {
                    rocketMQTemplate.asyncSend("offline",jsonString,new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("发送群聊离线消息{}失败，原因如下：{}",jsonString,e.getMessage());
                        }
                    });
                    continue;
                }
                try {
                    webSocketClient.sendMessage(jsonString);
                } catch (IOException e) {
                    log.error("发送群聊消息：{}失败，原因如下：{}",jsonString,e.getMessage());
                }
                rocketMQTemplate.asyncSend("store",jsonString,new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.error("群聊消息存储失败{}失败，原因如下：{}",jsonString,e.getMessage());
                    }
                });
            }
            return 1;
        } else {
            return 1;
        }
    }
}
