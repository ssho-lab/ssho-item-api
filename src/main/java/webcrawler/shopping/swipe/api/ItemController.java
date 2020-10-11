package webcrawler.shopping.swipe.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webcrawler.shopping.swipe.domain.CrawlingApiAccessLog;
import webcrawler.shopping.swipe.domain.Item;
import webcrawler.shopping.swipe.service.impl.CollectorServiceImpl;
import webcrawler.shopping.swipe.service.impl.ItemServiceImpl;
import webcrawler.shopping.swipe.util.auth.Auth;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 크롤링 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemServiceImpl itemService;
    private final CollectorServiceImpl collectorService;

    public ItemController(ItemServiceImpl itemService,
                          CollectorServiceImpl collectorService){
        this.itemService = itemService;
        this.collectorService = collectorService;
    }

    /**
     * 전체 쇼핑몰 데이터 크롤링 + DB 업데이트
     * 6시간에 한 번으로 스케쥴링
     * @throws IOException
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public List<Item> updateItems(){

        CrawlingApiAccessLog crawlingApiAccessLog =
                CrawlingApiAccessLog.builder()
                        .path("/item")
                        .accessTime(LocalDateTime.now())
                        .build();

        try {

            List<Item> itemList = collectorService.collectAndUpdateAllItems();

            crawlingApiAccessLog.setStatusCode(201);

            itemService.requestCrawlingApiAccessLogSave(crawlingApiAccessLog, itemList.size());

            return itemList;
        }
        catch (Exception e){

            crawlingApiAccessLog.setStatusCode(400);

            itemService.requestCrawlingApiAccessLogSave(crawlingApiAccessLog, 0);

            return new ArrayList<>();
        }
    }

    @GetMapping("")
    public List<Item> getItemList(){
        return itemService.getItems();
    }

    @GetMapping("/initial")
    public List<Item> getInitialItemList() {
        return itemService.get20Items();
    }

    /**
     * 회원별 좋아요한 상품 조회
     * @return List<Item>
     */
    @Auth
    @GetMapping("/shopping-bag")
    public List<List<Item>> getLikeItemsByUserId(final HttpServletRequest httpServletRequest){
        final String userId = String.valueOf(httpServletRequest.getAttribute("userId"));
        return itemService.getLikeItemsByUserId(userId);
    }
}
