package webcrawler.shopping.swipe.api;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import webcrawler.shopping.swipe.domain.Item;
import webcrawler.shopping.swipe.model.ItemIdImageUrlMap;
import webcrawler.shopping.swipe.service.impl.CommonCrawlingServiceImpl;
import webcrawler.shopping.swipe.service.impl.StyleNandaCrawlingServiceImpl;
import webcrawler.shopping.swipe.service.impl.VivastudioCrawlingServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/item")
public class CrawlingController {

    private final StyleNandaCrawlingServiceImpl styleNandaCrawlingService;
    private final VivastudioCrawlingServiceImpl vivastudioCrawlingService;
    private final CommonCrawlingServiceImpl commonCrawlingService;

    public CrawlingController(final StyleNandaCrawlingServiceImpl styleNandaCrawlingService,
                              final VivastudioCrawlingServiceImpl vivastudioCrawlingService,
                              final CommonCrawlingServiceImpl commonCrawlingService){
        this.styleNandaCrawlingService = styleNandaCrawlingService;
        this.vivastudioCrawlingService = vivastudioCrawlingService;
        this.commonCrawlingService = commonCrawlingService;
    }

    /**
     * 전체 쇼핑몰 데이터 크롤링 + DB 업데이트
     * 1시간에 한 번으로 스케쥴링
     * @throws IOException
     */
    @Scheduled(cron = "0 */1 * * * *")
    public void updateItems() throws IOException {
        List<Item> allProductList = new ArrayList<>();
        allProductList.addAll(styleNandaCrawlingService.crawlAllProducts());
        allProductList.addAll(vivastudioCrawlingService.crawlAllProducts());
        commonCrawlingService.updateAll(allProductList);
    }

    /**
     * 카드덱 전체 상품 조회 (100개)
     * @return List<Item>
     */
    @GetMapping
    public List<Item> getItemsForCardDeck(){
        return commonCrawlingService.get100Items();
    }

    /**
     * 카드덱 전체 상품(id-imageUrl) 조회 (100개)
     * @return List<ItemIdImageUrlMap>
     */
    @GetMapping("/image")
    public List<ItemIdImageUrlMap> getItemsIdImageUrlForCardDeck(){
        return commonCrawlingService.get100ItemsIdImageUrlMap();
    }
}
