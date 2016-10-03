package app.book.entities.custommappingtypes.usertypes;

import app.book.entities.User;
import app.book.entities.custommappingtypes.entities.MonetaryAmount;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

/**
 * Created by ajoshi on 03-Oct-16.
 */
public class MonetaryAmountUserType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{StandardBasicTypes.BIG_DECIMAL.sqlType()};
    }

    @Override
    public Class returnedClass() {
        return MonetaryAmount.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if(x == y) return true;
        if(x == null || y == null) return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        BigDecimal valueInUSD = rs.getBigDecimal(names[0]);
        if(rs.wasNull()) return null;
        Currency userCurrency = User.getPreferences();
        return new MonetaryAmount(valueInUSD, userCurrency);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if(value == null) {
            st.setNull(index, StandardBasicTypes.BIG_DECIMAL.sqlType());
        } else {
            MonetaryAmount anyCurrency = (MonetaryAmount) value;
            st.setBigDecimal(index, anyCurrency.getAmount());
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
