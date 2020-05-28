package webcrawler.shopping.swipe.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import webcrawler.shopping.swipe.domain.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByName(final String name);
}
