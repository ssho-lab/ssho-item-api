package webcrawler.shopping.swipe.service.collector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.domain.item.model.Item;
import webcrawler.shopping.swipe.service.crawling.mall.CrawlingService;
import webcrawler.shopping.swipe.service.crawling.mall.VivastudioCrawlingService;
import webcrawler.shopping.swipe.service.item.ItemServiceImpl;

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
    private final VivastudioCrawlingService vivastudioCrawlingService;

    private final ItemServiceImpl itemService;

    public CollectorServiceImpl(final List<CrawlingService> crawlingServiceList,
                                final ItemServiceImpl itemService,
                                final VivastudioCrawlingService vivastudioCrawlingService) {
        this.crawlingServiceList = crawlingServiceList;
        this.itemService = itemService;
        this.vivastudioCrawlingService = vivastudioCrawlingService;
    }

    /**
     * 전체 쇼핑몰 크롤링 데이터 수합 및 DB 업데이트
     *
     * @return List<Item>
     * @throws IOException
     */
    @Override
    public List<Item> updateAllMalls() throws IOException {

        List<Item> itemList = new ArrayList<>();

        // 개별 쇼핑몰 크롤링 서비스 전체 loop
        for (CrawlingService c : crawlingServiceList) {
            itemList.addAll(c.crawlAllProductsInAllCategory());
        }

        // ES 상품 인덱스 업데이트
        if (itemList.size() > 0) {
            // 누적 상품 인덱스 업데이트
            updateItemCumIndex(itemList);

            // 리얼타임 상품 인덱스 업데이트
            updateItemRtIndex(itemList);
        }
        return itemList;

    }

    /**
     * 전체 쇼핑몰 크롤링 데이터 수합 및 DB 업데이트
     *
     * @return List<Item>
     * @throws IOException
     */
    @Override
    public List<Item> updateMall() throws IOException {

        List<Item> itemList = new ArrayList<>();

        itemList.addAll(vivastudioCrawlingService.crawlAllProductsInAllCategory());

        // ES 상품 인덱스 업데이트
        if (itemList.size() > 0) {
            // 누적 상품 인덱스 업데이트
            updateItemCumIndex(itemList);

            // 리얼타임 상품 인덱스 업데이트
            updateItemRtIndex(itemList);
        }
        return itemList;

    }

    private void updateItemCumIndex(final List<Item> itemList) throws IOException {
        itemService.updateItemCum(itemList, "item-cum");
    }

    private void updateItemRtIndex(final List<Item> itemList) throws IOException {
        itemService.updateItemRt(itemList, "item-rt");
    }
}
