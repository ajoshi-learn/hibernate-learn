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
@AttributeOverride(name = "owner", column = @Column(name = "CC_OWNER", nullable = false))
public class CreditCard extends BillingDetails {
    @Id
    @GeneratedValue
    @Column(name = "CREDIT_CARD_ID")
    private Long id = null;
    @Column(name = "NUMBER", nullable = false)
    private String number;
}
