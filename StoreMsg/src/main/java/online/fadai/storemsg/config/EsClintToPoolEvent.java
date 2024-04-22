package online.fadai.storemsg.config;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class EsClintToPoolEvent extends ApplicationEvent {
    private RestHighLevelClient restHighLevelClient;
    public EsClintToPoolEvent(RestHighLevelClient client) {
        super(client);
        restHighLevelClient = client;
    }
}
