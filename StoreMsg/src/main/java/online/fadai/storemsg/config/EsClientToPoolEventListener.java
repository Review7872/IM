package online.fadai.storemsg.config;

import jakarta.annotation.Resource;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EsClientToPoolEventListener {
    @Resource
    private EsClient esClientPool;
    @EventListener(EsClintToPoolEvent.class)
    @Async("eventExecutor")
    public void clientToPool(EsClintToPoolEvent event){
        RestHighLevelClient restHighLevelClient = event.getRestHighLevelClient();
        esClientPool.addClient(restHighLevelClient);
    }
}
