package app.book.entities.custommappingtypes.usertypes;

import org.hibernate.usertype.ParameterizedType;

import java.util.Currency;
import java.util.Properties;

/**
 * Created by ajoshi on 03-Oct-16.
 */
public class MonetaryAmountConversionType extends MonetaryAmountUserType implements ParameterizedType {

    private Currency convertTo;

    @Override
    public void setParameterValues(Properties parameters) {
        this.convertTo = Currency.getInstance(parameters.getProperty("convertTo"));
    }
}
