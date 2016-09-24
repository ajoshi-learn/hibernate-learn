package app.hibernatereference.chapter2.customtypes.custombasictype.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.BitSet;

/**
 * Created by ajoshi on 15-Sep-16.
 */
@Entity
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue
    private Integer id;

    @Type(type = "bitset")
    private BitSet bitSet;
}
