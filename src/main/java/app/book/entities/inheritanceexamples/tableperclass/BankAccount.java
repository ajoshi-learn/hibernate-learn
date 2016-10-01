package app.book.entities.inheritanceexamples.tableperclass;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ajoshi on 01-Oct-16.
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "BANK_ACCOUNT")
public class BankAccount extends BillingDetails {
    @Column(name = "ACCOUNT", nullable = false)
    private String account;
}
