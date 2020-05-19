package webcrawler.shopping.swipe.service;

import webcrawler.shopping.swipe.domain.Item;

import java.io.IOException;
import java.util.List;

public interface CollectorService {
    List<Item> collectAndUpdateAllItems() throws IOException;
}
