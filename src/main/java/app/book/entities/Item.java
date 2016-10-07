package app.book.entities;

import app.book.entities.associationsexamples.entities.Bid;
import app.book.entities.custommappingtypes.entities.MonetaryAmount;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ajoshi on 28-Sep-16.
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "ITEM")
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String description;

//    private Set<Category> categories = new HashSet<>();
//    // UserType
//    @Type(type = "app.book.entities.custommappingtypes.usertypes.MonetaryAmountUserType")
//    @Column(name = "INITIAL_PRICE")

    // CompositeUserType
    @Type(type = "app.book.entities.custommappingtypes.usertypes.MonetaryAmountCompositeUserType")
    @Columns(columns = {
            @Column(name = "INITIAL_PRICE"),
            @Column(name = "INITIAL_PRICE_CURRENCY")
    })
    private MonetaryAmount initialPrice;

    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Set<Bid> bids = new HashSet<>();

    public void addBid(Bid bid) {
        bid.setItem(this);
        bids.add(bid);
    }
//    public void addCategory(Category category) {
//        if(category == null) {
//            throw new IllegalArgumentException("Null category");
//        }
//        category.getItems().add(this);
//        categories.add(category);
//    }
}
