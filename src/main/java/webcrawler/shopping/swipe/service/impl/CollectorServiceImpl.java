package webcrawler.shopping.swipe.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import webcrawler.shopping.swipe.domain.ExpTag;
import webcrawler.shopping.swipe.domain.Item;
import webcrawler.shopping.swipe.domain.RealTag;
import webcrawler.shopping.swipe.service.CollectorService;
import webcrawler.shopping.swipe.service.CrawlingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 개별 쇼핑몰 크롤링 데이터 수합 작업 Impl
 */
@Slf4j
@Service
public class CollectorServiceImpl implements CollectorService {

    // CrawlingService 타입을 가지는 모든 Bean 주입
    private final List<CrawlingService> crawlingServiceList;

    private final ItemServiceImpl itemService;

    private WebClient webClient;

    public CollectorServiceImpl(final List<CrawlingService> crawlingServiceList,
                                final ItemServiceImpl itemService,
                                final WebClient.Builder webClientBuilder) {
        this.crawlingServiceList = crawlingServiceList;
        this.itemService = itemService;
        this.webClient = webClientBuilder.baseUrl("http://13.125.68.140:8083").build();

    }

    /**
     * 전체 쇼핑몰 크롤링 데이터 수합 및 DB 업데이트
     *
     * @return List<Item>
     * @throws IOException
     */
    @Override
    public List<Item> collectAndUpdateAllItems() throws IOException {

        List<Item> itemList = new ArrayList<>();

        // 개별 쇼핑몰 크롤링 서비스 전체 loop
        for (CrawlingService c : crawlingServiceList) {
            itemList.addAll(c.crawlAllProductsInAllCategory());
        }

        try{

            // 태그 랜덤덤
           List<RealTag> tagList =
                    webClient
                            .get().uri("/tag/real")
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<List<RealTag>>(){})
                            .block();

            itemList.stream().forEach(item -> {

                List<RealTag> realTagList = new ArrayList<>();
                List<ExpTag> expTagList = new ArrayList<>();

                for(int i = 0; i < 2; i++){
                    RealTag realTag = tagList.get((int)(Math.random() * tagList.size()));
                    ExpTag expTag = ExpTag.builder().id(realTag.getId()).name(realTag.getName()).build();

                    if(realTagList.contains(realTag)) continue;

                    realTagList.add(realTag);
                    expTagList.add(expTag);
                }

                item.setRealTagList(realTagList);
                item.setExpTagList(expTagList);
            });

            // ES 상품 인덱스 업데이트
            if(itemList.size() > 0) {
                // 누적 상품 인덱스 업데이트
                updateItemCumIndex(itemList);

                // 리얼타임 상품 인덱스 업데이트
                updateItemRtIndex(itemList);
            }
            return itemList;
        }
        catch (ElasticsearchStatusException e){
            return itemList;
        }
    }

    private void updateItemCumIndex(final List<Item> itemList) throws IOException {
        itemService.updateItemCum(itemList, "item-cum");
    }

    private void updateItemRtIndex(final List<Item> itemList) throws IOException {
        itemService.updateItemRt(itemList, "item-rt");
    }
}
