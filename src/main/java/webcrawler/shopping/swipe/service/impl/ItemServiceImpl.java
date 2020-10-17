package webcrawler.shopping.swipe.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.concurrent.UnaryPromiseNotifier;
import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import webcrawler.shopping.swipe.domain.CrawlingApiAccessLog;
import webcrawler.shopping.swipe.domain.Item;
import webcrawler.shopping.swipe.domain.RealTag;
import webcrawler.shopping.swipe.domain.Tag;
import webcrawler.shopping.swipe.model.ItemIdImageUrlMap;
import webcrawler.shopping.swipe.model.ProductExtra;
import webcrawler.shopping.swipe.model.Selector;
import webcrawler.shopping.swipe.model.SlackMessage;
import webcrawler.shopping.swipe.model.log.SwipeLog;
import webcrawler.shopping.swipe.service.ItemService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 크롤링 공통 작업 Impl
 */
@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final WebClient webClient;
    private final ElasticSearchClientServiceImpl elasticSearchClientService;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    public ItemServiceImpl(final WebClient.Builder webClientBuilder,
                           final ElasticSearchClientServiceImpl elasticSearchClientService,
                           final RestHighLevelClient restHighLevelClient,
                           final ObjectMapper objectMapper){
        this.webClient = webClientBuilder.baseUrl("http://13.124.59.2:8082").build();
        this.elasticSearchClientService = elasticSearchClientService;
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    /**
     * HTML Parsing을 위한 최상위 Node Elements 탐색
     * @param pageNo
     * @param url
     * @param selectors
     * @return Elements
     * @throws IOException
     */
    @Override
    public Elements getTopNodeElements(final int pageNo, final String url, final List<String> selectors) throws IOException {

        // 상품 전체 리스트 페이지를 최초로 Jsoup을 통해 연결
        Document doc = Jsoup.connect(url + pageNo).get();

        Elements elements = doc.select(selectors.get(0));

        if (selectors.size() == 1) return elements;

        // Selector가 2개 이상일 때는 Chaining 진행
        else {
            for (int i = 1; i < selectors.size(); i++) {
                elements = elements.select(selectors.get(i));
            }
            return elements;
        }
    }

    /**
     * 상품 공통 필드 정보 크롤링
     * 제목, 판매 가격, 이미지 URL, 상세 페이지 Link
     * @param elements
     * @param selector
     * @return List<Item>
     */
    @Override
    public List<Item> getItemListWithCommonFields(final Elements elements, final Selector selector){

        List<Item> itemList = new ArrayList<>();

        for (int i = 0; i < elements.size(); i++) {

            Element element = elements.get(i).clone();

            Elements temp = new Elements();

            // 제목(Title)
            for (String s : selector.getTitle()) {
                temp = element.select(s);
            }
            String title = temp.text();

            // 판매 가격(Price)
            for (String s : selector.getPrice()) {
                temp = element.select(s);
            }

            String price = temp.text();

            // 이미지 Url(ImageUrl)
            for (String s : selector.getImageUrl()) {
                temp = element.select(s);
            }
            String imageUrl = temp.attr("src");

            // 상세 페이지 Link(Link)
            for (String s : selector.getLink()) {
                temp = element.select(s);
            }
            String link = temp.attr("href");

            Item item =
                    Item.builder()
                            .title(title)
                            .price(price)
                            .imageUrl(imageUrl)
                            .link(link)
                            .build();

            itemList.add(item);
        }
        return itemList;
    }

    /**
     * 상품 상세 필드 정보 크롤링
     * 제목, 판매 가격, 이미지 URL, 상세 페이지 Link
     * @param selector
     * @param url
     * @param host
     * @return ProductExtra
     * @throws IOException
     */
    @Override
    public ProductExtra setExtraFields(final Selector selector, final String url, final String host) throws IOException {

        // 상품 상세 페이지를 최초로 Jsoup을 통해 연결
        Document doc = Jsoup.connect(url).get();

        Elements elements;

        // 상세 이미지 리스트(ExtraImageList)
        elements = doc.select(selector.getExtraImageUrl().get(0));

        List<String> extraImageUrlList = new ArrayList<>();
        for(int i = 1; i < selector.getExtraImageUrl().size(); i++){
            elements = elements.select(selector.getExtraImageUrl().get(i));
        }

        for(Element e : elements){
            String imageUrl = host + e.attr("src");
            extraImageUrlList.add(imageUrl);
        }

        // 상품 설명(Description)
        elements = doc.select(selector.getDescription().get(0));

        for(int i = 1; i < selector.getDescription().size(); i++){
            elements = elements.select(selector.getDescription().get(i));
        }

        String description = elements.size() == 0 ? "" : elements.get(0).text();

        // 상품 사이즈 리스트(SizeList)
        List<String> sizeList = new ArrayList<>();
        elements = doc.select(selector.getSize().get(0));

        for(int i = 1; i < selector.getSize().size(); i++){
            elements = elements.select(selector.getSize().get(i));
        }

        for(Element e : elements){
            String size = e.text();
            if (size.equals("")) continue;
            if (size.equals("FRE") || size.equals("fre")
                    || size.equals("free") || size.equals("Free")) size = "FREE";

            sizeList.add(size);
        }

        ProductExtra productExtra =
                ProductExtra.builder().extraImageUrlList(extraImageUrlList)
                        .description(description)
                        .sizeList(sizeList).build();

        return productExtra;
    }

    public void updateItemRt(final List<Item> itemList, final String index) throws IOException {
        if(elasticSearchClientService.indexExist(index)){
            deleteItemList(index);
        }
        saveItemList(itemList, index);
    }

    public void updateItemCum(final List<Item> itemList, final String index) throws IOException {

        if(elasticSearchClientService.indexExist(index)){
            for (Item item : itemList) {
                // 누적 상품 인덱스에 없는 상품을 경우 추가
                if (elasticSearchClientService.searchItemById(index, item.getId()).equals(new Item())){
                    saveItem(item, index);
                }
            }
        }

        else {
            for (Item item : itemList) {
                saveItem(item, index);
            }
        }
    }

    private void saveItem(final Item item, final String index) throws IOException {

        // docId 생성
        final String docId = item.getId();

        try {
            restHighLevelClient.index(new IndexRequest(index)
                    .id(docId)
                    .source(objectMapper.writeValueAsString(item), XContentType.JSON), RequestOptions.DEFAULT);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void saveItemList(final List<Item> itemList, final String index) throws IOException {

        final BulkRequest bulkRequest = new BulkRequest();

        itemList.stream().forEach(item -> {

            // docId 생성
            final String docId = item.getId();

            IndexRequest indexRequest = null;

            try {
                indexRequest = new IndexRequest(index)
                        .id(docId)
                        .source(objectMapper.writeValueAsString(item), XContentType.JSON);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            bulkRequest.add(indexRequest);
        });

        // bulk 요청
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    private void deleteItemList(final String index) throws IOException{

        GetIndexRequest request = new GetIndexRequest("item");

        if(restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT)){
            restHighLevelClient.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
        }
    }

    /**
     *
     * @param crawlingApiAccessLog
     */
    @Override
    public void requestCrawlingApiAccessLogSave(final CrawlingApiAccessLog crawlingApiAccessLog, final int itemListSize){

        // slack webhook call
        SlackMessage slackMessage = new SlackMessage();

        String slackText = crawlingApiAccessLog.getStatusCode() == 201 ?
                "성공 | " + "업데이트 된 상품 수 : " + itemListSize : "실패";

        slackMessage.setText(slackText);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.postForEntity(slackWebhookUrl, slackMessage, String.class);
    }

    @Override
    public List<Item> get100Items() {
        return elasticSearchClientService.searchItemList("item", 100);
    }

    public List<Item> getItems(){

        List<Item> itemList = elasticSearchClientService.searchItemList("item-rt", 10000);
        Collections.sort(itemList);
        return itemList;
    }

    public List<Item> get20Items(){ return elasticSearchClientService.searchRandomItemList("item", 20); }

    /**
     * 20개 상품 랜덤 추출 (이미 본 상품 제외)
     * @return List<Item>
     */
    public List<Item> get20ItemsNotRevealed(final String userId){
        List<Item> itemList = elasticSearchClientService.searchItemList("item", 1000);

        List<String> userItemIdList =
                webClient
                .get().uri("/log/swipe/user?userId={userId}", userId)
                .retrieve()
                        .bodyToFlux(SwipeLog.class)
                        .map(SwipeLog::getItemId)
                        .collectList()
                        .block();

        List<Item> filteredItemList = itemList.stream().filter(item -> !userItemIdList.contains(item.getId())).collect(Collectors.toList());
        Collections.shuffle(filteredItemList);

        return filteredItemList.size() >= 20 ?
                filteredItemList.subList(0,20) : filteredItemList.subList(0,filteredItemList.size());
    }

    /**
     * 상품 고유 번호 - 상품 이미지 포함 100개 상품 랜덤 추출
     * @return List<ItemIdImageUrlMap>
     */
    @Override
    public List<ItemIdImageUrlMap> get100ItemsIdImageUrlMap(){
        List<Item> itemList = elasticSearchClientService.searchItemList("item", 1000);
        Collections.shuffle(itemList);
        List<ItemIdImageUrlMap> itemIdImageUrlMapList = new ArrayList<>();

        itemList.forEach(i ->itemIdImageUrlMapList.add(ItemIdImageUrlMap.builder()
                .id(i.getId())
                .imageUrl(i.getImageUrl())
                .build()));

        return itemIdImageUrlMapList;
    }

    /**
     * 회원별 좋아요 한 상품 조회
     * @return List<Item>
     */
    public List<List<Item>> getLikeItemsByUserId(final String userId){

        List<List<Item>> likedItemsList = new ArrayList<>();

        final Map<Integer, List<SwipeLog>> groupedSwipeLogList =
                webClient
                        .get().uri("/log/swipe/user/like/grouped?userId={userId}", userId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<Integer, List<SwipeLog>>>() {})
                        .block();

        // 스와이프 로그 이력이 없을 경우
        if(groupedSwipeLogList.size() == 0) {
            return likedItemsList;
        }

        for(Map.Entry<Integer, List<SwipeLog>> entry : groupedSwipeLogList.entrySet()){

            List<SwipeLog> swipeLogList = entry.getValue();

            List<Item> itemList =
                    swipeLogList
                        .stream()
                        .map(swipeLog -> {
                            try {
                                return elasticSearchClientService.searchItemById("item", swipeLog.getItemId());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .collect(Collectors.toList());

            likedItemsList.add(itemList);
        }

        return likedItemsList;
    }

    @Override
    public void deleteTag(final Tag tag, final String itemId) throws IOException {

        UpdateRequest updateRequest = new UpdateRequest("item-rt", itemId);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("expTagId", tag.getExpTag().getId());
        Script script = new Script(ScriptType.STORED, null, "delete-tag-script", parameters);
        updateRequest.script(script);

        restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
    }

    private boolean compareTag(final Tag prevTag, final Tag postTag){
        if(prevTag.getRealTagList().size() != postTag.getRealTagList().size()){
            return false;
        }
        if(!prevTag.getExpTag().equals(postTag.getExpTag())) {
            return false;
        }

        List<RealTag> prevRealTagList = prevTag.getRealTagList();
        List<RealTag> postRealTagList = postTag.getRealTagList();

        for(int i = 0; i < prevRealTagList.size(); i++){
            if(!prevRealTagList.get(i).equals(postRealTagList.get(i))){
                return false;
            }
        }
        return true;
    }
}
