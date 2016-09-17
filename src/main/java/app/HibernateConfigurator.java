package app;

import app.chapter2.customtypes.custombasictype.BitSetType;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by ajoshi on 14-Sep-16.
 */
public class HibernateConfigurator {
    private static SessionFactory sessionFactory;

    static {
        Configuration configuration = new Configuration().configure();
//        configuration.registerTypeContributor(((typeContributions, serviceRegistry) -> {
//            typeContributions.contributeType(BitSetType.INSTANCE);
//        }));
        sessionFactory = configuration.buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
