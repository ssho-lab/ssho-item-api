package webcrawler.shopping.swipe.domain;

import lombok.Builder;
import lombok.Data;
import webcrawler.shopping.swipe.model.ProductExtra;

@Data
@Builder
public class Item {
    private String id;
    private String category;
    private String mallNo;
    private String mallNm;
    private String title;
    private String price;
    private String imageUrl;
    private String link;
    private ProductExtra productExtra;
}
