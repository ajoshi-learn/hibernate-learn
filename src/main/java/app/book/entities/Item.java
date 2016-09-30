package app.book.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
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

    private String name;
    private String description;

    private Set<Category> categories = new HashSet<>();

    public void addCategory(Category category) {
        if(category == null) {
            throw new IllegalArgumentException("Null category");
        }
        category.getItems().add(this);
        categories.add(category);
    }
}
