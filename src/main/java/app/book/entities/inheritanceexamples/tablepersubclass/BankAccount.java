package app.book.entities.inheritanceexamples.tablepersubclass;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by ajoshi on 01-Oct-16.
 */
@Entity
@Data
@DiscriminatorValue("BA")
public class BankAccount extends BillingDetails {
    @Column(name = "BA_ACCOUNT")
    private String account;
}
