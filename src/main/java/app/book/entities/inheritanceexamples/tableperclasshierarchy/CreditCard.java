package app.book.entities.inheritanceexamples.tableperclasshierarchy;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ajoshi on 01-Oct-16.
 */
@NoArgsConstructor
@Data
@Entity
@DiscriminatorValue("CC")
public class CreditCard extends BillingDetails {
    @Column(name = "CC_NUMBER")
    private String number;
}
