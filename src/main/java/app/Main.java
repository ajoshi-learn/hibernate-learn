package app;

import app.chapter2.customtypes.customusertype.entities.Product;
import app.chapter2.mappingtypes.entities.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.BitSet;

/**
 * Created by ajoshi on 12-Sep-16.
 */
public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateConfigurator.getSessionFactory();
        Session session = sessionFactory.openSession();
        testBitSetUserType(session);
        session.close();
        sessionFactory.close();
    }

    private static void testSimpleEntitySaving(Session session) {
        Employee employee = new Employee();
        employee.setName("aj");
        employee.setSex(true);
        session.save(employee);
    }

//    private static void testBigIntegerCustomType(Session session) {
//        Product product = new Product();
//        product.setNumber(BigInteger.ZERO);
//        Serializable saved = session.save(product);
//        System.out.println(saved);
//    }

    private static void testBitSetUserType(Session session) {
        Product product = new Product();
        product.setId(1);
        product.setBitSet(BitSet.valueOf(new byte[]{1, 2, 3}));
        session.persist(product);
    }
}
