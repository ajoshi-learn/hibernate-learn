package app.book.entities.associationsexamples.entities;

import app.book.entities.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by ajoshi on 07-Oct-16.
 */
@NoArgsConstructor
@Data
public class Bid {

    @ManyToOne
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;
}
