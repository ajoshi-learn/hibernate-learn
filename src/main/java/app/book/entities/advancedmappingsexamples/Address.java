package app.book.entities.advancedmappingsexamples;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ajoshi on 08-Oct-16.
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "addresses")
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @OneToOne(mappedBy = "shippingAddress")
    private User user;
    @Column(name = "address")
    private String address;
}
