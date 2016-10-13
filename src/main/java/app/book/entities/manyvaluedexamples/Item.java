package app.book.entities.manyvaluedexamples;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ajoshi on 13-Oct-16.
 */
@Data
@NoArgsConstructor
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private List<Bid> bids = new ArrayList<>();

    @ManyToMany(mappedBy = "items")
    private Set<Category> categories = new HashSet<>();
}
