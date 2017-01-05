package app.book.entities.ternaryassociations;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajoshi on 05-Jan-17.
 */
@Data
@NoArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String categoryName;
    @ManyToMany
    @MapKeyJoinColumn(name = "item_id")
    @JoinTable(name = "category_item",
            joinColumns = {@JoinColumn(name = "category_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Map<Item, Account> itemsAccounts = new HashMap<>();
}
