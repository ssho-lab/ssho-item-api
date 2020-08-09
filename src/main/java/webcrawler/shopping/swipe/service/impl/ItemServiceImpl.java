package webcrawler.shopping.swipe.service.impl;

import lombok.extern.slf4j.Slf4j;

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


    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    public ItemServiceImpl(final WebClient.Builder webClientBuilder,
                           final ElasticSearchClientServiceImpl elasticSearchClientService){
        this.webClient = webClientBuilder.baseUrl("http://13.124.59.2:8082").build();
        this.elasticSearchClientService = elasticSearchClientService;
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

        String description = elements.get(0).text();

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

    @Override
    public void updateAll(List<Item> itemList) {

    }

    /**
     *
     * @param crawlingApiAccessLog
     */
    @Override
    public void requestCrawlingApiAccessLogSave(final CrawlingApiAccessLog crawlingApiAccessLog, final int itemListSize){

        // crawling api call
        webClient
                .post().uri("/log/crawling-api")
                .bodyValue(crawlingApiAccessLog)
                .retrieve()
                .bodyToMono(CrawlingApiAccessLog.class)
                .block();

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
        return null;
    }

    /**
     * 20개 상품 랜덤 추출 (이미 본 상품 제외)
     * @return List<Item>
     */
    public List<Item> get20ItemsNotRevealed(final String userId){
        List<Item> itemList = elasticSearchClientService.searchItemList("item");

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

        return filteredItemList.subList(0,20);
    }

    /**
     * 상품 고유 번호 - 상품 이미지 포함 100개 상품 랜덤 추출
     * @return List<ItemIdImageUrlMap>
     */
    @Override
    public List<ItemIdImageUrlMap> get100ItemsIdImageUrlMap(){
        List<Item> itemList = elasticSearchClientService.searchItemList("item");
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
}
