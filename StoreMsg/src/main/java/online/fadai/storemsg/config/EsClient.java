package online.fadai.storemsg.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EsClient {
    private LinkedList<RestHighLevelClient> clients = new LinkedList<>();
    private Lock lock = new ReentrantLock();

    public RestHighLevelClient getClient(){
        lock.lock();
        try {
            return clients.removeFirst();
        }finally {
            lock.unlock();
        }
    }
    public void addClient(RestHighLevelClient client){
        lock.lock();
        try {
            clients.addLast(client);
        }finally {
            lock.unlock();
        }
    }
}
