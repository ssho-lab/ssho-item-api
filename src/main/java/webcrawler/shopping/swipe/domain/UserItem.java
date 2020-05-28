package webcrawler.shopping.swipe.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class UserItem {
    @Id
    private String id;
    private String userId;
    private String itemId;
}
