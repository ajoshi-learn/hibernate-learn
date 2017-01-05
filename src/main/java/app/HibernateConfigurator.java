package app;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by ajoshi on 14-Sep-16.
 */
public class HibernateConfigurator {
    private static SessionFactory sessionFactory;

    static {
//        Configuration configuration = new Configuration().configure();
////        configuration.registerTypeContributor(((typeContributions, serviceRegistry) -> {
////            typeContributions.contributeType(BitSetType.INSTANCE);
////        }));
//        sessionFactory = configuration.buildSessionFactory();

        sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
