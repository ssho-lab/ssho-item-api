package webcrawler.shopping.swipe.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.domain.Item;
import webcrawler.shopping.swipe.service.CollectorService;
import webcrawler.shopping.swipe.service.CrawlingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  개별 쇼핑몰 크롤링 데이터 수합 작업 Impl
 */
@Slf4j
@Service
public class CollectorServiceImpl implements CollectorService {

    // CrawlingService 타입을 가지는 모든 Bean 주입
    private final List<CrawlingService> crawlingServiceList;

    private final ItemServiceImpl commonCrawlingService;

    public CollectorServiceImpl(final List<CrawlingService> crawlingServiceList,
                                final ItemServiceImpl commonCrawlingService){
        this.crawlingServiceList = crawlingServiceList;
        this.commonCrawlingService = commonCrawlingService;
    }

    /**
     * 전체 쇼핑몰 크롤링 데이터 수합 및 DB 업데이트
     * @return List<Item>
     * @throws IOException
     */
    @Override
    public List<Item> collectAndUpdateAllItems() throws IOException {

        List<Item> itemList = new ArrayList<>();

        // 개별 쇼핑몰 크롤링 impl 전체 loop
        for (CrawlingService c : crawlingServiceList) {
            itemList.addAll(c.crawlAllProductsInAllCategory());
        }

        // DB 업데이트
        commonCrawlingService.updateAll(itemList);

        return itemList;
    }
}
