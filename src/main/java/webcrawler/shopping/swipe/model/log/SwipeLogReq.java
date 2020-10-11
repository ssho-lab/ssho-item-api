package webcrawler.shopping.swipe.model.log;

import lombok.Builder;
import lombok.Data;

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
