package webcrawler.shopping.swipe.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import webcrawler.shopping.swipe.Item;

public interface ItemRepository extends MongoRepository<Item, String> {
}
