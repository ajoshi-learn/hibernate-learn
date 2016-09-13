# Hibernate cheat sheet

## 1. Architecture

### 1.1. Class diagram
![alt tag](http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/images/architecture/JPA_Hibernate.svg)

### 1.2. Classes description
`SessionFactory` `(org.hibernate.SessionFactory)`
A thread-safe (and immutable) representation of the mapping of the application domain model to a database. Acts as a factory for `org.hibernate.Session` instances. The `EntityManagerFactory` is the JPA equivalent of a `SessionFactory` and basically those two converge into the same `SessionFactory` implementation.

A `SessionFactory` is very expensive to create, so, for any given database, the application should have only one associated `SessionFactory`. The `SessionFactory` maintains services that Hibernate uses across all `Session`(s) such as second level caches, connection pools, transaction system integrations, etc.

`Session` `(org.hibernate.Session)`
A single-threaded, short-lived object conceptually modeling a "Unit of Work" PoEAA. In JPA nomenclature, the `Session` is represented by an `EntityManager`.

Behind the scenes, the Hibernate `Session` wraps a JDBC `java.sql.Connection` and acts as a factory for `org.hibernate.Transaction` instances. It maintains a generally "repeatable read" persistence context (first level cache) of the application domain model.

`Transaction` `(org.hibernate.Transaction)`
A single-threaded, short-lived object used by the application to demarcate individual physical transaction boundaries. `EntityTransaction` is the JPA equivalent and both act as an abstraction API to isolate the application from the underlying transaction system in use (JDBC or JTA).

## 2. Domain model

### 2.1. Mapping types

```
create table Contact (
    id integer not null,
    first varchar(255),
    last varchar(255),
    middle varchar(255),
    notes varchar(255),
    starred boolean not null,
    website varchar(255),
    primary key (id)
)
```

```
@Entity(name = "Contact")
 public static class Contact { 
     @Id
     private Integer id; 
     private Name name; 
     private String notes; 
     private URL website; 
     private boolean starred; 
 }
 @Embeddable
 public class Name { 
     private String first; 
     private String middle; 
     private String last; 
 }
 ```
 
Hibernate categorizes types into two groups:
Value types
Entity types

#### 2.1.1. Value types
A value type is a piece of data that does not define its own lifecycle. It is, in effect, owned by an entity, which defines its lifecycle.
Looked at another way, all the state of an entity is made up entirely of value types. These state fields or JavaBean properties are termed *persistent attributes*. The persistent attributes of the `Contact` class are value types.

#### 2.1.2 Entity types
Entities, by nature of their unique identifier, exist independently of other objects whereas values do not. Entities are domain model classes which correlate to rows in a database table, using a unique identifier. Because of the requirement for a unique identifier, entities exist independently and define their own lifecycle. The `Contact` class itself would be an example of an entity.

### 2.2. Naming strategies
#### 2.2.1. ImplicitNamingStrategy
When an entity does not explicitly name the database table that it maps to, Hibernate need to implicitly determine that table name. Or when a particular attribute does not explicitly name the database column that it maps to, Hibernate need to implicitly determine that column name.
Setting ImplicitNamingStrategy:
`hibernate.implicit_naming_strategy`:
1. pre-defined:
* `default` for `org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl` - an alias for `jpa`
* `jpa` for `org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl` - the JPA 2.0 compliant naming strategy
* `legacy-hbm` for `org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl` - compliant with the original Hibernate NamingStrategy
* `legacy-jpa` for `org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl` - compliant with the legacy NamingStrategy developed for JPA 1.0, which was unfortunately unclear in many respects regarding implicit naming rules.
* `component-path` for `org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl` - mostly follows `ImplicitNamingStrategyJpaCompliantImpl` rules, except that it uses the full composite paths, as opposed to just the ending property part
2. reference to Class that implements `org.hibernate.boot.model.naming.ImplicitNamingStrategy` contract

#### 2.2.2. PhysicalNamingStrategy

Many organizations define rules around the naming of database objects (tables, columns, foreign-keys, etc). The idea of a PhysicalNamingStrategy is to help implement such naming rules without having to hard-code them into the mapping via explicit names.
While the purpose of an ImplicitNamingStrategy is to determine that an attribute named `accountNumber` maps to a logical column name of `accountNumber` when not explicitly specified, the purpose of a PhysicalNamingStrategy would be, for example, to say that the physical column name should instead be abbreviated `acct_num`.
 
### 2.3. Basic Types
#### 2.3.1. Hibernate provided BasicTypes

| JDBC          | Java                  |
| ------------- |:---------------------:|
| VARCHAR       | String                |
| CHAR          | char, Character       |
| BIT           | boolean, Boolean      |
| TINYINT       | byte, Byte            |
| SMALLINT      | short, Short          |
| INTEGER       | int, Integer          |
| BIGINT        | long, Long            |
| FLOAT         | float, Float          |
| DOUBLE        | double, Double        |
| INTEGER       | BigInteger, BigDecimal|
| DATE          | Date, Calendar        |
| VARCHAR       | Class                 |
| INTEGER       | int, Integer    |

These mappings are managed by a service inside Hibernate called the 'BasicTypeRegistry', which essentially maintains a map of 'org.hibernate.type.BasicType' (a 'org.hibernate.type.Type' specialization) instances keyed by a name.