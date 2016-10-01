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
@Table(name = "CREDIT_CARD")
public class CreditCard extends BillingDetails {
    @Column(name = "NUMBER", nullable = false)
    private String number;
}
