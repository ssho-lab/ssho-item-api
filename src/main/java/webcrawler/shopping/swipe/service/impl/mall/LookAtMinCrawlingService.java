package webcrawler.shopping.swipe.service.impl.mall;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.domain.Item;
import webcrawler.shopping.swipe.model.Selector;
import webcrawler.shopping.swipe.service.CrawlingService;
import webcrawler.shopping.swipe.service.impl.ItemServiceImpl;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class LookAtMinCrawlingService implements CrawlingService {

    private static String url = "http://lookatmin.com/product/list.html?";
    private static String host = "http://lookatmin.com/";
    private static HashMap<String, String> categoryMap = new HashMap<String, String>() {{
        put("OUTER", "42");
        put("TEE/TOP", "43");
        put("DRESS", "44");
        put("BOTTOM", "45");
        put("TRAINING", "60");
        put("SWIM WEAR", "57");
        put("SHOES", "46");
        put("BAG", "48");
        put("ACCESSORY", "47");
        put("HAT", "56");
    }};

    private final ItemServiceImpl commonCrawlingService;

    public LookAtMinCrawlingService(final ItemServiceImpl commonCrawlingService) {
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

        for (Map.Entry<String, String> c : categoryMap.entrySet()) {
            for (int pageNo = 0;; pageNo++) {
                List<Item> productList = crawlAllProductsInOneCategory(pageNo, c);
                if (productList.size() == 0) break;
                allProductList.addAll(productList);
            }
        }

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

        String fullUrl = url + "cate_no=" + category.getValue() + "&page=" + pageNo;

        // 크롤링시 사용할 Selector 객체 생성
        Selector selector = Selector.builder()
                .topNode(new ArrayList<>(Arrays.asList(".prdList > li")))
                .title(new ArrayList<>(Arrays.asList(".description > strong", "a > span:eq(1)")))
                .price(new ArrayList<>(Arrays.asList(".description > ul > li:last-child > span")))
                .imageUrl(new ArrayList<>(Arrays.asList(".thumbnail > div > a > img")))
                .link(new ArrayList<>(Arrays.asList(".thumbnail > div > a")))
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

            String priceStr = item.getPrice().replace("원 ", "");

            priceStr = priceStr.replace(",", "");

            item.setPrice(priceStr);

            item.setImageUrl("https://" + item.getImageUrl().substring(2));

            item.setLink("https://www.lookatmin.com" + item.getLink());

            item.setMallNo("0003");

            item.setId(item.getMallNo() + item.getLink().split("/")[5]);

            item.setMallNm("룩앳민");

            item.setCategory(category.getKey());

            // extra 필드 추가
            item.setProductExtra(commonCrawlingService.setExtraFields(selector, item.getLink(), host));
        }
        return itemList;
    }
}
