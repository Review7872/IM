package online.fadai.storemsg.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class EsClientConfig {
    @Value("${elasticsearch.port}")
    private int port;
    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.poolSize}")
    private int poolSize;
    @Bean(name = "esPool")
    public EsClient esPool(){
        EsClient esClient = new EsClient();
        for (int i = 0; i < (poolSize==0 ? 1 : poolSize ) ; i++) {
            esClient.addClient(new RestHighLevelClient(
                    RestClient.builder(new HttpHost(host, port))));
        }
        return  esClient;
    }
    @Bean(name = "eventExecutor")
    public Executor getAsyncExecutor() {
        return new ThreadPoolExecutor(4,8,10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1024));
    }
}
