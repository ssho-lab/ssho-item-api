# ssho-shopping-mall-crawling-server

## base-url : http://13.124.59.2:8081

## 스타일난다 상품 조회

| 메소드 | 경로                 | 짧은 설명 |
| ------ | -------------------- | --------- |
| GET    |/stylenanda?pageNo={pageNo}&offset={offset} |           |

### 요청 파라미터

| 파라미터 명 | 파라미터 값 예시 | 최대 가능 값 | 설명
| ----------- | ---------------|------------|------|
| pageNo        | 2            | 22  | 페이지 인덱스(1부터 시작) |
| offset         | 50          | 100 | 페이지 당 상품 개수 |

### 응답 바디

```
[
    {
        "title": "소프트네온연두 일자앵클팬츠",
        "imageUrl": "https://www.stylenanda.com/web/product/tiny/20200417/b471905f3d4bbae38607b79f104b9a34.jpg",
        "link": "https://www.stylenanda.com/product/detail.html?product_no=248227&cate_no=1902&display_group=1"
    },
    {
        "title": "여리핏크로셰 언발크롭니트",
        "imageUrl": "https://www.stylenanda.com/web/product/tiny/20200417/85e39312f4e35f09665f98b3ba16f216.jpg",
        "link": "https://www.stylenanda.com/product/detail.html?product_no=248218&cate_no=1902&display_group=1"
    },
    {
        "title": "라이트아모르 크롭하프티",
        "imageUrl": "https://www.stylenanda.com/web/product/tiny/20200421/e4035f7e649349d5e48f03f1d6853842.jpg",
        "link": "https://www.stylenanda.com/product/detail.html?product_no=248224&cate_no=1902&display_group=1"
    },
    {
        "title": "고퀄무드라인 오픈토부티힐",
        "imageUrl": "https://www.stylenanda.com/web/product/tiny/20200413/0f736ba8632a928f8a6d2c41a0b9006a.jpg",
        "link": "https://www.stylenanda.com/product/detail.html?product_no=248162&cate_no=1902&display_group=1"
    },
    {
        "title": "라이트쭈링클 미니가디건탑",
        "imageUrl": "https://www.stylenanda.com/web/product/tiny/20200413/719758146add70c9a9747e7a162fb003.jpg",
        "link": "https://www.stylenanda.com/product/detail.html?product_no=248136&cate_no=1902&display_group=1"
    }
]
```
