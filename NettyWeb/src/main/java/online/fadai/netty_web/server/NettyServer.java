package online.fadai.netty_web.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyServer implements ApplicationRunner {
    @Value("${netty.port}")
    private int port;
    @Value("${netty.bossGroupThread}")
    private int bossGroupThread;
    @Value("${netty.workerGroupThread}")
    private int workerGroupThread;
    @Resource
    private GroupChatServerHandler groupChatServerHandler;
    @Resource
    private WebSocketHandler webSocketHandler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupThread);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerGroupThread);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // http编解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 用于大数据的分区传输
                        pipeline.addLast(new ChunkedWriteHandler());
                        // websocket数据块聚合器
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        // http协议升级为ws
                        pipeline.addLast(webSocketHandler);
                        pipeline.addLast(groupChatServerHandler);
                    }
                });
        log.info("端口号{}的NettyWeb服务已经启动", port);
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        channelFuture.channel().closeFuture().sync();
    }
}
