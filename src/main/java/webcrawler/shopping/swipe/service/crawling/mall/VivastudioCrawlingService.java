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
public class VivastudioCrawlingService implements CrawlingService {

    private static String url = "http://vivastudio.co.kr/product/list.html?";
    private static String host = "http://vivastudio.co.kr/";

    private static HashMap<String,String> categoryMap = new HashMap<String,String>(){{
        put("아우터", "34");
        put("가죽", "73");
        put("스웨트셔츠", "114");
        put("티셔츠", "116");
        put("셔츠", "117");
        put("니트", "115");
        put("하의", "36");
        put("악세사리", "37");
    }};

    private final ItemServiceImpl commonCrawlingService;

    public VivastudioCrawlingService(final ItemServiceImpl commonCrawlingService){
        this.commonCrawlingService = commonCrawlingService;
    }

    /**
     * @return List<Item>
     * @throws IOException
     */
    @Override
    public List<Item> crawlAllProductsInAllCategory() throws IOException {

        List<Item> allProductList = new ArrayList<>();

        for (Map.Entry<String, String> c : categoryMap.entrySet()) {
            for (int pageNo = 0;;pageNo++) {
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
                .topNode(new ArrayList<>(Arrays.asList("#grid2-2-4 > li")))
                .title(new ArrayList<>(Arrays.asList(".over_info p", ".name span")))
                .price(new ArrayList<>(Arrays.asList(".over_info ul", "span:contains(won)")))
                .imageUrl(new ArrayList<>(Arrays.asList(".over_bg div", "a", "img")))
                .link(new ArrayList<>(Arrays.asList(".box a")))
                .extraImageUrl(new ArrayList<>(Arrays.asList("#contents div", ".ThumbImage")))
                .description(new ArrayList<>(Arrays.asList(".desc div:eq(1)")))
                .size(new ArrayList<>(Arrays.asList("#cartArea", "span")))
                .build();

        List<Item> itemList = new ArrayList<>();

        // 최상위 Elements 추출
        Elements elements = commonCrawlingService.getTopNodeElements(pageNo, fullUrl, selector.getTopNode());

        if(elements.size() == 0) return itemList;

        // 공통 필드 추가
        itemList = commonCrawlingService.getItemListWithCommonFields(elements, selector);

        // 필드 로컬 작업
        for (Item item : itemList) {

            String[] priceStr = item.getPrice().split(" ");

            if(priceStr.length == 2) {
                item.setPrice(priceStr[0].replace(",", ""));
            }
            else if(priceStr.length == 5){
                item.setPrice(priceStr[2].replace(",", ""));
            }

            item.setPrice(item.getPrice().split(" ")[0].replace(",", ""));

            item.setImageUrl("https://" + item.getImageUrl().substring(2));

            item.setLink("http://vivastudio.co.kr" + item.getLink());

            item.setMallNo("0002");

            item.setId(item.getMallNo() + item.getLink().split("\\?")[1].split("&")[0].split("=")[1]);

            item.setMallNm("비바스튜디오");

            item.setCategory(category.getKey());

            // extra 필드 추가
            item.setProductExtra(commonCrawlingService.setExtraFields(selector, item.getLink(), host));
        }

        return itemList;
    }
}
