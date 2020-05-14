package webcrawler.shopping.swipe.api;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webcrawler.shopping.swipe.Item;
import webcrawler.shopping.swipe.service.impl.CommonCrawlingServiceImpl;
import webcrawler.shopping.swipe.service.impl.StyleNandaCrawlingServiceImpl;
import webcrawler.shopping.swipe.service.impl.VivastudioCrawlingServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    // 1시간에 한 번으로 스케쥴링
    @Scheduled(cron = "0 */1 * * *")
    public void updateItems() throws IOException {

        List<Item> allProductList = new ArrayList<>();

        for(int i = 1; i < 2; i++) {
            List<Item> productList = styleNandaCrawlingService.crawlAllProductsInSinglePage(i);
            if(productList.size() == 0) break;
            allProductList.addAll(productList);
        }

        for(int i = 1; i < 2; i++) {
            List<Item> productList = vivastudioCrawlingService.crawlAllProductsInSinglePage(i);
            if(productList.size() == 0) break;
            allProductList.addAll(productList);
        }
        commonCrawlingService.updateAll(allProductList);
    }

    @GetMapping
    public List<Item> getItemsForCardDeck(){
        return commonCrawlingService.get100Items();
    }
}
