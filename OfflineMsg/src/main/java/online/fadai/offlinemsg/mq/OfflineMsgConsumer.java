package online.fadai.offlinemsg.mq;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import online.fadai.pojo.Msg;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RocketMQMessageListener(topic = "offline",consumerGroup = "msg-consumer"
        ,messageModel = MessageModel.CLUSTERING
        ,consumeMode = ConsumeMode.CONCURRENTLY
)
public class OfflineMsgConsumer implements RocketMQListener<String> {
    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public void onMessage(String message) {
        Msg msg = JSON.parseObject(message, Msg.class);
        String s = msg.getMsgReceiver() + UUID.randomUUID();
        redisTemplate.opsForValue().set(s,message);
        redisTemplate.expire(s, 3, TimeUnit.DAYS);
    }
}
