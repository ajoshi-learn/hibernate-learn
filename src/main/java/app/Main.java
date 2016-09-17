package app;

import app.chapter2.customtypes.custombasictype.entities.Product;
import app.chapter2.mappingtypes.entities.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.BitSet;

/**
 * Created by ajoshi on 12-Sep-16.
 */
public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateConfigurator.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.close();
        sessionFactory.close();
    }

    private static void testSimpleEntitySaving(Session session) {
        Employee employee = new Employee();
        employee.setName("aj");
        employee.setSex(true);
        session.save(employee);
    }

    private static void testCustomBasicType(Session session) {
        BitSet bitSet = BitSet.valueOf(new long[]{1, 2, 3});
        Product product = new Product();
        product.setId(1);
        product.setBitSet(bitSet);
        session.persist(product);
    }
}
