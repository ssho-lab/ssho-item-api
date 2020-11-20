package webcrawler.shopping.swipe.service.collector;

import webcrawler.shopping.swipe.domain.item.model.Item;

import java.io.IOException;
import java.util.List;

public interface CollectorService {
    List<Item> updateAllMalls() throws IOException;
    List<Item> updateMall() throws IOException;
}
