package app.book.entities.inheritanceexamples.tablepersubclass;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Created by ajoshi on 01-Oct-16.
 */
@NoArgsConstructor
@Data
@Entity
@PrimaryKeyJoinColumn(name = "CREDIT_CARD_ID")
@Table(name = "CREDIT_CARD_ACCOUNT")
public class CreditCard extends BillingDetails {
    @Column(name = "NUMBER", nullable = false)
    private String number;

    @Override
    public String toString() {
        return "CreditCard{" +
                "number='" + number + '\'' +
                "id='" + getId() + '\'' +
                "owner='" + getOwner() + '\'' +
                '}';
    }
}
