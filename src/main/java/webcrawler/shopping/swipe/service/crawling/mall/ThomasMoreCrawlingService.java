package webcrawler.shopping.swipe.service.crawling.mall;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.domain.item.model.Item;
import webcrawler.shopping.swipe.model.Selector;
import webcrawler.shopping.swipe.service.item.ItemServiceImpl;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ThomasMoreCrawlingService implements CrawlingService {

    private static String url = "https://thomasmore.co.kr/product/list.html?";
    private static String host = "http://thomasmore.co.kr/";
    private static HashMap<String, String> categoryMap = new HashMap<String, String>() {{
        put("T-SHIRTS", "25");
        put("SHIRTS", "26");
        put("SWEATERS", "27");
        put("PANTS", "28");
        put("OUTWEARS", "29");
        put("ACC", "36");
    }};

    private final ItemServiceImpl commonCrawlingService;

    public ThomasMoreCrawlingService(final ItemServiceImpl commonCrawlingService) {
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
                .topNode(new ArrayList<>(Arrays.asList(".grid4 > li")))
                .title(new ArrayList<>(Arrays.asList("div > p.name > a > span")))
                .price(new ArrayList<>(Arrays.asList("div > p.prices > span")))
                .imageUrl(new ArrayList<>(Arrays.asList("div > div.hoverimg > a > img")))
                .link(new ArrayList<>(Arrays.asList("div > div.hoverimg > a")))
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

            if(item.getPrice().contains(" ₩")){
                item.setPrice(item.getPrice().split("\\₩")[1]
                        .replace(" ", "")
                        .replace("₩", "")
                        .replace(",", ""));
            }
            else{
                item.setPrice(item.getPrice()
                        .replace(" ", "")
                        .replace(" ", "")
                        .replace("₩", "")
                        .replace(",", ""));
            }

            item.setImageUrl("https://" + item.getImageUrl().substring(2));

            item.setLink("https://www.thomasmore.co.kr" + item.getLink());

            item.setMallNo("0004");

            item.setId(item.getMallNo() + item.getLink().split("\\?")[1].split("&")[0].split("=")[1]);

            item.setMallNm("토마스모어");

            item.setCategory(category.getKey());

            // extra 필드 추가
            item.setProductExtra(commonCrawlingService.setExtraFields(selector, item.getLink(), host));
        }

        return itemList;
    }
}
