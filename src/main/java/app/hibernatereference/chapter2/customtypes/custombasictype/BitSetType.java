package app.hibernatereference.chapter2.customtypes.custombasictype;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import java.util.BitSet;

public class BitSetType extends AbstractSingleColumnStandardBasicType<BitSet> implements DiscriminatorType<BitSet> {

    public static final BitSetType INSTANCE = new BitSetType();

    public BitSetType() {
        super(VarcharTypeDescriptor.INSTANCE, BitSetTypeDescriptor.INSTANCE);
    }

    @Override
    public BitSet stringToObject(String s) throws Exception {
        return fromString(s);
    }

    @Override
    public String objectToSQLString(BitSet bitSet, Dialect dialect) throws Exception {
        return toString(bitSet);
    }

    @Override
    public String getName() {
        return "bitset";
    }
}
