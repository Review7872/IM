package online.fadai.storemsg.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MsgDao extends ElasticsearchRepository<MsgES,Integer> {
    List<MsgES> findByMsgSenderAndMsgReceiverAndDateBetween(String msgSender, String msgReceiver, long beginTime, long endTime);
}
