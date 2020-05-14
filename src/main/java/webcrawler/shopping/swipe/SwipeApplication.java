package webcrawler.shopping.swipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 페이탈리즘 / Mmlg / 비욘드클로젯 / 엔더슨 벨 / 비바스튜디오 / 칼하트 / 블랭크 스튜디오 -> 스트릿
 * 프론트로우 / 마가린핑거스 / 커스텀멜로우 / 룩앳민 / 치즈달 / 스타일 난다 / 룩넌 . 츄 / 엠엔어스 / 바온 -> 여성/남성 의류 전문
 * 스파오 / 유니클로 / 토마스 모어 / 아더에러 / 모한 -> 놈코어
 */

@SpringBootApplication
@EnableScheduling
public class SwipeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwipeApplication.class, args);
    }

}
