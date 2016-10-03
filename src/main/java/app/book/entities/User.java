package app.book.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Currency;
import java.util.StringTokenizer;

/**
 * Created by ajoshi on 28-Sep-16.
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String firstname;
    private String lastname;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "HOME_STREET")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "HOME_ZIPCODE")),
            @AttributeOverride(name = "city", column = @Column(name = "HOME_CITY")),
    })
    private Address address;

    public String getName() {
        return firstname + " " + lastname;
    }

    public void setName(String name) {
        StringTokenizer stringTokenizer = new StringTokenizer(name);
        firstname = stringTokenizer.nextToken();
        lastname = stringTokenizer.nextToken();
    }

    public static Currency getPreferences() {
        return Currency.getInstance("USD");
    }
}
