package app.book.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.StringTokenizer;

/**
 * Created by ajoshi on 28-Sep-16.
 */
@NoArgsConstructor
@Data
public class User {

    private String firstname;
    private String lastname;

    public String getName() {
        return firstname + " " + lastname;
    }

    public void setName(String name) {
        StringTokenizer stringTokenizer = new StringTokenizer(name);
        firstname = stringTokenizer.nextToken();
        lastname = stringTokenizer.nextToken();
    }
}
