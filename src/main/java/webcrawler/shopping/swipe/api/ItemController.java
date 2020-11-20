package webcrawler.shopping.swipe.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import webcrawler.shopping.swipe.domain.CrawlingApiAccessLog;
import webcrawler.shopping.swipe.domain.item.model.Item;
import webcrawler.shopping.swipe.domain.tag.model.Tag;
import webcrawler.shopping.swipe.service.collector.CollectorServiceImpl;
import webcrawler.shopping.swipe.service.item.ItemServiceImpl;
import webcrawler.shopping.swipe.util.auth.Auth;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    //@Scheduled(cron = "0 0 */6 * * *")
    @GetMapping("/update/all")
    public List<Item> updateAllMallItems(){

        CrawlingApiAccessLog crawlingApiAccessLog =
                CrawlingApiAccessLog.builder()
                        .path("/item")
                        .accessTime(LocalDateTime.now())
                        .build();

        try {

            List<Item> itemList = collectorService.updateAllMalls();

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

    @GetMapping("/update/one")
    public List<Item> updateOneMallItems(){

        CrawlingApiAccessLog crawlingApiAccessLog =
                CrawlingApiAccessLog.builder()
                        .path("/item")
                        .accessTime(LocalDateTime.now())
                        .build();

        try {

            List<Item> itemList = collectorService.updateMall();

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

    /**
     * 상품 태그 업데이트
     * @param tagList
     * @param itemId
     * @throws IOException
     */
    @PostMapping("/update/tag")
    public void updateTag(@RequestBody List<Tag> tagList, @RequestParam("itemId") final String itemId) throws IOException {
        itemService.updateTagList(tagList, itemId);
    }

    /**
     * 상품 전체 조회
     * @return
     */
    @GetMapping("")
    public List<Item> getItemList(){
        return itemService.getItems();
    }

    @GetMapping("/initial")
    public List<Item> getInitialItemList() {
        return itemService.get20Items();
    }
}
