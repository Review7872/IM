package online.fadai.netty_web.server;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Scope("prototype")
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Value("${netty.webSocketPath}")
    private String webSocket;
    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource(name = "localIp")
    private String localIpPort;
    public static final String REDIS_HASH_ONLINE_NETTY = "NETTY_ONLINE";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        if (uri.startsWith("/?id=")){
            Long key = Long.parseLong(uri.substring(5));
            log.info("{}已经上线", key);
            if (!key.equals(0L)){
                GroupChatServerHandler.getChannelMap().put(key, ctx.channel());
                GroupChatServerHandler.getUserInfoMap().put(ctx.channel(), key);
                redisTemplate.opsForHash().put(REDIS_HASH_ONLINE_NETTY,key,localIpPort);
            }
            // 执行升级为 WebSocket 协议的逻辑
            ctx.pipeline().addLast(new WebSocketServerProtocolHandler(webSocket));
            // 将请求 URI 修改为新的 URI，以便后续的处理器能够识别
            request.setUri(webSocket);
            // 继续处理 WebSocket 握手请求
            ctx.fireChannelRead(request.retain());
        }else if (uri.startsWith("/nettyWeb?id=")) {
            Long key = Long.parseLong(uri.substring(13));
            log.info("{}已经上线", key);
            if (!key.equals(0L)){
                GroupChatServerHandler.getChannelMap().put(key, ctx.channel());
                GroupChatServerHandler.getUserInfoMap().put(ctx.channel(), key);
                redisTemplate.opsForHash().put(REDIS_HASH_ONLINE_NETTY,key,localIpPort);
            }
            // 执行升级为 WebSocket 协议的逻辑
            ctx.pipeline().addLast(new WebSocketServerProtocolHandler(webSocket));
            // 将请求 URI 修改为新的 URI，以便后续的处理器能够识别
            request.setUri(webSocket);
            // 继续处理 WebSocket 握手请求
            ctx.fireChannelRead(request.retain());
        }else {
            log.info("非法访问");
        }
    }
}
