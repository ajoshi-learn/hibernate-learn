package app.book.entities.inheritanceexamples.tablepersubclass;

import lombok.Data;

import javax.persistence.Entity;

/**
 * Created by ajoshi on 01-Oct-16.
 */
@Entity
@Data
public class BankAccount extends BillingDetails {
    private String account;
}
