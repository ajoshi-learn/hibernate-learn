# Hibernate notes

Hibernate (and JPA) require a constructor with no arguments for every persistent class. Hibernate calls persistent classes using Reflection API to init objects.
Constructor may be non public, but it has to be at least package-visible. Proxy generation also requires that the class isn't declared final
Hibernate requires interfaces for collection-typed attributes

Hibernate automatically detects object state changes in order to synchronize the updated state with the database.
It’s usually safe to return a different object from the getter method than the object passed by Hibernate to the setter. Hibernate compares the objects by value—not by object identity—to determine whether the property’s persistent state needs to be updated. For example, the following getter method doesn’t result in unnecessary SQL UPDATEs:

```
public String getFirstname() {
 return new String(firstname);
}
```
There is one important exception to this: Collections are compared by identity! For a property mapped as a persistent collection, you should return exactly the same collection instance from the getter method that Hibernate passed to the setter method. If you don’t, Hibernate will update the database, even if no update is necessary, every time the state held in memory is synchronized with the database.

If a `RuntimeException` is thrown, the current transaction is rolled back, and the exception is yours to handle.
If a checked application exception is thrown, Hibernate wraps the exception into a `RuntimeException`

It is possible to manipulate metadata in runtime:

```
// Get the existing mapping for User from Configuration
PersistentClass userMapping =
 cfg.getClassMapping(User.class.getName());
 
// Define a new column for the USER table
Column column = new Column();
column.setName("MOTTO");
column.setNullable(false);
column.setUnique(true);
userMapping.getTable().addColumn(column);

// Wrap the column in a Value
SimpleValue value = new SimpleValue();
value.setTable( userMapping.getTable() );
value.setTypeName("string");
value.addColumn(column);

// Define a new property of the User class
Property prop = new Property();
prop.setValue(value);
prop.setName("motto");
prop.setNodeName(prop.getName());
userMapping.addProperty(prop);

// Build a new session factory, using the new mapping
SessionFactory sf = cfg.buildSessionFactory();
```

A `PersistentClass` object represent the metamodel for a single persisten class.
`Column`, 'SimpleValue' nad 'Property' are all classes of Hibernate metamodel
Note that once a `SessionFactory` is created its mapping are immutable. There is no way to make changes in metamodel, however that application can read metamodel by calling `getClassMetadata`:

```
Item item = ...;
ClassMetadata meta = sessionFactory.getClassMetadata(Item.class);
String[] metaPropertyNames =
 meta.getPropertyNames();
Object[] propertyValues =
 meta.getPropertyValues(item, EntityMode.POJO);
 ```
