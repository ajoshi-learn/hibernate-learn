package app.book.entities.advancedmappingsexamples;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by ajoshi on 08-Oct-16.
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "categories")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address shippingAddress;
    @Column(name = "user_name")
    private String name;
}
