package webcrawler.shopping.swipe.service.item;

import org.jsoup.select.Elements;
import webcrawler.shopping.swipe.domain.CrawlingApiAccessLog;
import webcrawler.shopping.swipe.domain.item.model.Item;
import webcrawler.shopping.swipe.domain.tag.model.Tag;
import webcrawler.shopping.swipe.model.ItemIdImageUrlMap;
import webcrawler.shopping.swipe.model.ProductExtra;
import webcrawler.shopping.swipe.model.Selector;

import java.io.IOException;
import java.util.List;

/**
 * 크롤링 공통 작업을 위한 Interface
 */
public interface ItemService {

    Elements getTopNodeElements(final int pageNo, final String url, final List<String> selectors) throws IOException;

    List<Item> getItemListWithCommonFields(final Elements elements, final Selector selector);

    ProductExtra setExtraFields(final Selector selector, final String url, final String host) throws IOException;

    void requestCrawlingApiAccessLogSave(final CrawlingApiAccessLog crawlingApiAccessLog, final int itemListSize);

    List<Item> get100Items();

    List<ItemIdImageUrlMap> get100ItemsIdImageUrlMap();

    void updateTagList(final List<Tag> tagList, final String itemId) throws IOException;
}
