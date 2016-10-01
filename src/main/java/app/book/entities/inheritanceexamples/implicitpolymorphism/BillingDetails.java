package app.book.entities.inheritanceexamples.implicitpolymorphism;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by ajoshi on 01-Oct-16.
 */
@NoArgsConstructor
@Data
@MappedSuperclass
public abstract class BillingDetails {
    @Column(name = "OWNER", nullable = false)
    private String owner;
}
