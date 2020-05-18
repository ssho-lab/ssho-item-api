package webcrawler.shopping.swipe.service;

import webcrawler.shopping.swipe.domain.Item;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CrawlingService {
    List<Item> crawlAllProductsInCategory(final int pageNo, final Map.Entry<String, String> category) throws IOException;
}
