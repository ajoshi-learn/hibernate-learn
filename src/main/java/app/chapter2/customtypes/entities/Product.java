package app.chapter2.customtypes.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by ajoshi on 15-Sep-16.
 */
@Entity
@NoArgsConstructor
@Data
public class Product implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Type(type = "custom_biginteger")
    private BigInteger number;
}
