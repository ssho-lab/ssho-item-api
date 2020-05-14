package webcrawler.shopping.swipe.service;

import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.Item;

import java.io.IOException;
import java.util.List;

public interface CommonCrawlingService {
    Elements getTopElements(final int pageNo, final String url, final List<String> selectors) throws IOException;
    void updateAll(final List<Item> itemList);
}
