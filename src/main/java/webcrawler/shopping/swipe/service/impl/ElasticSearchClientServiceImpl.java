package webcrawler.shopping.swipe.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.domain.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticSearchClientServiceImpl {

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    public ElasticSearchClientServiceImpl(final RestHighLevelClient restHighLevelClient,
                                          final ObjectMapper objectMapper){
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    public List<Item> searchItemList(final String index, final int size){
        // ES에 요청 보내기
        SearchRequest searchRequest = new SearchRequest(index);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // search query 최대 크기 set
        sourceBuilder.size(size);

        searchRequest.source(sourceBuilder);

        // ES로 부터 데이터 받기
        SearchResponse searchResponse;

        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        SearchHit[] searchHits = searchResponse.getHits().getHits();

        List<Item> itemList = Arrays.stream(searchHits).map(hit -> {
            try {
                return new ObjectMapper()
                        .readValue(hit.getSourceAsString(), Item.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        return itemList;
    }

    public Item searchItemById(final String index, String id) throws IOException {
        // ES에 요청 보내기
        SearchRequest searchRequest = new SearchRequest(index);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        QueryBuilder queryBuilder = QueryBuilders.termQuery("id", id);

        // search query 최대 크기 set
        sourceBuilder.size(1000);
        sourceBuilder.query(queryBuilder);

        searchRequest.source(sourceBuilder);

        // ES로 부터 데이터 받기
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHit[] searchHits = searchResponse.getHits().getHits();
        SearchHit hit = searchHits[0];

        Item item = objectMapper.readValue(hit.getSourceAsString(), Item.class);

        return item;
    }
}
