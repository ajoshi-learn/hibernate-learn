package app.book.entities.inheritanceexamples.tableperclasshierarchy;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by ajoshi on 01-Oct-16.
 */
@NoArgsConstructor
@Data
@Entity
@DiscriminatorValue("BA")
public class BankAccount extends BillingDetails {
    @Column(name = "BA_ACCOUNT")
    private String account;
}
