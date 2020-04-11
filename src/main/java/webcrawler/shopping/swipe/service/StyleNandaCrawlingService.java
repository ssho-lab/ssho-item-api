package webcrawler.shopping.swipe.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import webcrawler.shopping.swipe.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StyleNandaCrawlingService {

    private static String url = "https://www.stylenanda.com/product/list.html?cate_no=1902&page=1";

    public List<Product> crawlAllProducts() throws IOException {
        Document doc = Jsoup.connect(url).get();

        List<Product> productList = new ArrayList<>();

        Elements elements = doc.select(".column4 li");
        for (int i = 0; i < elements.size(); i++) {
            Element e = elements.get(i);

            String s = e.select(".name").select("span").text();
            String title = s.substring(4);

            s = e.select(".box").select("a").select("img").attr("src");
            String imageUrl = "https://" + s.substring(2);

            s = e.select(".box").select("a").attr("href");
            String link = "https://www.stylenanda.com" + s;

            Product product =
                    Product.builder().title(title).imageUrl(imageUrl).link(link).build();
            productList.add(product);
        }
        return productList;
    }
}
