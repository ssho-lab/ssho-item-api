package webcrawler.shopping.swipe.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import webcrawler.shopping.swipe.domain.UserItem;

import java.util.List;

public interface UserItemRepository extends MongoRepository<UserItem, String> {
    List<UserItem> findAllByUserId(final String userId);
}
