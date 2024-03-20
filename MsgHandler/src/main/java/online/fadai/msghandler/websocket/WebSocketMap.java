package online.fadai.msghandler.websocket;

import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketMap {
    /**
     * key存储的是ipaddr
     * value存储的是具体连接对象
     */
    @Getter
    private static final Map<String, WebSocketClient> webSocketClientMap = new ConcurrentHashMap<>();
    @Resource
    private DiscoveryClient discoveryClient;
    @Scheduled(cron ="*/5 * * * * ?")
    public void run() throws URISyntaxException {
        List<ServiceInstance> nettyWeb = discoveryClient.getInstances("nettyWeb");
        for (ServiceInstance serviceInstance : nettyWeb) {
            String path = serviceInstance.getHost() + ":" + serviceInstance.getPort();
            if (!webSocketClientMap.containsKey(path)) {
                URI uri = new URI("ws://" + path + "/nettyWeb?id=0");
                WebSocketClient webSocketClient = new WebSocketClient(path);
                webSocketClient.connect(uri);
                webSocketClientMap.put(path, webSocketClient);
                log.info("{}的连接已经成功建立", path);
            }
        }
    }
}
