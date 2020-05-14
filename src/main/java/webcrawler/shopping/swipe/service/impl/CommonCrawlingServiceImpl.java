package webcrawler.shopping.swipe.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.Item;
import webcrawler.shopping.swipe.model.ProductExtra;
import webcrawler.shopping.swipe.model.Selector;
import webcrawler.shopping.swipe.repository.ItemRepository;
import webcrawler.shopping.swipe.service.CommonCrawlingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CommonCrawlingServiceImpl implements CommonCrawlingService {

    private final ItemRepository itemRepository;

    public CommonCrawlingServiceImpl(final ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }

    @Override
    public Elements getTopElements(final int pageNo, final String url, final List<String> selectors) throws IOException {

        Document doc = Jsoup.connect(url + pageNo).get();

        Elements elements = doc.select(selectors.get(0));

        if (selectors.size() == 1) return elements;

        else {
            for (int i = 1; i < selectors.size(); i++) {
                elements = elements.select(selectors.get(i));
            }
            return elements;
        }
    }

    public List<Item> getItemListWithCommonFields(final Elements elements, final Selector selector) throws IOException {

        List<Item> itemList = new ArrayList<>();

        for (int i = 0; i < elements.size(); i++) {

            Element e = elements.get(i);
            Element copy = e.clone();
            Elements temp = new Elements();

            // 제목(Title)
            for (String s : selector.getTitle()) {
                temp = copy.select(s);
            }
            String title = temp.text();

            // 가격(Price)
            for (String s : selector.getPrice()) {
                temp = copy.select(s);
            }
            String price = temp.text();

            // 이미지 Url(ImageUrl)
            for (String s : selector.getImageUrl()) {
                temp = copy.select(s);
            }
            String imageUrl = temp.attr("src");

            // 이미지 Url(ImageUrl)
            for (String s : selector.getLink()) {
                temp = copy.select(s);
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

    public ProductExtra setExtraFields(final String url, final Item item, final Selector selector, final String host, final int desIdx) throws IOException {

        Document doc = Jsoup.connect(url).get();

        Elements temp = doc.select(selector.getExtraImageUrl().get(0));

        List<String> extraImageUrlList = new ArrayList<>();
        for(int i = 1; i < selector.getExtraImageUrl().size(); i++){
            temp = temp.select(selector.getExtraImageUrl().get(i));
        }
        for(Element e : temp){
            String imageUrl = host + e.attr("src");
            extraImageUrlList.add(imageUrl);
        }

        temp = doc.select(selector.getDescription().get(0));
        for(int i = 1; i < selector.getDescription().size(); i++){
            temp = temp.select(selector.getDescription().get(i));
        }
        String description = temp.get(0).text();


        List<String> sizeList = new ArrayList<>();
        temp = doc.select(selector.getSize().get(0));
        for(int i = 1; i < selector.getSize().size(); i++){
            temp = temp.select(selector.getSize().get(i));
        }
        for(Element e : temp){
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

    @Override
    public void updateAll(final List<Item> itemList){
        itemRepository.deleteAll();
        itemRepository.saveAll(itemList);
        log.info("updated");
    }

    public List<Item> get100Items(){
        List<Item> itemList = itemRepository.findAll();
        Collections.shuffle(itemList);
        return itemList.subList(0,100);
    }
}
