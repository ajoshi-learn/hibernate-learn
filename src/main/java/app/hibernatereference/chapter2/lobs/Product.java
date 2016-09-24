package app.hibernatereference.chapter2.lobs;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Blob;
import java.sql.Clob;

/**
 * Created by ajoshi on 24-Sep-16.
 */
@Entity
@Table(name = "lobsProducts")
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String name;

    @Lob
    private Clob warranty;

    @Lob
    private Blob image;

//    Also we can map Clobs as String or char[]
//    @Lob
//    private String warranty;
//    We can map Blobs as byte[]
}
