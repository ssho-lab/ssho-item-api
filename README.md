# ssho-shopping-mall-crawling-server

## base-url : http://13.124.59.2:8081

## 100개 상품이 포함된 카드덱 리스트 조회

| 메소드 | 경로                 | 짧은 설명 |
| ------ | -------------------- | --------- |
| GET    | /item |  요청시 마다 상품 리스트가 바뀜(랜덤)   |

### 응답 바디

```
[
    {
        "id": "00023303",
        "mallNo": "0002",
        "mallNm": "비바스튜디오",
        "title": "BANDANA SHIRTS JS [BLACK]",
        "price": "BANDANA",
        "imageUrl": "https://vivastudio.co.kr/web/product/medium/20200428/b902d3ef729a9e02aba69a2af14b81aa.jpg",
        "link": "http://vivastudio.co.kr/product/detail.html?product_no=3303&cate_no=27&display_group=1",
        "productExtra": {
            "extraImageUrlList": [
                "http://vivastudio.co.kr///vivastudio.co.kr/web/product/extra/small/20200428/0755da07ddd4865b043c4172451620b8.jpg",
                "http://vivastudio.co.kr///vivastudio.co.kr/web/product/extra/small/20200428/5c74ce8b7f0dbad26d5e1410bcd00fb1.jpg",
                "http://vivastudio.co.kr///vivastudio.co.kr/web/product/extra/small/20200428/fed867987702f8a066a70bcdb562eaa2.jpg",
                "http://vivastudio.co.kr///vivastudio.co.kr/web/product/extra/small/20200428/f88a54affbac95b75ecdeb15f98ffb1d.jpg",
                "http://vivastudio.co.kr///vivastudio.co.kr/web/product/extra/small/20200428/a3ee6aae1320b633d17f12165bcbc3f3.jpg",
                "http://vivastudio.co.kr///vivastudio.co.kr/web/product/extra/small/20200428/b6c658af5e5a7b1d17eaa766d89ad79d.jpg",
                "http://vivastudio.co.kr///vivastudio.co.kr/web/product/extra/small/20200428/ff18b7f1e5cc49057947db650bae5496.jpg",
                "http://vivastudio.co.kr///vivastudio.co.kr/web/product/extra/small/20200428/45f09c5f137741482944f92ff751f3f3.jpg"
            ],
            "description": "No. P0000EXB",
            "sizeList": [
                "S",
                "M",
                "L",
                "XL"
            ]
        }
    },
    {
        "id": "0001248642",
        "mallNo": "0001",
        "mallNm": "스타일난다",
        "title": "멀티블러리 슬림텐션미니sk",
        "price": "31000 ",
        "imageUrl": "https://www.stylenanda.com/web/product/tiny/20200508/4ad1c8d3476dbf68df0e1a22c9fcb573.webp",
        "link": "https://www.stylenanda.com/product/detail.html?product_no=248642&cate_no=1902&display_group=1",
        "productExtra": {
            "extraImageUrlList": [
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_0(15).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_0(14).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_0(12).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_0(11).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_0(8).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_0(7).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_0(5).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_0(4).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_0(6).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(7).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(8).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(9).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(10).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(11).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(12).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(6).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(5).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(1).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(2).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(3).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200508_0429_102(4).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(1).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(2).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(3).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(4).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(5).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(6).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(8).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(10).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(13).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(14).jpg",
                "https://www.stylenanda.com//2017/upload5/yyj-20200512_0507_107(16).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_010.jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_011.jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_011(1).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_011(2).jpg",
                "https://www.stylenanda.com//2017/upload5/yjko200508_su0429_101_011(3).jpg"
            ],
            "description": "여러 물감을 한 팔레트에 블러링시킨 것처럼 아트적인 스커트예요. 물속에서 비치는 것처럼 웨이브가 있는 패턴이라 굉장히 입체적입니다. 하나의 미술 기법처럼 아름다운 한 폭이 될 거예요. 다른 디테일은 없이 오직 이 패턴으로 미니 스커트를 전면 채웠고요, 신축이 있는 편이라 힙 라인부터 허벅지 상단~반까지는 개인의 체형에 맞는 슬림 텐션핏을 연출합니다. 기본 이 스펙에 S/M 두 사이즈 구분 진행으로 더욱 최적의 핏을 픽하시기 좋을 거예요. 원 포인트로 탁월한 스커트이니 평소 즐겨 입는 톤의 연결 컬러로 초이스해 주시기 바랍니다.",
            "sizeList": [
                "S",
                "M"
            ]
        }
    }
]
```
