package app;

import app.book.entities.ternaryassociations.Account;
import app.book.entities.ternaryassociations.Category;
import app.book.entities.ternaryassociations.Item;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by ajoshi on 12-Sep-16.
 */
public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateConfigurator.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        saveTernary(session);
        session.getTransaction().commit();
        session.close();
        sessionFactory.close();
    }

    private static void saveTernary(Session session) {
        Account account = new Account();
        account.setUsername("ajoshi");
        session.save(account);

        Item item = new Item();
        item.setItemName("item1");
        session.save(item);

        Category category = new Category();
        category.setCategoryName("category1");
        category.getItemsAccounts().put(item, account);
        session.save(category);
    }

//    private static void saveUser(Session session) {
//        Category user = new Category();
//        user.setName("artur joshi");
//        session.save(user);
//        session.flush();
//    }

//    private static void saveBid(Session session) {
//        Bid bid = new Bid();
//        bid.setField1("field1");
//        bid.setField2("field2");
//        session.save(bid);
//        System.out.println(session.get(Bid.class, 1L));
//    }

//    private static void testInheritance(Session session) {
//        CreditCard creditCard = new CreditCard();
//        creditCard.setOwner("ajoshi");
//        creditCard.setNumber("123123");
//        session.save(creditCard);
//    }
//
//    private static void testCustomUserType(Session session) {
//        Item item = new Item();
//        item.setName("testItem");
//        MonetaryAmount initialPrice = new MonetaryAmount(BigDecimal.TEN, Currency.getInstance("USD"));
//        item.setInitialPrice(initialPrice);
//        session.save(item);
//    }

//    private static void testOneToOneAssociation(Session session) {
//        Category newUser = new Category();
//        newUser.setName("name");
//        Address shippingAddress = new Address();
//        shippingAddress.setAddress("address");
//        newUser.setShippingAddress(shippingAddress);
//        shippingAddress.setUser(newUser); // Bidirectional
//        session.save(shippingAddress);
//        session.save(newUser);
//    }

//    private static void testTablePerClass(Session session) {
//        CreditCard creditCard = new CreditCard();
//        creditCard.setId(1L);
//        creditCard.setOwner("ajoshi");
//        creditCard.setNumber("12321");
//        session.save(creditCard);
//
//        CreditCard loadedCreditCard = session.load(CreditCard.class, 2l);
//        System.out.println(loadedCreditCard);
//    }

//    private static void testSimpleEntitySaving(Session session) {
//        Employee employee = new Employee();
//        employee.setName("kk");
//        employee.setSex(true);
//        employee.setPhoneType(PhoneType.MOBILE);
//        session.save(employee);
//    }
//
//    private static void testCustomBasicType(Session session) {
//        BitSet bitSet = BitSet.valueOf(new long[]{10, 20, 30});
//        Product product = new Product();
//        product.setId(1);
//        product.setBitSet(bitSet);
//        session.save(product);
//    }
//
//    private static void testEnums(Session session) {
//        Phone phone = new Phone();
//        phone.setId(1L);
//        phone.setNumber("0961212314");
//        phone.setPhoneType(PhoneType.MOBILE);
//        session.save(phone);
//        System.out.println("saved");
//    }
//
//    private static void testEnumsAttributeConverter(Session session) {
//        Person person = new Person();
//        person.setName("aj");
//        person.setGender(Gender.MALE);
//        session.save(person);
//    }

//    private static void testClobs(Session session) {
//        String warranty = "My product warranty";
//        Product product = new Product();
//        product.setId(1);
//        product.setName("Phone");
//        session.doWork(connection -> product.setWarranty(ClobProxy.generateProxy(warranty)));
//        session.doWork(connection -> product.setImage(BlobProxy.generateProxy(new byte[]{1, 2, 3})));
//        Serializable saved = session.save(product);
//        System.out.println(saved);

//        Reading clobs
//        Product product = entityManager.find( Product.class, productId );
//        try (Reader reader = product.getWarranty().getCharacterStream()) {
//            assertEquals( "My product warranty", toString( reader ) );
//        }

//        Reading blobs
//        Product product = entityManager.find( Product.class, productId );
//
//        try (InputStream inputStream = product.getImage().getBinaryStream()) {
//            assertArrayEquals(new byte[] {1, 2, 3}, toBytes( inputStream ) );
//        }
}
