package online.fadai.storemsg.controller;

import jakarta.annotation.Resource;
import online.fadai.storemsg.config.EsClient;
import online.fadai.storemsg.config.EsClintToPoolEvent;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller("/storeMsg")
public class StoreMsgController {
    @Resource
    private EsClient esClient;
    @Resource
    private ApplicationContext applicationContext;

    @GetMapping("/get")
    public List<String> get(String sender, String receiver, long beginTime, long endTime) throws IOException {
        RestHighLevelClient client = null;
        long time = System.currentTimeMillis() + 10;
        while (client == null) {
            client = esClient.getClient();
            if (time > System.currentTimeMillis()) {
                throw new RuntimeException("系统繁忙");
            }
        }
        try {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices("msg");
            // 构建发送者查询
            BoolQueryBuilder senderQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("sender", sender));

            // 构建接收者查询
            BoolQueryBuilder receiverQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("receiver", receiver));

            // 构建时间范围查询
            BoolQueryBuilder timeQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.rangeQuery("date")
                            .gte(beginTime) // 大于等于开始时间
                            .lte(endTime)); // 小于等于结束时间

            // 结合所有查询条件
            BoolQueryBuilder finalQuery = QueryBuilders.boolQuery()
                    .must(senderQuery)
                    .must(receiverQuery)
                    .must(timeQuery);
            SearchSourceBuilder query = new SearchSourceBuilder().query(finalQuery);
            searchRequest.source(query);
            SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = search.getHits();
            ArrayList<String> hitList = new ArrayList<>();
            hits.forEach(i -> {
                hitList.add(i.getSourceAsString());
            });
            return hitList;
        } finally {
            applicationContext.publishEvent(new EsClintToPoolEvent(client));
        }
    }
}
