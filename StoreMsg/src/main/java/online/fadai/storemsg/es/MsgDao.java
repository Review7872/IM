package online.fadai.storemsg.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MsgDao extends ElasticsearchRepository<MsgES,Integer> {
}
