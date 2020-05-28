package webcrawler.shopping.swipe.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class User {
    @Id
    private String id;
    private String name;
}
