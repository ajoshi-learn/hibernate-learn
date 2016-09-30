package app.book.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ajoshi on 28-Sep-16.
 */
@NoArgsConstructor
@Data
public class Category {
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
