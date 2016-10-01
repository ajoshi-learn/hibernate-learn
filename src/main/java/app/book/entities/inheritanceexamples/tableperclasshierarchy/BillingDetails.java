package app.book.entities.inheritanceexamples.tableperclasshierarchy;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by ajoshi on 01-Oct-16.
 */
@NoArgsConstructor
@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "BILLING_DETAILS_TYPE",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class BillingDetails {
    @Id @GeneratedValue
    @Column(name = "BILLING_DETAILS_ID")
    private Long id = null;

    @Column(name = "OWNER")
    private String owner;
}
