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
        + [Built-in mapping types](#build-in-mapping-types)
        + [Date and time mapping types](#date-time-mapping-types)
        + [Binary and large value mapping types](#binary-large-mapping-types)
        + [JDK mapping types](#jdk-mapping-types)
        + [Using mapping types](#using-mapping-types)
    * [Creating custom mapping types](#custom-mapping-system)
        + [The extension points](#extensions-points)
        + [Creating a UserType](#creating-user-type)
        + [Creating a CompositeUserType](#creating-composite-user-type)
        + [Parameterizing custom types](#parameterizing-custom-type)
        + [Mapping enumerations](#mapping-enums)
3. [Mapping collections and entity associations](#mapping-collections-and-entity-associations)
    * [Sets, bags, lists, and maps of value types](#sets-bags-lists-maps)
        + [Basic collection mapping](#basic-collection-mapping)
        + [Sorted and ordered collections](#sorted-and-ordered-collections)
    * [Mapping a parent-child relationship](#mapping-parent-child-associations)
        + [The simplest possible association](#simplest-association)
        + [Cascading object state](#cascading-object-state)
4. [Advanced entity association mappings](#advanced-entity-association-mappings)
    * [Single-valued entity associations](#single-valued-associations)
        + [One-to-one foreign key associations](#one-to-one-foreign)
        + [One-to-one with join table](#one-to-one-jointable)
    * [Many-valued entity associations](#many-valued-associations)
        + [One-to-many associations](#one-to-many-associations)
        + [Many-to-many associations](#many-to-many-associations)
        + [Mapping maps](#mapping-maps)
        + [Ternary associations](#ternary-associations)
5. [Working with objects](#working-with-object)
    * [The persistence lifecycle](#persistent-lifecycle)
        + [The persistence context](#persistence-context)
    * [Object identity and equality](#identity-equality)
        + [The scope of object identity](#object-identity-scope)
    * [The Hibernate interfaces](#hibernate-interfaces)
        + [Storing and loading objects](#objects-storing-loading)
        + [Working with detached objects](#working-with-detached-objects)
6. [Transactions and concurrency](#transactions-and-concurrency)
    * [Transaction essentials](#transactions-essentials)
        + [Transactions in a Hibernate application](#transactions-in-hibernate)
    * [Controlling concurrent access](#controlling-concurrent-access)
        + [Transaction isolation issues](#isolation-issues)
        + [ANSI transaction isolation levels](#isolation-levels)
        + [Setting an isolation level](#setting-isolation-level)
        + [Optimistic concurrency control](#optimistic-concurrency-control)
        + [Obtaining additional isolation guarantees](#additional-isolation)
7. [Optimizing fetching and caching](#fetching-caching-optimizing)
    * [Defining the global fetch plan](#global-fetch-plan)
        + [The object-retrieval options](#object-retrieval-options)
    * [Selecting a fetch strategy](#select-fetch-strategy)
        + [Prefetching data in batches](#batch-prefetching)
        + [Prefetching collections with subselects](#subselects-prefetching)
    * [Caching fundamentals](#caching-fundamentals)
        + [Caching strategies and scopes](#сaching-strategies-scopes)
        + [The Hibernate cache architecture](#cache-architecture)
    * [Caching in practice](#caching-in-practice)
        + [Selecting a concurrency control strategy](#concurrency-control-strategy)
        + [Controlling the second-level cache](#controlling-second-level-cache)
8. [Querying with HQL and JPA QL](#querying-with-hql)
    * [Creating and running queries](#creating-running-queries)
    * [Basic HQL and JPA QL queries](#basic-hql-queries)
    * [Joins, reporting queries, and subselects](#joins-reporting-queries)
9. [Querying with Criteria](#criteria-querying)
    * [Basic criteria queries](#basic-criteria-queries)
    * [Using native SQL queries](#native-sql)


<hr>
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
* _Shared references_: Write your POJO classes in a way that avoids shared references to value type instances. For example, make sure an `Address` object can be referenced by only one `Category`. For example, make it immutable and enforce the relationship with the `Address` constructor
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

<a name="date-time-mapping-types"/>

#### Date and time mapping types

| **Mapping type** | **Java type**                        | **SQL built-in type** |
|------------------|--------------------------------------|-----------------------|
| date             | java.util.Date or java.sql.Date      | DATE                  |
| time             | java.util.Date or java.sql.Time      | TIME                  |
| timestamp        | java.util.Date or java.sql.Timestamp | TIMESTAMP             |
| calendar         | java.util.Calendar                   | TIMESTAMP             |
| calendar_date    | java.util.Calendar                   | DATE                  |

<a name="binary-large-mapping-types"/>

#### Binary and large value mapping types

| **Mapping type** | **Java type**           | **SQL built-in type** |
|------------------|-------------------------|-----------------------|
| binary           | byte[]                  | VARBINARY             |
| text             | java.lang.String        | CLOB                  |
| clob             | java.sql.Clob           | CLOB                  |
| blob             | java.sql.Blob           | BLOB                  |
| serializable     | implements Serializable | VARBINARY             |

If you want to map some `String` or `char[]` as `Lob` you have to put `@Lob` annotation under the field.
Also it can be applied to `byte[]` or `Byte[]`

<a name="jdk-mapping-types"/>

#### JDK mapping types

| **Mapping type** | **Java type**           | **SQL built-in type** |
|------------------|-------------------------|-----------------------|
| class            | Class                   | VARCHAR               |
| locale           | java.util.Locale        | VARCHAR               |
| timezone         | java.util.TimeZone      | VARCHAR               |
| currency         | java.util.Currency      | VARCHAR               |

<a name="using-mapping-types"/>

####  Using mapping types

The mapping type of a property is automatically detected, just like in Hibernate. For a java.util.Date or java.util.Calendar property, the Java Persistence standard requires that you select the precision with a @Temporal annotation:
```
@Temporal(TemporalType.TIMESTAMP)
@Column(nullable = false, updatable = false)
private Date startDate;
```

In other rare cases, you may want to add the `@org.hibernate.annotations.Type` annotation to a property and declare the name of a built-in or custom Hibernate mapping type explicitly

<a name="custom-mapping-system"/>

### Creating custom mapping types

<a name="extensions-points"/>

#### The extension points

Hibernate provides several interfaces that applications may use to define custom mapping types:
* `org.hibernate.usertype.UserType` provides the basic methods for custom loading and storing of value type instances.
* `org.hibernate.usertype.CompositeUserType` - an interface with more methods than the basic UserType, used to expose internals about your value type class to Hibernate, such as the individual properties.
* `org.hibernate.usertype.UserCollectionType` - a rarely needed interface that’s used to implement custom collections.
* `org.hibernate.usertype.EnhancedUserType` - an interface that extends UserType and provides additional methods for marshalling value types toand from XML representations, or enables a custom mapping type for use in identifier and discriminator mappings.
* `org.hibernate.usertype.UserVersionType` - an interface that extends UserType and provides additional methods enabling the custom mapping type for usage in entity version mappings.
* `org.hibernate.usertype.ParameterizedType` - a useful interface that can be combined with all others to provide configuration settings—that is, parameters defined in metadata. For example, you can write a single MoneyConverter that knows how to translate values into Euro or US dollars, depending on a parameter in the mapping.

<a name="creating-user-type"/>

#### Creating a UserType

In order to create custom UserType you should implement `UserType` interface
[MonetaryAmountUserType example](src/main/java/app/book/entities/custommappingtypes/usertypes/)

1. The `sqlTypes()` method tells Hibernate what SQL column types to use for DDL schema generation. Notice that this method returns an array of type codes.
2. The `returnedClass()` method tells Hibernate what Java value type class is mapped by this `UserType`.
3. The `UserType` is also partially responsible for creating a snapshot of a value in the first place. Because `MonetaryAmount` is an immutable class, the `deepCopy()` method returns its argument. In the case of a mutable type, it would need to return a copy of the argument to be used as the snapshot value.
4. The `disassemble()` method is called when Hibernate puts a `MonetaryAmount` into the second-level cache. As you’ll learn later, this is a cache of data that stores information in a serialized form.
5. The `assemble()` method does the opposite of disassembly: It can transform cached data into an instance of MonetaryAmount. As you can see, implementation of both routines is easy for immutable types.
6. Implement `replace()` to handle merging of detached object state.
7. The `nullSafeGet()` method retrieves the property value from the JDBC `ResultSet`.
8. The `nullSafeSet()` method writes the property value to the JDBC `PreparedStatement`.

<a name="creating-composite-user-type"/>

#### Creating a CompositeUserType

In order to create custom CompositeUserType you should implement `CompositeUserType` interface
[MonetaryAmountCompositeUserType example](src/main/java/app/book/entities/custommappingtypes/usertypes/)

1. The `CompositeUserType` interface requires the same housekeeping methods as the `UserType`. However, the `sqlTypes()` method is no longer needed.
2. Loading a value now is straightforward: You transform two column values in the result set to two property values in a new `MonetaryAmount` instance.
3. Saving a value involves setting two parameters on the prepared statement.
4. A `CompositeUserType` exposes the properties of the value type through `getPropertyNames()`.
5. The properties each have their own type, as defined by `getPropertyTypes()`. The types of the SQL columns are now implicit from this method.
6. The `getPropertyValue()` method returns the value of an individual property of the `MonetaryAmount`.
7. The `setPropertyValue()` method sets the value of an individual property of the `MonetaryAmount`. 

<a name="parameterizing-custom-type"/>

#### Parameterizing custom types

[MonetaryAmountConversionType example](src/main/java/app/book/entities/custommappingtypes/usertypes/)

```
@org.hibernate.annotations.TypeDefs({
 @org.hibernate.annotations.TypeDef(
 name="monetary_amount_usd",
 typeClass = persistence.MonetaryAmountConversionType.class,
 parameters = { @Parameter(name="convertTo", value="USD") }
 ),
 @org.hibernate.annotations.TypeDef(
 name="monetary_amount_eur",
 typeClass = persistence.MonetaryAmountConversionType.class,
 parameters = { @Parameter(name="convertTo", value="EUR") }
 )
})
```
This annotation metadata is global, so it can be placed outside any Java class declaration (right after the import statements) or in a separate file, package-info.java. A good location in this system is in a package-info.java file in the persistence package.

```
@org.hibernate.annotations.Type(type = "monetary_amount_eur")
@org.hibernate.annotations.Columns({
 @Column(name = "BID_AMOUNT"),
 @Column(name = "BID_AMOUNT_CUR")
})
private MonetaryAmount bidAmount;
```

<a name="mapping-enums"/>

#### Mapping enumerations

Instead of the most basic `UserType` interface, we now we will use the `EnhancedUserType` interface.
First of all you have to write your custom enumeration handler that should implement `EnhancedUserType` and `ParameterizedType` interfaces
And that map in the entity class:

```
@Enumerated(EnumType.STRING)
 @Column(name = "RATING", nullable = false, updatable = false)
 private Rating rating;
```

<a name="mapping-collections-and-entity-associations"/>

## Mapping collections and entity associations

<a name="sets-bags-lists-maps"/>

### Sets, bags, lists, and maps of value types

Without extending Hibernate you can choose from the following collections:
* `java.util.Set` is mapped with a `<set>` and initialized with a `HashSet`
* `java.util.SortedSet` is mapped with a `<set>` and initialized with a `TreeSet`
* `java.util.List` is mapped with a `<list>` and initialized with a `ArrayList`
* `java.util.Collection` can be mapped with a `<bag>` or `<idbag>` and initialized with a `ArrayList`
* `java.util.Map` is mapped with a `<map>` and initialized with a `HashMap`
* `java.util.SortedMap` is mapped with a `<map>` and initialized with a `TreeMap`

<a name="basic-collection-mapping"/>

#### Basic collection mapping

The following maps a simple collection of `String` elements:
```
@org.hibernate.annotations.CollectionOfElements(
 targetElement = java.lang.String.class
)
@JoinTable(
 name = "ITEM_IMAGE",
 joinColumns = @JoinColumn(name = "ITEM_ID")
)
@Column(name = "FILENAME", nullable = false)
private Set<String> images = new HashSet<String>();
```

To map a persistent `List`, add `@org.hibernate.annotations.IndexColumn` with an optional base for the index (default is zero):
```
@org.hibernate.annotations.CollectionOfElements
@JoinTable(
 name = "ITEM_IMAGE",
 joinColumns = @JoinColumn(name = "ITEM_ID")
)
@org.hibernate.annotations.IndexColumn(
 name="POSITION", base = 1
)
@Column(name = "FILENAME")
private List<String> images = new ArrayList<String>();
```
If you forget the index column, this list would be treated as a bag collection.

To map a persistent map, use `@org.hibernate.annotations.MapKey`:
```
@org.hibernate.annotations.CollectionOfElements
@JoinTable(
 name = "ITEM_IMAGE",
 joinColumns = @JoinColumn(name = "ITEM_ID")
)
@org.hibernate.annotations.MapKey(
 columns = @Column(name="IMAGENAME")
)
@Column(name = "FILENAME")
private Map<String, String> images = new HashMap<String, String>();
```

<a name="sorted-and-ordered-collections"/>

#### Sorted and ordered collections

A collection can also be sorted or ordered with Hibernate annotations:

```
@org.hibernate.annotations.CollectionOfElements
@JoinTable(
 name = "ITEM_IMAGE",
 joinColumns = @JoinColumn(name = "ITEM_ID")
)
@Column(name = "FILENAME", nullable = false)
@org.hibernate.annotations.Sort(
 type = org.hibernate.annotations.SortType.NATURAL
)
private SortedSet<String> images = new TreeSet<String>();
```
The @Sort annotation supports various SortType attributes, with the same semantics as the XML mapping options. The shown mapping uses a java.util.SortedSet (with a java.util.TreeSet implementation) and natural sort order. If you enable SortType. COMPARATOR, you also need to set the comparator attribute to a class that implements your comparison routine.

Maps, sets, and even bags, can be ordered on load, by the database, through an SQL fragment in the ORDER BY clause:
```
@org.hibernate.annotations.OrderBy(
 clause = "FILENAME asc"
)
```

Finally, you can map a collection of components, of user-defined value-typed elements. You need to add the `@Embeddable` component annotation on that class to enable embedding:
```
@Embeddable
public class Image {
 @org.hibernate.annotations.Parent
 Item item;
 @Column(length = 255, nullable = false)
 private String name;
 @Column(length = 255, nullable = false)
 private String filename;
 @Column(nullable = false)
 private int sizeX;
 @Column(nullable = false)
 private int sizeY;
 @org.hibernate.annotations.CollectionOfElements
 @JoinTable(
  name = "ITEM_IMAGE",
  joinColumns = @JoinColumn(name = "ITEM_ID")
 )
 @AttributeOverride(
  name = "element.name",
  column = @Column(name = "IMAGENAME",
  length = 255,
  nullable = false)
 )
 private Set<Image> images = new HashSet<Image>();
```

<a name="mapping-parent-child-associations"/>

### Mapping a parent-child relationship

<a name="simplest-association"/>

#### The simplest possible association

![alt tag](readmeImgs/associations.png)

[BidItem example](src/main/java/app/book/entities/associationsexamples/entities)

`targetEntity` annotation parameter is used to set type explicitly. An explicit `targetEntity` attribute is useful in more complex domain models - for example, when you map a `@ManyToOne` on a getter method that returns a delegate class, which mimics a particular target entity interface.
`@JoinColumn` is also os optional. If it isn't defined, column name will be `<type>_id`

<a name="cascading-object-state"/>

#### Cascading object state

**Transitive persistence**
To implement transitive persistence you have to put `cascade` parameter into `@OneToMany` annotation

[BidItem example](src/main/java/app/book/entities)

Same can be applied for deletion

<a name="advanced-entity-association-mappings"/>

## Advanced entity association mappings

<a name="single-valued-associations"/>

### Single-valued entity associations

![alt tag](readmeImgs/onetoone.png)

<a name="one-to-one-foreign"/>

#### One-to-one foreign key associations

```
@Entity
@Table(name = "addresses")
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @OneToOne(mappedBy = "shippingAddress")
    private User user;
    @Column(name = "address")
    private String address;
}
```

```
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address shippingAddress;
    @Column(name = "user_name")
    private String name;
}
```

<a name="one-to-one-jointable"/>

#### One-to-one with join table

![alt tag](readmeImgs/onetoonejointable.png)

```
public class Shipment {
 @OneToOne
 @JoinTable(
 name="ITEM_SHIPMENT",
 joinColumns = @JoinColumn(name = "SHIPMENT_ID"),
 inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
 )
 private Item auction;
}
```

<a name="many-valued-associations"/>

### Many-valued entity associations

<a name="one-to-many-associations"/>

#### One-to-many associations

One-to-many relation is mapped by `@OneToMany` and `@ManyToOne` annotations. In order to specify join column use `@JoinColumn` annotation
[Mapping example](src/main/java/app/book/entities/manyvaluedexamples)

It is also possible to map one-to-many relationship via additional table:

```
@Entity
public class Item {
 @ManyToOne
 @JoinTable(
 name = "ITEM_BUYER",
 joinColumns = {@JoinColumn(name = "ITEM_ID")},
 inverseJoinColumns = {@JoinColumn(name = "USER_ID")}
 )
 private User buyer;
 ...
}
```

<a name="many-to-many-associations"/>

#### Many-to-many associations

[Mapping example](src/main/java/app/book/entities/manyvaluedexamples)

<a name="mapping-maps"/>

#### Mapping maps

```
@MapKey(name="id")
@OneToMany
private Map<Long,Bid> bidsByIdentifier = new HashMap<Long,Bid>();
```

<a name="ternary-associations"/>

#### Ternary associations

![alt tag](readmeImgs/ternary.png)

```
@ManyToMany
@org.hibernate.annotations.MapKeyManyToMany(
 joinColumns = @JoinColumn(name = "ITEM_ID")
)
@JoinTable(
 name = "CATEGORY_ITEM",
 joinColumns = @JoinColumn(name = "CATEGORY_ID"),
 inverseJoinColumns = @JoinColumn(name = "USER_ID")
)
private Map<Item,User> itemsAndUser = new HashMap<Item,User>();
```

To create a link between all three entities, if all your instances are already in persistent
state, add a new entry to the map:

```
aCategory.getItemsAndUser().add( anItem, aUser );
```

To remove the link, remove the entry from the map. 

<a name="working-with-object"/>

## Working with objects

<a name="persistent-lifecycle"/>

### The persistence lifecycle

![alt tag](readmeImgs/objectStates.png)

**_Transient objects_**

Objects instantiated using the `new` operator aren’t immediately persistent. Their state is _transient_, which means they aren’t associated with any database table row and so their state is lost as soon as they’re no longer referenced by any other object. These objects have a lifespan that effectively ends at that time, and they become inaccessible and available for garbage collection.
Hibernate and Java Persistence consider all transient instances to be nontransactional; any modification of a transient instance isn’t known to a persistence context. This means that Hibernate doesn’t provide any roll-back functionality for transient objects.
 
**_Persistent objects_**
 
A _persistent_ instance is an entity instance with a _database identity_. That means a persistent and managed instance has a primary key value set as its database identifier.
Persistent instances may be objects instantiated by the application and then made persistent by calling one of the methods on the persistence manager. They may even be objects that became persistent when a reference was created from another persistent object that is already managed.
Persistent instances are always associated with a persistence context. Hibernate caches them and can detect whether they have been modified by the application.

**_Removed objects_**

You can delete an entity instance in several ways: For example, you can remove it with an explicit operation of the persistence manager. It may also become available for deletion if you remove all references to it, a feature available only in Hibernate or in Java Persistence with a Hibernate extension setting (orphan deletion for entities).
An object is in the removed state if it has been scheduled for deletion at the end of a unit of work, but it’s still managed by the persistence context until the unit of work completes.
 
**_Detached objects_**
 
_Detached_ state is an object state after closing persistent context, indicating that it's state is no longer guaranteed to be synchronized with database state. 
They still contain persistent data (which may soon be stale). You can continue working with a detached object and modify it. However, at some point you probably want to make those changes persistent—in other words, bring the detached instance back into persistent state.
Hibernate offers two operations, _reattachment_ and _merging_, to deal with this situation. Java Persistence only standardizes merging. These features have a deep impact on how multitiered applications may be designed. The ability to return objects from one persistence context to the presentation layer and later reuse them in a new persistence context is a main selling point of Hibernate and Java Persistence. It enables you to create _long_ units of work that span user think-time. We call this kind of long-running unit of work a _conversation_.

<a name="persistence-context"/>

#### The persistence context

You may consider the persistence context to be a cache of managed entity instances. The persistence context isn’t something you see in your application; it isn’t an API you can call. In a Hibernate application, we say that one `Session` has one internal persistence context. In a Java Persistence application, an `EntityManager` has a persistence context. All entities in persistent state and managed in a unit of work are cached in this context.

##### Automatic dirty checking

Persistent instances are managed in a persistence context—their state is synchronized with the database at the end of the unit of work. When a unit of work completes, state held in memory is propagated to the database by the execution of SQL INSERT, UPDATE, and DELETE statements (DML). This procedure may also occur at other times. For example, Hibernate may synchronize with the database before execution of a query. This ensures that queries are aware of changes made earlier during the unit of work.
Hibernate doesn’t update the database row of every single persistent object in memory at the end of the unit of work. ORM software must have a strategy for detecting which persistent objects have been modified by the application. We call this automatic dirty checking. An object with modifications that have not yet been propagated to the database is considered dirty. Again, this state isn’t visible to the application. With transparent transaction-level write-behind, Hibernate propagates state changes to the database as late as possible but hides this detail from the application. By executing DML as late as possible (toward the end of the database transaction), Hibernate tries to keep lock-times in the database as short as possible. (DML usually creates locks in the database that are held until the transaction completes.)
Hibernate is able to detect exactly which properties have been modified so that it’s possible to include only the columns that need updating in the SQL UPDATE statement. This may bring some performance gains. However, it’s usually not a significant difference and, in theory, could harm performance in some environments.

##### The persistence context cache

A persistence context is a cache of persistent entity instances. This means it remembers all persistent entity instances you’ve handled in a particular unit of work. Automatic dirty checking is one of the benefits of this caching. Another benefit is _repeatable read_ for entities and the performance advantage of a unit of work-scoped cache.
The persistence context cache sometimes helps avoid unnecessary database traffic; but, more important, it ensures that:
* The persistence layer isn’t vulnerable to stack overflows in the case of circular references in a graph of objects
* There can never be conflicting representations of the same database row at the end of a unit of work. In the persistence context, at most a single object represents any database row. All changes made to that object may be safely written to the database.
* Likewise, changes made in a particular persistence context are always immediately visible to all other code executed inside that persistence context and its unit of work (the repeatable read for entities guarantee). 

<a name="identity-equality"/>

### Object identity and equality

There are two strategies to implement Hibernate conversation:

* with detached objects
![alt tag](readmeImgs/detachedObjects.png)
The _detached_ object state and the already mentioned features of reattachment or merging are ways to implement a conversation. Objects are held in detached state during user think-time, and any modification of these objects is made persistent manually through reattachment or merging. This strategy is also called _session-perrequest-with-detached-objects_

* extending persistence context
![alt tag](readmeImgs/extendingPersistenceContext.png)

<a name="object-identity-scope"/>

#### The scope of object identity

Example:

```
Session session1 = sessionFactory.openSession();
Transaction tx1 = session1.beginTransaction();
// Load Item with identifier value "1234"
Object a = session1.get(Item.class, new Long(1234) );
Object b = session1.get(Item.class, new Long(1234) );
( a==b ) // True, persistent a and b are identical
tx1.commit();
session1.close();
// References a and b are now to an object in detached state
Session session2 = sessionFactory.openSession();
Transaction tx2 = session2.beginTransaction();
Object c = session2.get(Item.class, new Long(1234) );
( a==c ) // False, detached a and persistent c are not identical
tx2.commit();
session2.close();
```

<a name="hibernate-interfaces"/>

### The Hibernate interfaces

The persistence manager may be exposed by several different interfaces. In the case of Hibernate, these are `Session`, `Query`, `Criteria`, and `Transaction`. Under the covers, the implementations of these interfaces are coupled tightly together.
In Java Persistence, the main interface you interact with is the `EntityManager`; it has the same role as the Hibernate `Session`. Other Java Persistence interfaces are `Query` and `EntityTransaction` (you can probably guess what their counterpart in native Hibernate is). 

<a name="objects-storing-loading"/>

#### Storing and loading objects

##### Beginning a unit of work

At the beginning of a unit of work, an application obtains an instance of `Session` from the application’s `SessionFactory`:
 
```
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();
```

##### Making an object persistent
```
Item item = new Item();
item.setName("Playstation3 incl. all accessories");
item.setEndDate( ... );
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();
Serializable itemId = session.save(item);
tx.commit();
session.close();
```

![alt tag](readmeImgs/makingPersistent.png)

##### Retrieving a persistent object

```
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();
Item item = (Item) session.load(Item.class, new Long(1234));
// Item item = (Item) session.get(Item.class, new Long(1234));
tx.commit();
session.close();
```

![alt tag](readmeImgs/retrievePersistent.png)

##### Modifying a persistent object

Any persistent object returned by `get()`, `load()`, or any entity queried is already associated with the current Session and persistence context. It can be modified, and its state is synchronized with the database

```
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();
Item item = (Item) session.get(Item.class, new Long(1234));
item.setDescription("This Playstation is as good as new!");
tx.commit();
session.close();
```

![alt tag](readmeImgs/modifyPersistent.png)

##### Making a persistent object transient

```
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();
Item item = (Item) session.load(Item.class, new Long(1234));
session.delete(item);
tx.commit();
session.close();
```

![alt tag](readmeImgs/makeTransient.png)

##### Replicating objects

```
Session session = sessionFactory1.openSession();
Transaction tx = session.beginTransaction();
Item item = (Item) session.get(Item.class, new Long(1234));
tx.commit();
session.close();
Session session2 = sessionFactory2.openSession();
Transaction tx2 = session2.beginTransaction();
session2.replicate(item, ReplicationMode.LATEST_VERSION);
tx2.commit();
session2.close();
```

The ReplicationMode controls the details of the replication procedure:
* ReplicationMode.IGNORE—Ignores the object when there is an existing database row with the same identifier in the target database.
* ReplicationMode.OVERWRITE—Overwrites any existing database row with the same identifier in the target database.
* ReplicationMode.EXCEPTION—Throws an exception if there is an existing database row with the same identifier in the target database.
* ReplicationMode.LATEST_VERSION—Overwrites the row in the target database if its version is earlier than the version of the object, or ignores the object otherwise. Requires enabled Hibernate optimistic concurrency control.

<a name="working-with-detached-objects"/>

#### Working with detached objects

Modifying the item after the `Session` is closed has no effect on its persistent representation in the database. As soon as the persistence context is closed, item becomes a detached instance. If you want to save modifications you made to a detached object, you have to either _reattach_ or _merge_ it.

##### Reattaching a modified detached instance

A detached instance may be reattached to a new `Session` (and managed by this new persistence context) by calling update() on the detached object. In our experience, it may be easier for you to understand the following code if you rename the `update()` method in your mind to `reattach()` — however, there is a good reason it’s called updating. The `update()` method forces an update to the persistent state of the object in the database, always scheduling an SQL `UPDATE`

It doesn’t matter if the item object is modified before or after it’s passed to `update()`. The important thing here is that the call to `update()` is reattaching the detached instance to the new Session (and persistence context).

![alt tag](readmeImgs/reattachDetached.png)

##### Reattaching an unmodified detached instance

A call to `lock()` associates the object with the Session and its persistence context without forcing an update.

```
Session sessionTwo = sessionFactory.openSession();
Transaction tx = sessionTwo.beginTransaction();
sessionTwo.lock(item, LockMode.NONE);
item.setDescription(...);
item.setEndDate(...);
tx.commit();
sessionTwo.close();
```

In this case, it _does_ matter whether changes are made before or after the object has been reattached. Changes made before the call to lock() aren’t propagated to the database, you use it only if you’re sure the detached instance hasn’t been modified. This method only guarantees that the object’s state changes from detached to persistent and that Hibernate will manage the persistent object again. Of course, any modifications you make to the object once it’s in managed persistent state require updating of the database.
By specifying `LockMode.NONE` here, you tell Hibernate not to perform a version check or obtain any database-level locks when reassociating the object with the Session. If you specified `LockMode.READ`, or `LockMode.UPGRADE`, Hibernate would execute a `SELECT` statement in order to perform a version check (and to lock the row(s) in the database for updating).

##### Merging the state of a detached object

```
item.getId(); // The database identity is "1234"
item.setDescription(...);
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();
Item item2 = (Item) session.get(Item.class, new Long(1234));
session.update(item); // Throws exception!
tx.commit();
session.close();
```

A persistent instance with the same database identifier is already associated with the Session!

You can let Hibernate merge item and item2 automatically:
 
```
item.getId() // The database identity is "1234"
item.setDescription(...);
Session session= sessionFactory.openSession();
Transaction tx = session.beginTransaction();
Item item2 = (Item) session.get(Item.class, new Long(1234));
Item item3 = (Item) session.merge(item);
(item == item2) // False
(item == item3) // False
(item2 == item3) // True
return item3;
tx.commit();
session.close();
```

<a name="transactions-and-concurrency"/>

## Transactions and concurrency

<a name="transactions-essentials"/>

### Transaction essentials

Interfaces:
* `java.sql.Connection` - Plain JDBC transaction demarcation with `setAutoCommit(false)`, `commit()`, and `rollback()`. It can but shouldn't be used in a Hibernate application, because it binds your application to a plain JDBC environment.
* `org.hibernate.Transaction` - Unified transaction demarcation in Hibernate applications. It works in a nonmanaged plain JDBC environment and also in an application server with JTA as the underlying system transaction service.
* `javax.transaction.UserTransaction` - Standardized interface for programmatic transaction control in Java; part of JTA. This should be your primary choice whenever you have a JTA-compatible transaction service and want to control transactions programmatically.
* `javax.persistence.EntityTransaction` - Standardized interface for programmatic transaction control in Java SE applications that use Java Persistence.

<a name="transactions-in-hibernate"/>

#### Transactions in a Hibernate application

##### Programmatic transactions in Java SE

```
Session session = null;
Transaction tx = null;
try {
 session = sessionFactory.openSession();
 tx = session.beginTransaction();
 concludeAuction(session);
 tx.commit();
} catch (RuntimeException ex) {
 tx.rollback();
} finally {
 session.close();
}
```

##### Transactions with Java Persistence

```
EntityManager em = null;
EntityTransaction tx = null;
try {
 em = emf.createEntityManager();
 tx = em.getTransaction();
 tx.begin();
 concludeAuction(em);
 tx.commit();
} catch (RuntimeException ex) {
 try {
 tx.rollback();
 } catch (RuntimeException rbEx) {
 log.error("Couldn't roll back transaction", rbEx);
 }
 throw ex;
} finally {
 em.close();
}
```

<a name="controlling-concurrent-access"/>

### Controlling concurrent access

<a name="isolation-issues"/>

#### Transaction isolation issues

A _lost update_ occurs if two transactions both update a row and then the second transaction aborts, causing both changes to be lost.

![alt tag](readmeImgs/lostUpdate.png)

A _dirty read_ occurs if a one transaction reads changes made by another transaction that has not yet been committed.

![alt tag](readmeImgs/dirtyRead.png)

An _unrepeatable read_ occurs if a transaction reads a row twice and reads different state each time. 

![alt tag](readmeImgs/unrepeatableRead.png)

A _phantom read_ is said to occur when a transaction executes a query twice, and the second result set includes rows that weren’t visible in the first result set or rows that have been deleted.
 
![alt tag](readmeImgs/phantomRead.png)

<a name="isolation-levels"/>

#### ANSI transaction isolation levels

* A system that permits dirty reads but not lost updates is said to operate in _read uncommitted_ isolation. One transaction may not write to a row if another uncommitted transaction has already written to it. Any transaction may read any row, however. This isolation level may be implemented in the database-management system with exclusive write locks.
* A system that permits _unrepeatable_ reads but not dirty reads is said to implement read committed transaction isolation. This may be achieved by using shared read locks and exclusive write locks. Reading transactions don’t block other transactions from accessing a row. However, an uncommitted writing transaction blocks all other transactions from accessing the row.
* A system operating in _repeatable read_ isolation mode permits neither unrepeatable reads nor dirty reads. Phantom reads may occur. Reading transactions block writing transactions (but not other reading transactions), and writing transactions block all other transactions.
* _Serializable_ provides the strictest transaction isolation. This isolation level emulates serial transaction execution, as if transactions were executed one after another, serially, rather than concurrently. Serializability may not be implemented using only row-level locks. There must instead be some other mechanism that prevents a newly inserted row from becoming visible to a transaction that has already executed a query that would return the row.

<a name="setting-isolation-level"/>

#### Setting an isolation level

```
hibernate.connection.isolation = 4
```
* 1 - Read uncommitted isolation
* 2 - Read committed isolation
* 4 - Repeatable read isolation
* 8 - Serializable isolation 

<a name="optimistic-concurrency-control"/>

#### Optimistic concurrency control

An optimistic approach always assumes that everything will be OK and that conflicting data modifications are rare. Optimistic concurrency control raises an error only at the end of a unit of work, when data is written.

To understand optimistic concurrency control, imagine that two transactions read a particular object from the database, and both modify it.

![alt tag](readmeImgs/optimistickLockingExample.png)

You have three choices for how to deal with lost updates in these second transactions in the conversations:
 
* _Last commit wins_ - Both transactions commit successfully, and the second commit overwrites the changes of the first. No error message is shown.
* _First commit wins_ - The transaction of conversation A is committed, and the user committing the transaction in conversation B gets an error message. The user must restart the conversation by retrieving fresh data and go through all steps of the conversation again with nonstale data.
* _Merge conflicting updates_ - The first modification is committed, and the transaction in conversation B aborts with an error message when it’s committed. The user of the failed conversation B may however apply changes selectively, instead of going through all the work in the conversation again.

If you don’t enable optimistic concurrency control, and by default it isn’t enabled, your application runs with a _last commit_ wins strategy.

##### Versioning with Java Persistence

```
@Entity
public class Item {
 ...
 @Version
 @Column(name = "OBJ_VERSION")
 private int version;
 ...
}
```

<a name="additional-isolation"/>

#### Obtaining additional isolation guarantees

##### Explicit pessimistic locking

Instead of switching all database transactions into a higher and nonscalable isolation level, you obtain stronger isolation guarantees when necessary with the `lock()` method on the Hibernate Session:

```
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();
Item i = (Item) session.get(Item.class, 123);
session.lock(i, LockMode.UPGRADE);
String description = (String)
 session.createQuery("select i.description from Item i" +
 " where i.id = :itemid")
 .setParameter("itemid", i.getId() )
 .uniqueResult();
tx.commit();
session.close();
```

**Hibernate lock modes**

* `LockMode.NONE` - Don’t go to the database unless the object isn’t in any cache.
* `LockMode.READ` - Bypass all caches, and perform a version check to verify that the object in memory is the same version that currently exists in the database.
* `LockMode.UPDGRADE` - Bypass all caches, do a version check (if applicable), and obtain a database-level pessimistic upgrade lock, if that is supported. Equivalent to LockModeType.READ in Java Persistence. This mode transparently falls back to LockMode.READ if the database SQL dialect doesn’t support a SELECT ... FOR UPDATE option.
* `LockMode.UPDGRADE_NOWAIT` - The same as UPGRADE, but use a SELECT ... FOR UPDATE NOWAIT, if supported. This disables waiting for concurrent lock releases, thus throwing a locking exception immediately if the lock can’t be obtained. This mode transparently falls back to LockMode.UPGRADE if the database SQL dialect doesn’t support the NOWAIT option.
* `LockMode.FORCE` - Force an increment of the objects version in the database, to indicate that it has been modified by the current transaction. Equivalent to LockModeType.WRITE in Java Persistence.
* `LockMode.WRITE` - Obtained automatically when Hibernate has written to a row in the current transaction. (This is an internal mode; you may not specify it in your application.) 

<a name="fetching-caching-optimizing"/>

## Optimizing fetching and caching

<a name="global-fetch-plan"/>

### Defining the global fetch plan

<a name="object-retrieval-options"/>

#### The object-retrieval options

Hibernate provides the following ways to get objects out of the database:

* Navigating the object graph, starting from an already loaded object, by accessing the associated objects through property accessor methods such as `aUser.getAddress().getCity()`, and so on.
* Retrieval by identifier, the most convenient method when the unique identifier value of an object is known.
* HQL
* Criteria
* Native SQL

Hibernate defaults to a lazy fetching strategy for all entities and collections. This means that Hibernate by default loads only the objects you’re querying for.
 
Proxies are placeholders that are generated at runtime. Whenever Hibernate returns an instance of an entity class, it checks whether it can return a proxy instead and avoid a database hit. A proxy is a placeholder that triggers the loading of the real object when it’s accessed for the first time:
```
Item item = (Item) session.load(Item.class, new Long(123));
item.getId();
item.getDescription(); // Initialize the proxy
```

If you call `get()` instead of `load()` you trigger a database hit and no proxy is returned. The `get()` operation always hits the database and returns `null` if the object can’t be found.
If you want to disable proxies:
```
@org.hibernate.annotations.Proxy(lazy = false)
public class User { ... }
```
Eager collection fetching:
```
@OneToMany(fetch = FetchType.EAGER)
private Set<Bid> bids = new HashSet<Bid>();
```
The `FetchType.EAGER` provides the same guarantees as `lazy="false"` in Hibernate: the associated entity instance must be fetched eagerly, not lazily.

<a name="select-fetch-strategy"/>

### Selecting a fetch strategy

<a name="batch-prefetching"/>

#### Prefetching data in batches

The first optimization is called _batch fetching_, and it works as follows: If one proxy of a `Category` must be initialized, go ahead and initialize several in the same `SELECT`.
```
@org.hibernate.annotations.BatchSize(size = 10)
public class User { ... }
```

<a name="subselects-prefetching"/>

#### Prefetching collections with subselects
Let’s take the last example and apply a (probably) better prefetch optimization:
```
List allItems = session.createQuery("from Item").list();
processBids( (Item)allItems.get(0) );
processBids( (Item)allItems.get(1) );
processBids( (Item)allItems.get(2) );
```

A much better optimization is _subselect fetching_ for this collection mapping:
```
@org.hibernate.annotations.Fetch(
 org.hibernate.annotations.FetchMode.SUBSELECT
)
private Set<Bid> bids = new HashSet<Bid>();}
```

<a name="caching-fundamentals"/>

### Caching fundamentals

<a name="caching-strategies-scopes"/>

#### Caching strategies and scopes

There are three main types of cache:

* _Transaction scope cache_ - Attached to the current unit of work, which may be a database transaction or even a conversation. It’s valid and used only as long as the unit of work runs. Every unit of work has its own cache. Data in this cache isn’t accessed concurrently.
* _Process scope cache_ - Shared between many (possibly concurrent) units of work or transactions. This means that data in the process scope cache is accessed by concurrently running threads, obviously with implications on transaction isolation.
* _Cluster scope cache_ - Shared between multiple processes on the same machine or between multiple machines in a cluster. Here, network communication is an important point worth consideration.
 
 <a name="cache-architecture"/>

#### The Hibernate cache architecture

* The first-level cache is the persistence context cache. A Hibernate Session lifespan corresponds to either a single request (usually implemented with one database transaction) or a conversation.
* The second-level cache in Hibernate is pluggable and may be scoped to the process or cluster. This is a cache of state (returned by value), not of actual persistent instances.

The four built-in concurrency strategies represent decreasing levels of strictness in terms of transaction isolation:
* Transactional - Available in a managed environment only, it guarantees full transactional isolation up to repeatable read, if required. Use this strategy for read-mostly data where it’s critical to prevent stale data in concurrent transactions, in the rare case of an update.
* Read-write - This strategy maintains read committed isolation, using a timestamping mechanism and is available only in nonclustered environments. Again, use this strategy for read-mostly data where it’s critical to prevent stale data in concurrent transactions, in the rare case of an update.
* Nonstrict-read-write - Makes no guarantee of consistency between the cache and the database. If there is a possibility of concurrent access to the same entity, you should configure a sufficiently short expiry timeout. Otherwise, you may read stale data from the cache. Use this strategy if data hardly ever changes (many hours, days, or even a week) and a small likelihood of stale data isn’t of critical concern.
* Read-only - A concurrency strategy suitable for data which never changes. Use it for reference data only.

![alt tag](readmeImgs/caches.png)

<a name="caching-in-practice"/>

### Caching in practice

<a name="concurrency-control-strategy"/>

#### Selecting a concurrency control strategy

```
@Entity
@Table(name = "CATEGORY")
@org.hibernate.annotations.Cache(usage =
 org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE
)
public class Category { ... }
```

<a name="controlling-second-level-cache"/>

#### Controlling the second-level cache
```
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();
session.setCacheMode(CacheMode.IGNORE);
```

The available options are as follows:
* `CacheMode.NORMAL`- The default behavior.
* `CacheMode.IGNORE`- Hibernate never interacts with the second-level cache except to invalidate cached items when updates occur.
* `CacheMode.GET`- Hibernate may read items from the second-level cache,but it won’t add items except to invalidate items when updates occur.
* `CacheMode.PUT`- Hibernate never reads items from the second-level cache,but it adds items to the cache as it reads them from the database.
* `CacheMode.REFRESH`- Hibernate never reads items from the second-levelcache, but it adds items to the cache as it reads them from the database. In this mode, the effect of hibernate.cache.use_minimal_puts is bypassed, in order to force a cache refresh in a replicated cluster cache.

<a name="querying-with-hql"/>

## Querying with HQL and JPA QL

<a name="creating-running-queries"/>

### Creating and running queries

**_Creating a query object_**

To create a new Hibernate Query instance, call either createQuery() or createSQLQuery() on a Session. The createQuery() method prepares an HQL query:
```
Query hqlQuery = session.createQuery("from User");
```
`createSQLQuery()` is used to create an SQL query using the native syntax of the underlying database:
```
Query sqlQuery = session.createSQLQuery(
 "select {user.*} from USERS {user}"
 ).addEntity("user", User.class);
```
To obtain a `Criteria` instance, call `createCriteria()`, passing the class of the objects you want the query to return. This is also called the root entity of the criteria query, the `Category` in this example:
```
Criteria crit = session.createCriteria(User.class);
```
To create a native SQL query, use `createNativeQuery()`:
```
Query sqlQuery = session.createNativeQuery(
 "select u.USER_ID, u.FIRSTNAME, u.LASTNAME from USERS u",
 User.class
 );
```

**_Paging the result_**
```
query.setMaxResults(10);
```

In `Criteria` query, the requested page starts in the middle of the resultset:
```
crit.setFirstResult(40);
```

**_Considering parameter binding_**
```
String queryString = "from Item item where item.description like :search";
Query q = session.createQuery(queryString).setString("search", searchString);
```

**_Using Hibernate parameter binding_**
`setString()`, `setDate()` and so on or `setParameter()`
```
String queryString = "from Item item"
 + " where item.seller = :seller and"
 + " item.description like :desc";
session.createQuery(queryString)
 .setParameter( "seller",
 theSeller,
 Hibernate.entity(User.class) )
 .setParameter( "desc", description, Hibernate.STRING );
```

**_Using positional parameters_**
```
String queryString = "from Item item"
 + " where item.description like ?"
 + " and item.date > ?";
Query q = session.createQuery(queryString)
 .setString(0, searchString)
 .setDate(1, minDate);
```

**_Executing a query_**
`list()`, `getResultList()` and `uniqueResult()`

**_Calling a named query_**
```
session.getNamedQuery("findItemsByDescription")
 .setString("desc", description);
```

**_Defining a named query with annotations_**
```
@NamedQueries({
 @NamedQuery(
 name = "findItemsByDescription",
 query = "select i from Item i where i.description like :desc"
 ),
 ...
})
@Entity
@Table(name = "ITEM")
public class Item { ... }
```

<a name="basic-hql-queries"/>

### Basic HQL and JPA QL queries

**_Selection_**
`from Item`
This query generates the following SQL:
```
select i.ITEM_ID, i.NAME, i.DESCRIPTION, ... from ITEM i
```
Using aliases
`from Item as item`
The as keyword is always optional. The following is equivalent:
`from Item item`

**_Restriction_**
`from Category u where u.email = 'foo@hibernate.org'`

**_Comparison expressions_**
```
from Bid bid where bid.amount between 1 and 10
from Bid bid where bid.amount > 100
from User u where u.email in ('foo@bar', 'bar@foo')
from User u where u.firstname like 'G%'
```

**_Expressions with collections_**
```
from Item i where i.bids is not empty
from Item i, Category c where i.id = '123' and i member of c.items
```

**_Ordering query results_**
```
from User u order by u.username
from User u order by u.username desc
```

**_Getting distinct results_**
select distinct item.description from Item item

<a name="joins-reporting-queries"/>

### Joins, reporting queries, and subselects
```
from Item i
 join i.bids b
 where i.description like '%Foo%'
 and b.amount > 100
```
converts to 
```
select i.DESCRIPTION, i.INITIAL_PRICE, ...
 b.BID_ID, b.AMOUNT, b.ITEM_ID, b.CREATED_ON
from ITEM i
inner join BID b on i.ITEM_ID = b.ITEM_ID
where i.DESCRIPTION like '%Foo%'
and b.AMOUNT > 100
```

Count:
```
Long count =
 (Long) session.createQuery("select count(i) from Item i")
 .uniqueResult();
```

Using subselects:
```
from User u where 10 < (
 select count(i) from u.items i where i.successfulBid is not null
)
```

<a name="criteria-querying"/>

## Querying with Criteria

<a name="basic-criteria-queries"/>

### Basic criteria queries

Creation:
```
session.createCriteria(BillingDetails.class);
```

Ordering:
```
session.createCriteria(User.class)
 .addOrder( Order.asc("lastname") )
 .addOrder( Order.asc("firstname") );
```

Applying restrictions:
```
Criterion emailEq = Restrictions.eq("email", "foo@hibernate.org");
Criteria crit = session.createCriteria(User.class);
crit.add(emailEq);
User user = (User) crit.uniqueResult();
```

Creating comparison expressions:
```
Criterion restriction =
 Restrictions.between("amount",
 new BigDecimal(100),
 new BigDecimal(200) );
session.createCriteria(Bid.class).add(restriction);
session.createCriteria(Bid.class)
 .add( Restrictions.gt("amount", new BigDecimal(100) ) );
String[] emails = { "foo@hibernate.org", "bar@hibernate.org" };
session.createCriteria(User.class)
 .add( Restrictions.in("email", emails) );
```

String matching:
```
session.createCriteria(User.class)
 .add( Restrictions.like("username", "G%") );
session.createCriteria(User.class)
 .add( Restrictions.like("username", "G", MatchMode.START) );
```

Combining expressions with logical operators:
```
session.createCriteria(User.class)
 .add(
 Restrictions.or(
 Restrictions.and(
 Restrictions.like("firstname", "G%"),
 Restrictions.like("lastname", "K%")
 ),
 Restrictions.in("email", emails)
 )
 );
```

Writing subqueries:
```
DetachedCriteria subquery =
 DetachedCriteria.forClass(Item.class, "i");
subquery.add( Restrictions.eqProperty("i.seller.id", "u.id"))
 .add( Restrictions.isNotNull("i.successfulBid") )
 .setProjection( Property.forName("i.id").count() );
Criteria criteria = session.createCriteria(User.class, "u")
 .add( Subqueries.lt(10, subquery) );
```

Simple projection lists:
```
session.createCriteria(Item.class)
 .add( Restrictions.gt("endDate", new Date()) )
 .setProjection( Projections.id() );
```

<a name="native-sql"/>

### Using native SQL queries

Automatic resultset handling:
```
List result = session.createSQLQuery("select * from CATEGORY")
 .addEntity(Category.class)
 .list();
```

Retrieving scalar values:
```
session.createSQLQuery("select u.FIRSTNAME as fname from USERS u")
 .addScalar("fname");
```