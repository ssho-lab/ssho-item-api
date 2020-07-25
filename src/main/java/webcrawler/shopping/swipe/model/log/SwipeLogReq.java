package webcrawler.shopping.swipe.model.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Swipe Req
 */
@Data
@Builder
public class SwipeLogReq {

    private String startTime;

    private List<SwipeLog> swipeList;
}
