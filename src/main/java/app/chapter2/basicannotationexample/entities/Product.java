package app.chapter2.basicannotationexample.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by ajoshi on 15-Sep-16.
 */
@Entity
@NoArgsConstructor
@Data
public class Product {

    @Id
    @Basic
    private Integer id;

    @Basic
    private String sku;
}
