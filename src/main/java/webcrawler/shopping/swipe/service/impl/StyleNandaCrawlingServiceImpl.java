package webcrawler.shopping.swipe.service.impl;

import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.Item;
import webcrawler.shopping.swipe.model.Selector;
import webcrawler.shopping.swipe.service.CrawlingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StyleNandaCrawlingServiceImpl implements CrawlingService {

    private static String url = "https://www.stylenanda.com/product/list.html?cate_no=1902&page=";
    private static String host = "https://www.stylenanda.com/";

    private final CommonCrawlingServiceImpl commonCrawlingService;

    public StyleNandaCrawlingServiceImpl(final CommonCrawlingServiceImpl commonCrawlingService){
        this.commonCrawlingService = commonCrawlingService;
    }

    @Override
    public List<Item> crawlAllProductsInSinglePage(final int pageNo) throws IOException {

        // 크롤링시 사용할 Selector 객체 생성
        Selector selector = Selector.builder()
                .commonSelector(new ArrayList<>(Arrays.asList(".column4 li")))
                //.extraSelector(new ArrayList<>(Arrays.asList(".d_proimage img")))
                .title(new ArrayList<>(Arrays.asList(".name span")))
                .price(new ArrayList<>(Arrays.asList(".price", "p")))
                .imageUrl(new ArrayList<>(Arrays.asList(".box", "a", "img")))
                .link(new ArrayList<>(Arrays.asList(".box", "a")))
                .extraImageUrl(new ArrayList<>(Arrays.asList(".d_proimage img")))
                .description(new ArrayList<>(Arrays.asList(".explain div", ".cont")))
                .size(new ArrayList<>(Arrays.asList(".ec-product-disabled", "span")))
                .build();

        List<Item> itemList = new ArrayList<>();

        // 최상위 Elements 추출
        Elements elements = commonCrawlingService.getTopElements(pageNo, url, selector.getCommonSelector());

        if(elements.size() == 0) return itemList;

        // 공통 필드 추가
        itemList = commonCrawlingService.getItemListWithCommonFields(elements, selector);

        // 필드 로컬 작업
        for (Item item : itemList) {
            item.setTitle(item.getTitle().substring(4));

            item.setPrice(item.getPrice().replace("원 →", ""));
            item.setPrice((item.getPrice().contains("원") ? item.getPrice().split(" ")[1]
                    .replace("원", "").replace(",", "")
                    : item.getPrice().replace(",", "")));

            item.setImageUrl("https://" + item.getImageUrl().substring(2));

            item.setLink("https://www.stylenanda.com" + item.getLink());

            item.setMallNo("0001");

            item.setId(item.getMallNo() + item.getLink().split("\\?")[1].split("&")[0].split("=")[1]);

            item.setMallNm("스타일난다");

            // extra 필드 추가
            item.setProductExtra(commonCrawlingService.setExtraFields(item.getLink(), item, selector, host, 0));
        }

        return itemList;

    }
}
