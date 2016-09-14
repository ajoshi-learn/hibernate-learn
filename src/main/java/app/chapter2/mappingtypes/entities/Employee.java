package app.chapter2.mappingtypes.entities;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by ajoshi on 14-Sep-16.
 */
@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column(columnDefinition = "smallint")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean sex;
}
