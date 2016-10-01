package app.book.entities.inheritanceexamples.implicitpolymorphism;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by ajoshi on 01-Oct-16.
 */
@NoArgsConstructor
@Data
@Entity
@AttributeOverride(name = "owner", column = @Column(name = "BA_OWNER", nullable = false))
public class BankAccount extends BillingDetails {
    @Id @GeneratedValue
    @Column(name = "BANK_ACCOUNT_ID")
    private Long id;

    @Column(name = "ACCOUNT", nullable = false)
    private String account;
}
