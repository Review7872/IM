package online.fadai.storemsg.mq;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import online.fadai.storemsg.es.MsgDao;
import online.fadai.storemsg.es.MsgES;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RocketMQMessageListener(topic = "store",consumerGroup = "msg-consumer"
        ,messageModel = MessageModel.CLUSTERING
        ,consumeMode = ConsumeMode.CONCURRENTLY
)
public class StoreMsgConsumer implements RocketMQListener<String> {
    @Resource
    private MsgDao msgDao;
    @Override
    public void onMessage(String message) {
        MsgES msgES = JSON.parseObject(message, MsgES.class);
        msgES.setDate(new Date().getTime());
        msgES.setId(UUID.randomUUID().toString());
        msgDao.save(msgES);
    }
}
