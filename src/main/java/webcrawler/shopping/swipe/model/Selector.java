package webcrawler.shopping.swipe.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * CSS Selector 모델
 * 상품 데이터 HTML Parsing(Jsoup) 모듈화를 위해 사용
 * Selector Chaining을 위해 모든 필드는 List
 */
@Data
@Builder
public class Selector {
    List<String> topNode;    // 상품 공통 정보를 위한 Selector 리스트

    List<String> title;
    List<String> price;
    List<String> imageUrl;
    List<String> link;

    List<String> extraImageUrl;
    List<String> description;
    List<String> size;
}
