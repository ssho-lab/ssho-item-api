package webcrawler.shopping.swipe.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="user")
@Data
public class User {
    @Id
    private String id;
    private String name;
}
