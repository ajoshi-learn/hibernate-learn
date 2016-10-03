package app.book.entities.custommappingtypes.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by ajoshi on 03-Oct-16.
 */
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class MonetaryAmount implements Serializable {

    private final BigDecimal amount;
    private final Currency currency;
}
