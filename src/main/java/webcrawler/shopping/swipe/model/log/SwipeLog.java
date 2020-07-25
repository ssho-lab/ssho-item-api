package webcrawler.shopping.swipe.model.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * SwipeLog 도메인
 */
@Data
@Builder
public class SwipeLog {
    private String userId;
    private String itemId;
    private int score;

    private String swipeTime;

    private int duration;
}
