package app.book.entities.custommappingtypes.usertypes;

import app.book.entities.User;
import app.book.entities.custommappingtypes.entities.MonetaryAmount;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.ParameterizedType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import static org.hibernate.type.StandardBasicTypes.BIG_DECIMAL;
import static org.hibernate.type.StandardBasicTypes.CURRENCY;

/**
 * Created by ajoshi on 03-Oct-16.
 */
public class MonetaryAmountCompositeUserType implements CompositeUserType {
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
        BigDecimal value = rs.getBigDecimal(names[0]);
        if(rs.wasNull()) return null;
        Currency currency = Currency.getInstance(rs.getString(names[1]));
        return new MonetaryAmount(value, currency);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if(value == null) {
            st.setNull(index, BIG_DECIMAL.sqlType());
            st.setNull(index + 1, CURRENCY.sqlType());
        } else {
            MonetaryAmount amount = (MonetaryAmount) value;
            String currencyCode = amount.getCurrency().getCurrencyCode();
            st.setBigDecimal(index, amount.getAmount());
            st.setString(index + 1, currencyCode);
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
    public String[] getPropertyNames() {
        return new String[]{"amount", "currency"};
    }

    @Override
    public Type[] getPropertyTypes() {
        return new Type[]{BIG_DECIMAL, CURRENCY};
    }

    @Override
    public Object getPropertyValue(Object component, int property) throws HibernateException {
        MonetaryAmount monetaryAmount = (MonetaryAmount) component;
        return property == 0 ? monetaryAmount.getAmount() : monetaryAmount.getCurrency();
    }

    @Override
    public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
        throw new UnsupportedOperationException("Immutable MonetaryAmount");
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return original;
    }
}
