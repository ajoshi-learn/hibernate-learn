package app.chapter2.customtypes.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.BitSet;

/**
 * Created by ajoshi on 15-Sep-16.
 */
@Entity(name = "Product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private Integer id;

    @Type( type = "bitset" )
    private BitSet bitSet;
}