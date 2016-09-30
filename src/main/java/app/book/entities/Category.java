package app.book.entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ajoshi on 28-Sep-16.
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "CATEGORY")
public class Category {

    @Setter(AccessLevel.PRIVATE)
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "CATEGORY_ID")
    private Long id;
    private String name;
    private Category parentCategory;

    private Set<Category> childCategories = new HashSet<>();
    private Set<Item> items = new HashSet<>();

    public void addChildCategory(Category childCategory) {
        if (childCategory == null) {
            throw new IllegalArgumentException("Null child category!");
        }
        if(childCategory.getParentCategory() != null) {
            childCategory.getParentCategory().getChildCategories().remove(childCategory);
        }
        childCategory.setParentCategory(this);
        childCategories.add(childCategory);
    }
}
