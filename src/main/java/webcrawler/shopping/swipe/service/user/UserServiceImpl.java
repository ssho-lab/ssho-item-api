package webcrawler.shopping.swipe.service.user;

import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.domain.user.model.User;
import webcrawler.shopping.swipe.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private JwtService jwtService;
    private UserRepository userRepository;

    public UserServiceImpl(final JwtService jwtService, final UserRepository userRepository){
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public int authorization(final String jwt) {

        final int userIdx = jwtService.decode(jwt).getUser_idx();
        if (userIdx == -1) return -1;

        final Optional<User> user = userRepository.findById(userIdx);
        if (!user.isPresent()) return -1;

        return userIdx;

    }
}
