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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startTime;

    private List<SwipeLog> swipeList;
}
