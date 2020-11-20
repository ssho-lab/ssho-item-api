package webcrawler.shopping.swipe.service.crawling.mall;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.domain.item.model.Item;
import webcrawler.shopping.swipe.model.Selector;
import webcrawler.shopping.swipe.service.item.ItemServiceImpl;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class CustomMellowCrawlingService implements CrawlingService {

    private static String url = "https://www.kolonmall.com/CUSTOMELLOW/List/";
    private static String host = "https://www.kolonmall.com/";
    private static HashMap<String, String> categoryMap = new HashMap<String, String>() {{
        put("OUTER", "4972");
        put("TOP", "4973");
        put("BOTTOM", "4974");
        put("SUIT", "4971");
        put("ACCESSORIES", "4975");
    }};

    private final ItemServiceImpl commonCrawlingService;

    public CustomMellowCrawlingService(final ItemServiceImpl commonCrawlingService) {
        this.commonCrawlingService = commonCrawlingService;
    }

    /**
     * 모든 카테고리 상품 크롤링
     *
     * @return List<Item>
     * @throws IOException
     */
    @Override
    public List<Item> crawlAllProductsInAllCategory() throws IOException {

        List<Item> allProductList = new ArrayList<>();

        /*
        for (Map.Entry<String, String> c : categoryMap.entrySet()) {
            for (int pageNo = 0;; pageNo++) {
                List<Item> productList = crawlAllProductsInOneCategory(pageNo, c);
                if (productList.size() == 0) break;
                allProductList.addAll(productList);
            }
        }

         */

        /*
        for (Map.Entry<String, String> c : categoryMap.entrySet()) {
            for (int pageNo = 0; pageNo < 1; pageNo++) {
                List<Item> productList = crawlAllProductsInOneCategory(pageNo, c);
                if (productList.size() == 0) break;
                allProductList.addAll(productList);
            }
            break;
        }

         */

        return allProductList;
    }

    /**
     * 카테고리, 페이지 내의 상품 크롤링
     *
     * @param pageNo
     * @param category
     * @return List<Item>
     * @throws IOException
     */
    @Override
    public List<Item> crawlAllProductsInOneCategory(final int pageNo, final Map.Entry<String, String> category) throws IOException {

        String fullUrl = url + category.getValue() + "?page=" + pageNo;

        // 크롤링시 사용할 Selector 객체 생성
        Selector selector = Selector.builder()
                .topNode(new ArrayList<>(Arrays.asList(".cHbJxO > div")))
                .title(new ArrayList<>(Arrays.asList("a .dnwUOg")))
                .price(new ArrayList<>(Arrays.asList("a .djyfFL")))
                .imageUrl(new ArrayList<>())
                .link(new ArrayList<>(Arrays.asList("a")))
                .extraImageUrl(new ArrayList<>(Arrays.asList(".d_proimage img")))
                .description(new ArrayList<>(Arrays.asList("#detailArea", ".cont > p")))
                .size(new ArrayList<>(Arrays.asList(".ec-product-disabled", "span")))
                .build();

        List<Item> itemList = new ArrayList<>();

        // 최상위 Elements 추출
        Elements elements = commonCrawlingService.getTopNodeElements(pageNo, fullUrl, selector.getTopNode());

        if (elements.size() == 0) return itemList;

        // 공통 필드 추가
        itemList = commonCrawlingService.getItemListWithCommonFields(elements, selector);

        // 필드 로컬 작업
        for (Item item : itemList) {

            item.setPrice(item.getPrice().replace("원", "").replace(",", ""));

            item.setLink("https://www.kolonmall.com" + item.getLink());

            item.setMallNm("커스텀멜로우");

            item.setMallNo("0005");

            item.setId(item.getMallNo() + item.getLink().split("\\/")[4].split("\\?")[0]);

            item.setCategory(category.getKey());

            Document doc = Jsoup.connect(item.getLink()).get();
            elements = doc.select(".box-img");

            WebDriver driver = new FirefoxDriver();
            driver.get("https://www.google.com");
            System.out.println(driver.getPageSource());

            // extra 필드 추가
            item.setProductExtra(commonCrawlingService.setExtraFields(selector, item.getLink(), host));
        }

        return itemList;
    }
}

