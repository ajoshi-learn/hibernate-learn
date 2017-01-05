package app.book.entities.ternaryassociations;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by ajoshi on 05-Jan-17.
 */
@Data
@NoArgsConstructor
@Entity
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String username;
}
