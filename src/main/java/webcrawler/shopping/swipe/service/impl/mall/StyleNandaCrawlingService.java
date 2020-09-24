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
public class StyleNandaCrawlingService implements CrawlingService {

    private static String url = "https://www.stylenanda.com/product/list.html?";
    private static String host = "https://www.stylenanda.com/";
    private static HashMap<String,String> categoryMap = new HashMap<String, String>(){{
        put("아우터", "51");
        put("탑", "50");
        put("드레스", "54");
        put("스커트", "52");
        put("팬츠", "53");
        put("가방", "56");
        put("슈즈", "77");
        put("악세사리", "55");
    }};

    private final ItemServiceImpl commonCrawlingService;

    public StyleNandaCrawlingService(final ItemServiceImpl commonCrawlingService){
        this.commonCrawlingService = commonCrawlingService;
    }

    /**
     * 모든 카테고리 상품 크롤링
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
                .topNode(new ArrayList<>(Arrays.asList(".column4 li")))
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
        Elements elements = commonCrawlingService.getTopNodeElements(pageNo, fullUrl, selector.getTopNode());

        if(elements.size() == 0) return itemList;

        // 공통 필드 추가
        itemList = commonCrawlingService.getItemListWithCommonFields(elements, selector);

        // 필드 로컬 작업
        for (Item item : itemList) {
            String titleStr = item.getTitle().substring(4);
            titleStr = titleStr.split("#")[0];
            titleStr = titleStr.substring(0, titleStr.length() -1);
            item.setTitle(titleStr);

            String priceStr = item.getPrice().replace("원 →", "");

            priceStr = (priceStr.contains("원") ? priceStr.split(" ")[1]
                    .replace("원", "").replace(",", "")
                    : priceStr.replace(",", ""));

            priceStr = priceStr.replace(" ", "");

            item.setPrice(priceStr);

            //item.setImageUrl("https://" + item.getImageUrl().substring(2));

            item.setLink("https://www.stylenanda.com" + item.getLink());

            item.setMallNo("0001");

            item.setId(item.getMallNo() + item.getLink().split("\\?")[1].split("&")[0].split("=")[1]);

            item.setMallNm("스타일난다");

            item.setCategory(category.getKey());

            // extra 필드 추가
            item.setProductExtra(commonCrawlingService.setExtraFields(selector, item.getLink(), host));
        }

        return itemList;

    }
}
