# ExampleQuery

Example Query is a library to easily query databases elements without spend time creating sql or hql queries. You can built custom queries

Find by Examples

Example Query will use a combination of an example entity with a maps of filter values as main parameters that will be applied if it is necessary to the result query. 
	
Custom queries.

Example Query also provides manual field selection for query.

Unique Exception Processing 
Returning controlled Unique Exceptions at persistence of entities

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.
	
### Prerequisites

To install ExampleQuery you will need:

* Git
* Java 1.8
* Maven 3.x

### Installing

To install ExampleQuery from source code you need to clone repository and install it in local maven repository.

```
mkdir exampleQuery
cd exampleQuery
git clone https://github.com/jesuslopezpino/ExampleQuery.git
mvn install
```

## Usage

### Setting up project

To use ExampleQuery in your project you need to include the dependency at your pom.xml file:

```
<dependencies>
	...
	<dependency>
		<groupId>foo.bar</groupId>
		<artifactId>ExampleQuery</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
	...
</dependencies>
```

### Setting up entities

To be able to use ExampleQuery service your entity classes must extends abstract `BasicVO<PK>.`

```java
@Entity
@Table(name = "PRODUCT")
public class Product extends BasicVO<Long> {

	public static final String NAME = "name"; // Those constants aren't requiered at all, but I found it very usefull 
											  // to define a constant that represent a field of the class because we
											  // will use strings to refer to filter and select fields selection

	public static final String DESCRIPTION = "description";
	
	public Product() {
		super();
	}

	public Product(HashMap<String, Object> mapValues) {
		super(mapValues);
	}
	...
	
	@Id
	@GeneratedValue(generator = "SQ_PRODUCT")
	@SequenceGenerator(name = "SQ_PRODUCT", sequenceName = "SQ_PRODUCT")
	private Long pk;
	
	@NotBlank
	@Column(name = NAME)
	private String name;

	@NotBlank
	@Column(name = DESCRIPTION)
	private String description;
	...
}
```

Developer still have to implements two abstract methods from `BasicVO`

```java
public abstract PK getPk();

public abstract void setPk(PK pk);
```

This is like that because we want that `@Id` annotation will be set in pk field that user must define in order to use sequence generator annotation instead of creating a field at abstract class.


### Setting up services

To create a ExampleQuery service instance you just need to create a class that extends the abstract class `ServiceImpl<VO extends BasicVO>` with an entity class that extends `BasicVO` as type parameter.

```
package foo.bar.service.impl;

import foo.bar.domain.Product;

public class ProductServiceImpl extends ServiceImpl<Product> {

}
```

That's all you need to set up and service of an entity.


### Filters

Filters in ExampleQuery are very simple, it is the composition of a field name and a condition. In this case, a filter is represented by a `Map<String, HqlCondition>` where the key will be the field value (with dot annotation) and the condition that will be applied to the field. In that case, allowed conditions are represented by a java enum `HqlConditions`.


#### HqlConditions

`HqlConditions` is an enum that contains the allowed filtering types to use with ExampleQuery. They are basically the most common jpql conditions clause.

Does NOT requires value in example to be applied

* IS_NULL: the field is null
* IS_NOT_NULL: the field is not null
* IS_EMPTY: the field is empty
* IS_NOT_EMPTY: the field is not empty

Does require value in the example object to be applied

* LIKE: the field is like (case-sensitive) the value of field in the example
* LIKE_IGNORE_CASE: the field is like (case-insensitive) the value of field in the example
* EQUALS: the field is equals to the value of field in the examplethe value of field in the example
* NOT_EQUALS: the field is not equals to the value of field in the example
* GREATER_THAN: the field is greater than the value of field in the example
* GREATER_EQUALS: the field is greater or equals to the value of field in the example
* LOWER_THAN: the field is lower than the value of field in the example
* LOWER_EQUALS: the field is greater or equals to the value of field in the example
* IN: the field is in the list value of the example
* NOT_IN: the field is not in the list value of the example

## First usage: findByExample

```
ProductService service;

Map<String, HqlConditions> filter = new HashMap<>();
filter.put(Product.NAME, HqlConditions.EQUALS);
filter.put(Product.DESCRIPTION, HqlConditions.LIKE_IGNORE_CASE);
		
Product example = new Product();
example.setName("Pizza");
example.setDescription("food");

List<VO> result = service.findByExample(example, filter); 
```

