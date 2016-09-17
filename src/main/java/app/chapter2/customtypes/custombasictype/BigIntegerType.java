package app.chapter2.customtypes.custombasictype;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.java.BigIntegerTypeDescriptor;
import org.hibernate.type.descriptor.sql.IntegerTypeDescriptor;
import java.math.BigInteger;

/**
 * Created by ajoshi on 15-Sep-16.
 */
public class BigIntegerType
        extends AbstractSingleColumnStandardBasicType<BigInteger>
        implements DiscriminatorType<BigInteger> {

    public static final BigIntegerType INSTANCE = new BigIntegerType();

    public BigIntegerType() {
        super(IntegerTypeDescriptor.INSTANCE, BigIntegerTypeDescriptor.INSTANCE);
    }

    public BigInteger stringToObject(String xml) throws Exception {
        return fromString(xml);
    }

    public String objectToSQLString(BigInteger value, Dialect dialect) throws Exception {
        return toString(value.add(BigInteger.TEN));
    }

    public String getName() {
        return "custom_biginteger";
    }
}
