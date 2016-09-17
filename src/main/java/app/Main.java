package app;

import app.chapter2.customtypes.custombasictype.entities.Product;
import app.chapter2.enums.attributeconverterexample.enities.Gender;
import app.chapter2.enums.attributeconverterexample.enities.Person;
import app.chapter2.mappingtypes.entities.Employee;
import app.chapter2.enums.enumssimpleexample.Phone;
import app.chapter2.enums.enumssimpleexample.PhoneType;
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
        testEnumsAttributeConverter(session);
        session.close();
        sessionFactory.close();
    }

    private static void testSimpleEntitySaving(Session session) {
        Employee employee = new Employee();
        employee.setName("kk");
        employee.setSex(true);
        employee.setPhoneType(PhoneType.MOBILE);
        session.save(employee);
    }

    private static void testCustomBasicType(Session session) {
        BitSet bitSet = BitSet.valueOf(new long[]{10, 20, 30});
        Product product = new Product();
        product.setId(1);
        product.setBitSet(bitSet);
        session.save(product);
    }

    private static void testEnums(Session session) {
        Phone phone = new Phone();
        phone.setId(1L);
        phone.setNumber("0961212314");
        phone.setPhoneType(PhoneType.MOBILE);
        session.save(phone);
        System.out.println("saved");
    }

    private static void testEnumsAttributeConverter(Session session) {
        Person person = new Person();
        person.setName("aj");
        person.setGender(Gender.MALE);
        session.save(person);
    }
}
