package online.fadai.netty_web.server;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import online.fadai.pojo.Msg;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Scope("prototype")
@ChannelHandler.Sharable
public class GroupChatServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Getter
    private static final Map<Long, Channel> channelMap = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Channel, Long> userInfoMap = new ConcurrentHashMap<>();
    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    public static final String REDIS_HASH_ONLINE_NETTY = "NETTY_ONLINE";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Msg msgObj = JSON.parseObject(msg.text(), Msg.class);
        if (msgObj.getType() == 101 || msgObj.getType() == 102) {
            String jsonString = JSON.toJSONString(msgObj);
            Channel receiverChannel = channelMap.get(Long.parseLong(msgObj.getMsgReceiver()));
            if (receiverChannel == null) {
                rocketMQTemplate.asyncSend("offline",jsonString,new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.error("发送离线消息{}失败，原因如下：{}",jsonString,e.getMessage());
                    }
                });
                return;
            }
            receiverChannel.writeAndFlush(new TextWebSocketFrame(jsonString));
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Long key = userInfoMap.get(channel);
        if (key != null){
            redisTemplate.opsForHash().delete(REDIS_HASH_ONLINE_NETTY,key);
            channelMap.remove(key);
        }
        if (channel != null){
            userInfoMap.remove(channel);
        }
        log.info("{}已经离线，离线原因：退出登录/网络异常/客户端被关闭", key);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