Execution of that example will result in that hql query:

```
select 
	product 
from 
	foo.bar.domain.Product product 
where 
	1=1  and 
	(product.name = :name) and 
	(UPPER(product.description) LIKE UPPER(:description))
```

Setting up parameter `:name` with value `"Pizza"` and parameter `:description`	with value `"%FOOD%"`


## Annotation: @Reference

The main idea of ExampleQuery is to usage the same class that represent the entity has holder for the different values that we want to apply to the custom filters applied. But what happens if we want to use a filter that can't be set directly in the entity, like a filter for a value inside a list. For that purpose ExampleQuery includes the field annotation `@Reference`

```java
@Entity
@Table(name = "CUSTOMER")
public class Customer extends BasicVO<Long> {

	...
	@OneToMany(mappedBy = CustomerOrder.CUSTOMER, targetEntity = CustomerOrder.class)
	private List<CustomerOrder> customerOrders;
	
	...
	@Transient
	@Reference(fieldName = ORDERS_PRODUCTS_NAME, referenceFor = Customer.CUSTOMER_ORDERS + "." + CustomerOrder.PRODUCTS_STOCK + "." + ProductStock.PRODUCT + "." + Product.NAME)
	private String ordersProductsName;
	...
	
}
```

## Second Usage (With reference)

TODO

## Custom fields

TODO

## UniqueException

ExampleQuery returns `UniqueException` when a unique constraint is violated, this is because the rely on that constraint should be part of database. To consider that behavior part as save action exception, it will be easier for developer to identify that exception and the involved fields and values. 

A unique exception contains: the entity instance that violated the constraint, the class of the entity, the `@UniqueException` annotation instance and a detailed message. `ServiceImpl` needs that the uk constraint name will be defined inside the annotation `@Table` unique constraints array.

```java
@Entity
@Table(name = "CUSTOMER", uniqueConstraints = {
		@UniqueConstraint(name = "DOCUMENT_UK", columnNames = { Customer.DOCUMENT }) })
public class Customer extends BasicVO<Long> {
	...
	public static final String DOCUMENT = "document";
	...
	@NotBlank
	@Column(name = DOCUMENT)
	private String document;
	...
}
```


## Running the tests

ExampleQuery includes a in-memory database to test all the ExampleQuery API. The basic examples includes a simple sample of database. That database includes a table system that represent a Note - Customer - CustomerOrder - ProductStock - Product. It is provided a test class for each entity that test the full service based on a Given class instance. 

To run the test, in the root application folder execute:

```
mvn test
```

To create an test class for an Entity and Service you will have to provide a Given Entity Class instance

For example, to test Customer entity and CustomerServiceImpl we only need to provide the GivenCustomer class:

```java
package foo.bar.test.service;

import foo.bar.domain.Customer;
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.test.common.TestCommon;
import foo.bar.test.given.GivenCustomer;

public class TestCustomerService extends TestCommon<CustomerServiceImpl, Customer, GivenCustomer> {

}

```

## Given abstract class

`Given` is an abstract class that developer must extends to be able to test how the service deals with the entity in real time. Developer has to fill the abstract methods with code that will represent the use of services in a real environment in order to prove that all works fine.

```java
public abstract void givenExamplesEnvironment() throws UniqueException, InstantiationException, IllegalAccessException;

public abstract String[] initCustomFields();

public abstract VO[] initExamples() throws UniqueException, InstantiationException, IllegalAccessException;

public abstract VO initTestSaveInstance() throws UniqueException, InstantiationException, IllegalAccessException;

public abstract Map<String, HqlConditions> initFilter();

public abstract Map<String, Object> initEntityFields();

public abstract Map<String, Object> initTestUpdateValues();
```

When the developer provides content to that methods the unit test can be run. The test should be the most similar to what would happen in the real application usage.


## Built With

* [Git](https://git-scm.com/) Git for downloading source code
* [Hibernate](http://hibernate.org/orm/documentation/4.2/) - The hibernate framework version used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Java SDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java used version

## Authors

* **Jesús María López Pino** 2017


## License

This project is licensed under the Apache License 2.0 - see the [LICENSE.md](LICENSE.md) file for details

