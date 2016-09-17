package app.chapter2.enumsexample;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "phones")
@NoArgsConstructor
@Data
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "phone_number")
    private String number;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "phone_type")
    private PhoneType phoneType;
}
