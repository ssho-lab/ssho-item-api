package webcrawler.shopping.swipe.service;

import org.jsoup.select.Elements;
import webcrawler.shopping.swipe.domain.Item;
import webcrawler.shopping.swipe.model.ItemIdImageUrlMap;

import java.io.IOException;
import java.util.List;

public interface CommonCrawlingService {
    Elements getTopElements(final int pageNo, final String url, final List<String> selectors) throws IOException;
    void updateAll(final List<Item> itemList);
    List<Item> get100Items();
    List<ItemIdImageUrlMap> get100ItemsIdImageUrlMap();
}
