package online.fadai.netty_web.server;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
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
    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    private static final Map<Channel, String> userInfoMap = new ConcurrentHashMap<>();
    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource(name = "localIp")
    private String localIpPort;
    public static final String REDIS_HASH_ONLINE_NETTY = "NETTY_ONLINE";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        Msg msgObj = JSON.parseObject(msg.text(), Msg.class);
        if (msgObj.getType() == 201) {
            channelMap.put(msgObj.getMsgSender(), channel);
            userInfoMap.put(channel, msgObj.getMsgSender());
            redisTemplate.opsForHash().put(REDIS_HASH_ONLINE_NETTY,msgObj.getMsgSender(),localIpPort);
            log.info("{}已经上线", msgObj.getMsgSender());
            channel.writeAndFlush(new TextWebSocketFrame("成功连接服务器"));
        } else if (msgObj.getType() == 202) {
            channelMap.remove(msgObj.getMsgSender());
            userInfoMap.remove(channel);
            redisTemplate.opsForHash().delete(REDIS_HASH_ONLINE_NETTY,msgObj.getMsgSender());
            log.info("{}已经离线，离线原因：退出登录", msgObj.getMsgSender());
        } else if (msgObj.getType() == 101 || msgObj.getType() == 102) {
            Channel receiverChannel = channelMap.get(msgObj.getMsgReceiver());
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
        String userInfo = userInfoMap.get(channel);
        if (userInfo != null){
            redisTemplate.opsForHash().delete(REDIS_HASH_ONLINE_NETTY,userInfo);
            channelMap.remove(userInfo);
        }
        if (channel != null){
            userInfoMap.remove(channel);
        }
        log.info("{}已经离线，离线原因：网络异常/客户端被关闭", userInfo);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
