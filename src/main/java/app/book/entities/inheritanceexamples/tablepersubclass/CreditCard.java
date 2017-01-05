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
@DiscriminatorValue("CC")
public class CreditCard extends BillingDetails {
    @Column(name = "CC_NUMBER")
    private String number;
}
