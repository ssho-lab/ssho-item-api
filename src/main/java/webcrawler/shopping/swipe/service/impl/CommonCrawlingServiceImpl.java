package webcrawler.shopping.swipe.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.stereotype.Service;

import webcrawler.shopping.swipe.domain.Item;
import webcrawler.shopping.swipe.model.ItemIdImageUrlMap;
import webcrawler.shopping.swipe.model.ProductExtra;
import webcrawler.shopping.swipe.model.Selector;
import webcrawler.shopping.swipe.repository.ItemRepository;
import webcrawler.shopping.swipe.service.CommonCrawlingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 크롤링 공통 작업 Impl
 */
@Slf4j
@Service
public class CommonCrawlingServiceImpl implements CommonCrawlingService {

    private final ItemRepository itemRepository;

    public CommonCrawlingServiceImpl(final ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }

    /**
     * HTML Parsing을 위한 최상위 Node Elements 탐색
     * @param pageNo
     * @param url
     * @param selectors
     * @return Elements
     * @throws IOException
     */
    @Override
    public Elements getTopNodeElements(final int pageNo, final String url, final List<String> selectors) throws IOException {

        // 상품 전체 리스트 페이지를 최초로 Jsoup을 통해 연결
        Document doc = Jsoup.connect(url + pageNo).get();

        Elements elements = doc.select(selectors.get(0));

        if (selectors.size() == 1) return elements;

        // Selector가 2개 이상일 때는 Chaining 진행
        else {
            for (int i = 1; i < selectors.size(); i++) {
                elements = elements.select(selectors.get(i));
            }
            return elements;
        }
    }

    /**
     * 상품 공통 필드 정보 크롤링
     * 제목, 판매 가격, 이미지 URL, 상세 페이지 Link
     * @param elements
     * @param selector
     * @return List<Item>
     */
    @Override
    public List<Item> getItemListWithCommonFields(final Elements elements, final Selector selector){

        List<Item> itemList = new ArrayList<>();

        for (int i = 0; i < elements.size(); i++) {

            Element element = elements.get(i).clone();

            Elements temp = new Elements();

            // 제목(Title)
            for (String s : selector.getTitle()) {
                temp = element.select(s);
            }
            String title = temp.text();

            // 판매 가격(Price)
            for (String s : selector.getPrice()) {
                temp = element.select(s);
            }
            String price = temp.text();

            // 이미지 Url(ImageUrl)
            for (String s : selector.getImageUrl()) {
                temp = element.select(s);
            }
            String imageUrl = temp.attr("src");

            // 상세 페이지 Link(Link)
            for (String s : selector.getLink()) {
                temp = element.select(s);
            }
            String link = temp.attr("href");

            Item item =
                    Item.builder()
                            .title(title)
                            .price(price)
                            .imageUrl(imageUrl)
                            .link(link)
                            .build();

            itemList.add(item);
        }
        return itemList;
    }

    /**
     * 상품 상세 필드 정보 크롤링
     * 제목, 판매 가격, 이미지 URL, 상세 페이지 Link
     * @param selector
     * @param url
     * @param host
     * @return ProductExtra
     * @throws IOException
     */
    @Override
    public ProductExtra setExtraFields(final Selector selector, final String url, final String host) throws IOException {

        // 상품 상세 페이지를 최초로 Jsoup을 통해 연결
        Document doc = Jsoup.connect(url).get();

        Elements elements;

        // 상세 이미지 리스트(ExtraImageList)
        elements = doc.select(selector.getExtraImageUrl().get(0));

        List<String> extraImageUrlList = new ArrayList<>();
        for(int i = 1; i < selector.getExtraImageUrl().size(); i++){
            elements = elements.select(selector.getExtraImageUrl().get(i));
        }

        for(Element e : elements){
            String imageUrl = host + e.attr("src");
            extraImageUrlList.add(imageUrl);
        }

        // 상품 설명(Description)
        elements = doc.select(selector.getDescription().get(0));

        for(int i = 1; i < selector.getDescription().size(); i++){
            elements = elements.select(selector.getDescription().get(i));
        }

        String description = elements.get(0).text();

        // 상품 사이즈 리스트(SizeList)
        List<String> sizeList = new ArrayList<>();
        elements = doc.select(selector.getSize().get(0));

        for(int i = 1; i < selector.getSize().size(); i++){
            elements = elements.select(selector.getSize().get(i));
        }

        for(Element e : elements){
            String size = e.text();
            if (size.equals("")) continue;
            if (size.equals("FRE") || size.equals("fre")
                    || size.equals("free") || size.equals("Free")) size = "FREE";

            sizeList.add(size);
        }

        ProductExtra productExtra =
                ProductExtra.builder().extraImageUrlList(extraImageUrlList)
                        .description(description)
                        .sizeList(sizeList).build();

        return productExtra;
    }

    /**
     * DB 업데이트
     * @param itemList
     */
    @Override
    public void updateAll(final List<Item> itemList){
        itemRepository.deleteAll();
        itemRepository.saveAll(itemList);
    }

    /**
     * 100개 상품 랜덤 추출
     * @return List<Item>
     */
    @Override
    public List<Item> get100Items(){
        List<Item> itemList = itemRepository.findAll();
        Collections.shuffle(itemList);
        return itemList.subList(0,100);
    }

    /**
     * 상품 고유 번호 - 상품 이미지 포함 100개 상품 랜덤 추출
     * @return List<ItemIdImageUrlMap>
     */
    @Override
    public List<ItemIdImageUrlMap> get100ItemsIdImageUrlMap(){
        List<Item> itemList = itemRepository.findAll();
        Collections.shuffle(itemList);
        List<ItemIdImageUrlMap> itemIdImageUrlMapList = new ArrayList<>();

        itemList.forEach(i ->itemIdImageUrlMapList.add(ItemIdImageUrlMap.builder()
                .id(i.getId())
                .imageUrl(i.getImageUrl())
                .build()));

        return itemIdImageUrlMapList;
    }
}
