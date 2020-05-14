package webcrawler.shopping.swipe.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductExtra {
    private List<String> extraImageUrlList;
    private String description;
    private List<String> sizeList;
    //private List<String> colorList;
}
