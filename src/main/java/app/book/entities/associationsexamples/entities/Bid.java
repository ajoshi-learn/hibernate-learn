package app.book.entities.associationsexamples.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

/**
 * Created by ajoshi on 07-Oct-16.
 */
@NoArgsConstructor
@Data
@Entity
@ToString
public class Bid {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false)
    private String field1;
    private String field2;
}
