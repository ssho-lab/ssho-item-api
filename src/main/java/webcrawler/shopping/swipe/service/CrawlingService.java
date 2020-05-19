package webcrawler.shopping.swipe.service;

import webcrawler.shopping.swipe.domain.Item;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 쇼핑몰별 크롤링 작업 Interface
 */
public interface CrawlingService {

    List<Item> crawlAllProductsInAllCategory() throws IOException;

    List<Item> crawlAllProductsInOneCategory(final int pageNo, final Map.Entry<String, String> category) throws IOException;
}
