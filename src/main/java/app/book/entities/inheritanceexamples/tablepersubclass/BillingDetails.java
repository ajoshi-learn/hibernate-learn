package app.book.entities.inheritanceexamples.tablepersubclass;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by ajoshi on 01-Oct-16.
 */
@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "BILLING_DETAILS_TYPE",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class BillingDetails {
    @Id @GeneratedValue
    @Column(name = "BILLING_DETAILS_ID")
    private Long id;


    @Column(name = "OWNER")
    private String owner;
}