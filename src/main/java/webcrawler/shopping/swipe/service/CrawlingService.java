package webcrawler.shopping.swipe.service;

import webcrawler.shopping.swipe.Item;

import java.io.IOException;
import java.util.List;

public interface CrawlingService {
    List<Item> crawlAllProductsInSinglePage(final int pageNo) throws IOException;
}
