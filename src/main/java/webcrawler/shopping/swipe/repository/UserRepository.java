package webcrawler.shopping.swipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webcrawler.shopping.swipe.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
    User findByName(String name);
    Optional<User> findById(int id);
}
