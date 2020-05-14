package webcrawler.shopping.swipe.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.Item;
import webcrawler.shopping.swipe.model.ProductExtra;
import webcrawler.shopping.swipe.model.Selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class VivastudioCrawlingServiceImpl {

    private static String url = "http://vivastudio.co.kr/product/list.html?cate_no=27&page=";
    private static String host = "http://vivastudio.co.kr/";

    private final CommonCrawlingServiceImpl commonCrawlingService;

    public VivastudioCrawlingServiceImpl(final CommonCrawlingServiceImpl commonCrawlingService){
        this.commonCrawlingService = commonCrawlingService;
    }

    public List<Item> crawlAllProductsInSinglePage(final int pageNo) throws IOException {

        // 크롤링시 사용할 Selector 객체 생성
        Selector selector = Selector.builder()
                .commonSelector(new ArrayList<>(Arrays.asList("#grid2-2-4 > li")))
                //.extraSelector(new ArrayList<>(Arrays.asList(".d_proimage img")))
                .title(new ArrayList<>(Arrays.asList(".over_info p", ".name span")))
                .price(new ArrayList<>(Arrays.asList(".over_info ul", "span")))
                .imageUrl(new ArrayList<>(Arrays.asList(".over_bg div", "a", "img")))
                .link(new ArrayList<>(Arrays.asList(".box a")))
                .extraImageUrl(new ArrayList<>(Arrays.asList("#contents div", ".ThumbImage")))
                .description(new ArrayList<>(Arrays.asList(".desc div")))
                .size(new ArrayList<>(Arrays.asList("#cartArea", "span")))
                .build();

        List<Item> itemList = new ArrayList<>();

        // 최상위 Elements 추출
        Elements elements = commonCrawlingService.getTopElements(pageNo, url, selector.getCommonSelector());

        if(elements.size() == 0) return itemList;

        // 공통 필드 추가
        itemList = commonCrawlingService.getItemListWithCommonFields(elements, selector);

        // 필드 로컬 작업
        for (Item item : itemList) {

            item.setPrice(item.getPrice().split(" ")[0].replace(",", ""));

            item.setImageUrl("https://" + item.getImageUrl().substring(2));

            item.setLink("http://vivastudio.co.kr" + item.getLink());

            item.setMallNo("0002");

            item.setId(item.getMallNo() + item.getLink().split("\\?")[1].split("&")[0].split("=")[1]);

            item.setMallNm("비바스튜디오");

            // extra 필드 추가
            item.setProductExtra(commonCrawlingService.setExtraFields(item.getLink(), item, selector, host, 1));
        }

        return itemList;
    }

    public ProductExtra crawlExtra(final String url) throws IOException {

        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select("#contents div").select(".ThumbImage");
        List<String> extraImageUrlList = new ArrayList<>();

        for(int i = 0; i < elements.size(); i++){
            String imageUrl = elements.get(i).attr("src").substring(2);
            extraImageUrlList.add(imageUrl);
        }

        String description = doc.select(".desc div").get(1).text();

        Elements sizeElements = doc.select("#cartArea").select("span");
        List<String> sizeList = new ArrayList<>();

        for(int i = 0; i < sizeElements.size(); i++){
            String size = sizeElements.get(i).text();
            if(size.equals("")) continue;
            sizeList.add(size);
        }

        ProductExtra productExtra =
                ProductExtra.builder().extraImageUrlList(extraImageUrlList)
                        .description(description)
                        .sizeList(sizeList).build();

        return productExtra;
    }
}
