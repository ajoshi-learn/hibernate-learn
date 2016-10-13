package app.book.entities.manyvaluedexamples;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by ajoshi on 13-Oct-16.
 */
@Data
@NoArgsConstructor
@Entity
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID", nullable = false, updatable = false, insertable = false)
    private Item item;
}
