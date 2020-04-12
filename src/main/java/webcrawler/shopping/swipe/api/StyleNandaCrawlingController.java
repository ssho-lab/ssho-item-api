package webcrawler.shopping.swipe.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webcrawler.shopping.swipe.Product;
import webcrawler.shopping.swipe.service.StyleNandaCrawlingService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("stylenanda")
public class StyleNandaCrawlingController {

    private final StyleNandaCrawlingService styleNandaCrawlingService;

    public StyleNandaCrawlingController(final StyleNandaCrawlingService styleNandaCrawlingService){
        this.styleNandaCrawlingService = styleNandaCrawlingService;
    }

    @GetMapping("")
    List<Product> getAllProducts() throws IOException {
        return styleNandaCrawlingService.crawAllProducts();
    }
}
