package webcrawler.shopping.swipe;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    String title;
    String imageUrl;
    String link;
}
