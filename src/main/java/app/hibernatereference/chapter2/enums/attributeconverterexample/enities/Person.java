package app.hibernatereference.chapter2.enums.attributeconverterexample.enities;

import app.hibernatereference.chapter2.enums.attributeconverterexample.converter.GenderConverter;
import lombok.Data;

import javax.persistence.*;

@Entity(name = "persons")
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Convert(converter = GenderConverter.class)
    public Gender gender;
}