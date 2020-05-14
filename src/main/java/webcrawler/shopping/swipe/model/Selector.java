package webcrawler.shopping.swipe.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Selector {
    List<String> commonSelector;
    List<String> extraSelector;

    List<String> title;
    List<String> price;
    List<String> imageUrl;
    List<String> link;

    List<String> extraImageUrl;
    List<String> description;
    List<String> size;
}
