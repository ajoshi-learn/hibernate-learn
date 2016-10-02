# Hibernate notes

### Table of contents
1. [Mapping persistent classes](#mapping-persistent-classes) 
    * [Entities and value types](#entities-values-types) 
        + [Hibernate types](#hibernate-types) 
    * [Mapping entities with identity](#mapping-entities-identity) 
        + [Id generators](#id-generators) 
    * [Class mapping options](#class-mapping-options) 
        + [Dynamic SQL generation](#dynamic-sql-generation) 
        + [Making an entity immutable](#immutable-entity) 
        + [Customizing property access](#property-access) 
        + [Using derived properties](#derived-properties) 
        + [Generated and default property values](#generated-default-property-values) 
        + [Annotating embedded classes](#annotating-embedded-classes) 
2. [Mapping class inheritance and custom types](#mapping-class-inheritance-and-custom-types) 
    * [Mapping class inheritance](#mapping-class-inheritance) 
        + [Table per class with implicit polymorphism](#table-per-class-with-implicit-polymorphism) 
        + [Table per concrete class with unions](#table-per-concrete-class) 
        + [Table per class hierarchy](#table-per-class-hierarchy) 
        + [Table per subclass](#table-per-subclass) 
        + [Mixing inheritance strategies](#"mixing-inheritance-strategies) 
        + [Choosing a strategy](#choosing-strategy) 
    * [The Hibernate type system](#hibernate-type-system)
    
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

<a name="mapping-persistent-classes"/>
## Mapping persistent classes

<a name="entities-values-types"/>
### Entities and value types

<a name="hibernate-types"/>
#### Hibernate types
Hibernate categorizes types into two groups:
* Value types
* Entity types

**Value types**
A value type is a piece of data that does not define its own lifecycle. It is, in effect, owned by an entity, which defines its lifecycle.
Looked at another way, all the state of an entity is made up entirely of value types. These state fields or JavaBean properties are termed *persistent attributes*. The persistent attributes of the `Contact` class are value types.

**Entity types**
Entities, by nature of their unique identifier, exist independently of other objects whereas values do not. Entities are domain model classes which correlate to rows in a database table, using a unique identifier. Because of the requirement for a unique identifier, entities exist independently and define their own lifecycle. The `Contact` class itself would be an example of an entity.

Hint: First of all try to make everything as value-typed class and promote it to an entity only when absolutely necessary.
As the next step you should care about three things:
* _Shared references_: Write your POJO classes in a way that avoids shared references to value type instances. For example, make sure an `Address` object can be referenced by only one `User`. For example, make it immutable and enforce the relationship with the `Address` constructor
* _Lifecycle dependencies_: As discussed, the lifecycle of a value-type instance is bound to that of its owning entity instance. If a User object is deleted, its `Address` dependent object(s) have to be deleted as well.
* _Identity_: Entity classes need an identifier property in almost all cases. User-defined value-type classes (and JDK classes) don’t have an identifier property, because instances are identified through the owning entity.

<a name="mapping-entities-identity"/>
### Mapping entities with identity

<a name="id-generators"/>
#### Id generators
| Generator name | JPA GenerationType | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|----------------|--------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| native         | AUTO               | The native identity generator picks other identity generators like identity,sequence, or hilo, depending on the capabilities of the underlying database. Use this generator to keep your mapping metadata portable to different database management systems.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| identity       | IDENTITY           | This generator supports identity columns in DB2, MySQL, MS SQL Server, Sybase, and HypersonicSQL. The returned identifier is of type long, short, or int.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| sequence       | SEQUENCE           | This generator creates a sequence in DB2, PostgreSQL, Oracle, SAP DB, or Mckoi; or a generator in InterBase is used. The returned identifier is of type long, short, or int. Use the sequence option to define a catalog name for the sequence (hibernate_ sequence is the default) and parameters if you need additional settings creating a sequence to be added to the DDL.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| increment      | (Not available)    | At Hibernate startup, this generator reads the maximum (numeric) primary key column value of the table and increments the value by one each time a new row is inserted. The generated identifier is of type long, short, or int. This generator is especially efficient if the single-server Hibernate application has exclusive access to the database but should not be used in any other scenario.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| hilo           | (Not available)    | A high/low algorithm is an efficient way to generate identifiers of type long, given a table and column (by default hibernate_unique_key and next, respectively) as a source of high values. The high/low algorithm generates identifiers that are unique only for a particular database. High values are retrieved from a global source and are made unique by adding a local low value. This algorithm avoids congestion when a single source for identifier values has to be accessed for many inserts. See “Data Modeling 101” (Ambler, 2002) for more information about the high/low approach to unique identifiers. This generator needs to use a separate database connection from time to time to retrieve high values, so it isn’t supported with user-supplied database connections. In other words, don’t use it with sessionFactory.openSession(myCo nnection). The max_lo option defines how many low values are added until a new high value is fetched. Only settings greater than 1 are sensible; the default is 32767 (Short.MAX_VALUE). |
| seqhilo        | (Not available)    | This generator works like the regular hilo generator, except it uses a named database sequence to generate high values.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| (JPA only)     | TABLE              | Much like Hibernate’s hilo strategy, TABLE relies on a database table that holds the lastgenerated integer primary key value, and each generator is mapped to one row in this table. Each row has two columns: pkColumnName and valueColumnName. The pkColumnValue assigns each row to a particular generator, and the value column holds the last retrieved primary key. The persistence provider allocates up to allocationSize integers in each turn.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| uuid.hex       | (Not available)    | This generator is a 128-bit UUID (an algorithm that generates identifiers of type string, unique within a network). The IP address is used in combination with a unique timestamp. The UUID is encoded as a string of hexadecimal digits of length 32, with an optional separator string between each component of the UUID representation. Use this generator strategy only if you need globally unique identifiers, such as when you have to merge two databases regularly.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| guid           | (Not available)    | This generator provides a database-generated globally unique identifier string on MySQL and SQL Server.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| select         | (Not available)    | This generator retrieves a primary key assigned by a database trigger by selecting the row by some unique key and retrieving the primary key value. An additional unique candidate key column is required for this strategy, and the key option has to be set to the name of the unique key column.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |

If you want to use Hibernate identifier, that is not available in JPA, you can use `org.hibernate.annotations.GenericGenerator` annotation:
```
@Entity
@org.hibernate.annotations.GenericGenerator(
 name = "hibernate-uuid",
 strategy = "uuid"
)
class name MyEntity {
 @Id
 @GeneratedValue(generator = "hibernate-uuid")
 @Column(name = "MY_ID")
 String id;
}
```

If you want to make your custom sequence generator, you should use `SequenceGenerator` annotation:
```
@Entity
@SequenceGenerator(name = "MY_SEQ", initialValue = 123, allocationSize = 20)
class name MyEntity {
    @Id @GeneratedValue(generator = "mySequenceGenerator")
    String id;
}
```

To create own identifier generator `IdentifierGenerator` interface should be implemented
 
<a name="class-mapping-options"/>
### Class mapping options
 
 <a name="dynamic-sql-generation"/>
#### Dynamic SQL generation
In some situations, such as a legacy table with hundreds of columns where the SQL statements will be large for even the simplest operations (say, only one column needs updating), you have to turn off this startup SQL generation and switch to dynamic statements generated at runtime.
To disable dynamic insertion/updating you have to use `@DynamicInsert` and `@DynamicUpdate` annotations
It is also useful for immutable classes

<a name="immutable-entity"/>
#### Making an entity immutable
Just put `@Immutable` annotation under the entity. It will entail avoiding dirty checking, for example.

To exclude field from entity you can mark property with the `@Transient` annotation or use `transient` keyword

If a property of persistent class isn't annotated, the following rules apply:
* If the property of a JDK type, it's automatically persistent;
* Otherwise, if the class of the property is annotated as `@Embeddable`, it's mapped as a component of the owning class;
* Otherwise, if the type of the property is `Serializable` its value is stored in its serialized form. It's usually not you want!

To customize this rules apply the `@Basic` annotation

<a name="property-access"/>
#### Customizing property access
* If `AccessType` is set on the class level, all attributes of the class are accessed according to the selected strategy.
* If an entity defaults or is explicitly set for field access, the `AccessType("property")` annotation on a field switches this particular attribute to runtime access through property getter/setter methods. The position of the `AccessType` annotation is still the field.
* If an entity defaults or is explicitly set for property access, the `AccessType("field")` annotation on a getter method switches this particular attribute to runtime access through a field of the same name. The position of the `AccessType` annotation is still the getter method.
* Any `@Embedded` class inherits the default or explicitly declared access strategy of the owning root entity class.
* Any `@MappedSuperclass` properties are accessed with the default or explicitly declared access strategy of the mapped entity class.

<a name="derived-properties"/>
#### Using derived properties
```
@org.hibernate.annotations.Formula("TOTAL + TAX_RATE * TOTAL")
public BigDecimal getTotalIncludingTax() {
    return totalIncludingTax;
}
```

<a name="generated-default-property-values"/>
#### Generated and default property values
```
@Column(updatable = false, insertable = false)
@org.hibernate.annotations.Generated(
 org.hibernate.annotations.GenerationTime.ALWAYS
)
private Date lastModified;
```

```
@Column(name = "INITIAL_PRICE",
 columnDefinition = "number(10,2) default '1'")
@org.hibernate.annotations.Generated(
 org.hibernate.annotations.GenerationTime.INSERT
)
private BigDecimal initalPrice;
```

<a name="annotating-embedded-classes"/>
#### Annotating embedded classes
[Mapping example(Address and User classes)](src/main/java/app/book/entities/)

<a name="mapping-class-inheritance-and-custom-types"/>
## Mapping class inheritance and custom types

<a name="mapping-class-inheritance"/>
### Mapping class inheritance
There are four different approaches to representing an inheritance hierarchy:
* Table per concrete class with implicit polymorphism—Use no explicit inheritance mapping, and default runtime polymorphic behavior.
* Table per concrete class—Discard polymorphism and inheritance relationships completely from the SQL schema
* Table per class hierarchy—Enable polymorphism by denormalizing the SQL schema, and utilize a type discriminator column that holds type information.
* Table per subclass—Represent is a (inheritance) relationships as has a (foreign key) relationships. 

<a name="table-per-class-with-implicit-polymorphism"/>
#### _Table per class with implicit polymorphism_
You can use exactly one table for each (nonabstract) class. All properties of a class, including inherited properties, can be mapped to columns of this table, as shown in figure.
![alt tag](readmeImgs/implicitPolymorphism.png)

The main problem with this approach is that it doesn’t support polymorphic associations very well. In the database, associations are usually represented as foreign key relationships. In figure 5.1, if the subclasses are all mapped to different tables, a polymorphic association to their superclass (abstract BillingDetails in this example) can’t be represented as a simple foreign key relationship. This would be problematic in our domain model, because BillingDetails is associated with User; both subclass tables would need a foreign key reference to the USERS table. Or, if User had a many-to-one relationship with BillingDetails, the USERS table would need a single foreign key column, which would have to refer both concrete subclass tables. This isn’t possible with regular foreign key constraints. 

[Mapping example(Address and User classes)](src/main/java/app/book/entities/inheritanceexamples/implicitpolymorphism)

<a name="table-per-concrete-class"/>
#### _Table per concrete class with unions_
1. An abstract superclass or an interface has to be declared as abstract="true"; otherwise
a separate table for instances of the superclass is needed.
2. The database identifier mapping is shared for all concrete classes in the hierarchy.
The CREDIT_CARD and the BANK_ACCOUNT tables both have a BILLING_DETAILS_ID
primary key column. The database identifier property now has to be shared for all
subclasses; hence you have to move it into BillingDetails and remove it from
CreditCard and BankAccount.
3. Properties of the superclass (or interface) are declared here and inherited by all
concrete class mappings. This avoids duplication of the same mapping.
4. A concrete subclass is mapped to a table; the table inherits the superclass (or
interface) identifier and other property mappings. 

[Mapping example(Address and User classes)](src/main/java/app/book/entities/inheritanceexamples/tableperclass)

<a name="table-per-class-hierarchy"/>
#### _Table per class hierarchy_
![alt tag](readmeImgs/tableHierarchy.png)

There is one major problem: Columns for properties declared by subclasses must be declared to be nullable.
[Mapping example(Address and User classes)](src/main/java/app/book/entities/inheritanceexamples/tableperclasshierarchy)

Also you can write `@DiscriminatorFormula` instead of `@DiscriminatorValue` in subclasses:
```
@org.hibernate.annotations.DiscriminatorFormula(
 "case when CC_NUMBER is not null then 'CC' else 'BA' end"
)
```

<a name="table-per-subclass"/>
#### _Table per subclass_
The fourth option is to represent inheritance relationships as relational foreign key associations. Every class/subclass that declares persistent properties—including abstract classes and even interfaces—has its own table.
![alt tag](readmeImgs/tablePerSubclass.jpg)

<a name="mixing-inheritance-strategies"/>
#### _Mixing inheritance strategies_
![alt tag](readmeImgs/breakingSubclass.jpg)
```
@Entity
@DiscriminatorValue("CC")
@SecondaryTable(
 name = "CREDIT_CARD",
 pkJoinColumns = @PrimaryKeyJoinColumn(name = "CREDIT_CARD_ID")
)
public class CreditCard extends BillingDetails {
 @Column(table = "CREDIT_CARD",
 name = "CC_NUMBER",
 nullable = false)
 private String number;
 ...
}
```

<a name="choosing-strategy"/>
#### Choosing a strategy
* If you don’t require polymorphic associations or queries, lean toward tableper-concrete-class—in other words, if you never or rarely query for BillingDetails and you have no class that has an association to BillingDetails (our model has). An explicit UNION-based mapping should be preferred, because (optimized) polymorphic queries and associations will then be possible later. Implicit polymorphism is mostly useful for queries utilizing non-persistence-related interfaces.
* If you do require polymorphic associations (an association to a superclass, hence to all classes in the hierarchy with dynamic resolution of the concrete class at runtime) or queries, and subclasses declare relatively few properties (particularly if the main difference between subclasses is in their behavior), lean toward table-per-class-hierarchy. Your goal is to minimize the number of nullable columns and to convince yourself (and your DBA) that a denormalized schema won’t create problems in the long run.
* If you do require polymorphic associations or queries, and subclasses declare many properties (subclasses differ mainly by the data they hold), lean toward table-per-subclass. Or, depending on the width and depth of your inheritance hierarchy and the possible cost of joins versus unions, use table-per-concrete-class.
By default, choose table-per-class-hierarchy only for simple problems. For more complex cases (or when you’re overruled by a data modeler insisting on the importance of nullability constraints and normalization), you should consider the table-per-subclass strategy.

<a name="hibernate-type-system"/>
### The Hibernate type system

<a name="build-in-mapping-types"/>
#### Built-in mapping types

| **Mapping type** | **Java type**      | **SQL built-in type** |
|------------------|--------------------|-----------------------|
| integer          | int or Integer     | INTEGER               |
| long             | long or Long       | BIGINT                |
| short            | short or Short     | SMALLINT              |
| float            | float or Float     | FLOAT                 |
| double           | double or Double   | DOUBLE                |
| big_decimal      | BigDecimal         | NUMERIC               |
| character        | String             | CHAR(1)               |
| string           | String             | VARCHAR               |
| byte             | byte or Byte       | TINYINT               |
| boolean          | boolean or Boolean | BIT                   |
| yes_no           | boolean or Boolean | CHAR(1) ('Y' or 'N' ) |
| true_false       | boolean or Boolean | CHAR(1) ('T' or 'F')  |