package webcrawler.shopping.swipe.model.log;

import lombok.Builder;
import lombok.Data;

/**
 * SwipeLog 도메인
 */
@Data
@Builder
public class SwipeLog {
    private int userId;      // 회원 고유 번호
    private String itemId;      // 상품 고유 번호
    private int score;          // 스와이프 score
    private String swipeTime;   // 스와이프 로그 생성 시각
    private int duration;       // 해당 상품 카드에서 머문 시간(sec)
    private String expTagId;    // 노출 태그 고유 번호
    private String expTagName;  // 노출 태그 이름
    private int cardSetSeq;
    private int cardSeq;
}
