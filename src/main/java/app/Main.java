package app;

import app.chapter2.mappingtypes.entities.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by ajoshi on 12-Sep-16.
 */
public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateConfigurator.getSessionFactory();
        Session session = sessionFactory.openSession();
        Employee employee = new Employee();
        employee.setName("aj");
        employee.setSex(true);
        session.save(employee);
        session.close();
        sessionFactory.close();
    }
}
