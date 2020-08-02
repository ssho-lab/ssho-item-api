package webcrawler.shopping.swipe.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import webcrawler.shopping.swipe.domain.Item;

import java.util.Optional;

/**
 * Item Repository
 */
public interface ItemRepository extends MongoRepository<Item, String> {
}
