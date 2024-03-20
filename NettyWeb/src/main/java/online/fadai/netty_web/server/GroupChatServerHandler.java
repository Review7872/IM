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
    public static final String REDIS_HASH_ONLINE_NETTY = "NETTY_ONLINE";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Msg msgObj = JSON.parseObject(msg.text(), Msg.class);
        if (msgObj.getType() == 101 || msgObj.getType() == 102) {
            Channel receiverChannel = channelMap.get(Long.parseLong(msgObj.getMsgReceiver()));
            if (receiverChannel == null) {
                // todo todo 交付给MQ做离校消息处理
                return;
            }
            receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msgObj)));
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
