# ExampleQuery

ExampleQuery is a tool library where main utility is the ability of easily execute customized database queries without spend time creating or updating the queries, just using the defined entities class. You will build custom queries just identifying entity fields with the desired condition that you want to apply. Perform queries at tables by examples of entities with filters, so you will have the parameter value in the field that you chose for the filter. You won't have to worry about joins anymore! ExampleQuery will figure out it for you! You just have to worry about write the path to the field that you want to use, there will be cases that will be covered by `@FilterForField` annotation that will be very useful. Changes at database will not affect you so much in your code, besides ExampleQuery offers a TestCommon class to be able to set up a full battery of unit test for your services.

*Example Query will use a combination of an example entity with a maps of filter values as main parameters that will be applied if it is necessary to the result query.* 
	
*Example Query also provides manual field selection for query.*

*Unique Exception Processing returning controlled Unique Exceptions at persistence of entities*


* [Getting Started](#getting-started)
	* [Prerequisites](#prerequisites)
	* [Installing](#installing)
	* [Set up](#set-up)
		* [Setting up project](#setting-up-project)
		* [Setting up entities](#setting-up-entities)
		* [Setting up services](#setting-up-services)
* [ExampleQuery Filters](#examplequery-filters)
	* [HqlConditions](#hqlconditions)
* [ExampleQuery Examples](#examplequery-examples)
* [ExampleQuery Service](#examplequery-service)
	* [findByExample](#findbyexample)
		* [Annotation: @FilterForField](#annotation-filterforfield)
		* [@FilterForField: First usage](#filterforfield-first-usage)
		* [@FilterForField: Second usage](#filterforfield-second-usage)
	* [findCustomByPk](#findcustombypk)
	* [findCustomByExample](#findcustombyexample)
	* [countByExample](#countbyexample)
	* [findAll](#findall)
	* [countAll](#countall)
	* [save](#save)
	* [delete](#delete)
	* [update](#update)
		* [UniqueException](#uniqueexception)
* [Running the tests](#running-the-tests)
* [Given abstract class](#given-abstract-class)
* [Built With](#built-with)
* [Authors](#authors)
* [License](#license)

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
### Set up
#### Setting up project
To use ExampleQuery in your project you need to include the dependency at your pom.xml file:
```
<dependencies>
	...
	<dependency>
		<groupId>com.polvisoft.exampleQuery</groupId>
		<artifactId>ExampleQuery</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
	...
</dependencies>
```
#### Setting up entities
To be able to use ExampleQuery service your entity classes must extends abstract `BasicDTO<PK>`.

```java
@Entity
@Table(name = "PRODUCT")
public class Product extends BasicDTO<Long> {
...
}
```

Now we can define the rest of the entity as usual.

```java
	...
	// Those constants aren't requiered at all, but I found it very usefull
	// to define a constant that represent a field of the class because we
	// will use strings to refer to filter and select fields selection
	public static final String NAME = "name"; 

	public static final String DESCRIPTION = "description";
	
	
	@Id
	@GeneratedValue(generator = "SQ_PRODUCT")
	@SequenceGenerator(name = "SQ_PRODUCT", sequenceName = "SQ_PRODUCT")
	private Long pk;
	
	@NotBlank
	@Column
	private String name;

	@NotBlank
	@Column
	private String description;
	...
}
```

Developer still have to implements two abstract methods from `BasicDTO`

```java
public abstract PK getPk();

public abstract void setPk(PK pk);
```

This is like that because we want that `@Id` annotation will be set in pk field that user must define in order to use sequence generator annotation instead of creating a field at abstract class.


#### Setting up services
To create a ExampleQuery service instance you just need to create a class that extends the abstract class `ServiceImpl<DTO extends BasicDTO>` with an entity class that extends `BasicDTO` as type parameter.

```java
package com.polvisoft.exampleQuery.service.impl;

import com.polvisoft.exampleQuery.domain.Product;

public class ProductServiceImpl extends ServiceImpl<Product> {

}
```
That's all you need to set up and service of an entity.

## ExampleQuery Filters
Filters in ExampleQuery are very simple, it is the composition of a *field name* and a *condition*. In this case, a filter is represented by a `Map<String, HqlCondition>` where the key will be the field value (with dot annotation) and the condition that will be applied to the field. In that case, allowed conditions are represented by a java enum `HqlConditions`. Each filter entry that has to be applied will be added with an `AND` to the where clause.

```java
FilterMap filter = new FilterMap();
filter.put(Product.NAME, HqlConditions.EQUALS);
filter.put(Product.DESCRIPTION, HqlConditions.LIKE_IGNORE_CASE);
```

Conditions are applied in two ways, all* or automatic. In automatic mode the conditions will be applied only if a value is present (there are conditions that are always applied). In all mode all the conditions will be applied.

(* - Not implemented)

### HqlConditions

`HqlConditions` is an enum that contains the allowed filtering types to use with ExampleQuery. They are basically the most common jpql conditions clause.

```java
package com.polvisoft.exampleQuery.service.utils;

public enum HqlConditions {

	// Does NOT requires value in example to be applied
	IS_NULL(" is null "), 		// the field is null
	IS_NOT_NULL(" is not null "), 	// the field is not null
	IS_EMPTY(" is empty "), 	// the field is empty
	IS_NOT_EMPTY(" is not empty "), // the field is not empty
	
	// Does require value in the example object to be applied
	LIKE(" like "), 		// the field is like (case-sensitive) the value of field in the example
	LIKE_IGNORE_CASE(" LIKE "), 	// the field is like (case-insensitive) the value of field in the example
	EQUALS(" = "), 			// the field is equals to the value of field in the examplethe value of field in the example
	NOT_EQUALS(" != "), 		// the field is not equals to the value of field in the example
	GREATER_THAN(" > "), 		// the field is greater than the value of field in the example
	GREATER_EQUALS(" >= "), 	// the field is greater or equals to the value of field in the example
	LOWER_THAN(" < "), 		// the field is lower than the value of field in the example
	LOWER_EQUALS(" <= "),		// the field is greater or equals to the value of field in the example
	IN(" in "), 			// the field is in the list value of the example
	NOT_IN(" not in "); 		// the field is not in the list value of the example
	...
	
}
```

## ExampleQuery Examples
ExampleQuery bases its queries on the examples, that means that the query for the entity example given. For example, if we want to filter Customer table, our example will be an instance of Customer.
```java
Customer example = new Customer();
example.setName("Jesús");
example.setLastName("López");
```
This is an example that we can apply as filter values container for our queries to CUSTOMER table. We will see more uses of examples in the service method explanations. 
## ExampleQuery Service

ExampleQuery provides an abstract interface that also implements that offers most usual usage to deal with a data repository.

```java
public int countAll() throws ExampleQueryException;

public List<DTO> findAll() throws ExampleQueryException;

public DTO findByPk(Object primaryKey);

public DTO findCustomByPk(Object primaryKey, String[] fields) throws ExampleQueryException;

public int countByExample(DTO example, FilterMap filter) throws ExampleQueryException;

public List<DTO> findByExample(DTO example, FilterMap filter) throws ExampleQueryException;

public List<DTO> findByExample(DTO example, FilterMap filter, int pageNumber, int pageSize) throws ExampleQueryException;

public List<DTO> findCustomByExample(DTO example, String[] fields, FilterMap filter) throws ExampleQueryException;

public List<DTO> findCustomByExample(DTO example, String[] fields, FilterMap filter, int pageNumber, int pageSize) throws ExampleQueryException;

boolean delete(DTO element);

public DTO save(DTO element) throws UniqueException;

public DTO update(DTO element) throws UniqueException;

public List<DTO> saveList(List<DTO> list) throws UniqueException;

public List<DTO> updateList(List<DTO> list) throws UniqueException;

public boolean deleteList(List<DTO> list);
```

### findByExample

ExampleQuery offers to developer an easy way to perform custom filtered queries, to do this `Service<BasicDTO<PK>>` provides five methods to perform these queries:

```java
public int countByExample(DTO example, FilterMap filter) throws ExampleQueryException;

public List<DTO> findByExample(DTO example, FilterMap filter) throws ExampleQueryException;

public List<DTO> findByExample(DTO example, FilterMap filter, int pageNumber, int pageSize) throws ExampleQueryException;

public List<DTO> findCustomByExample(DTO example, String[] fields, FilterMap filter) throws ExampleQueryException;

public List<DTO> findCustomByExample(DTO example, String[] fields, FilterMap filter, int pageNumber, int pageSize) throws ExampleQueryException;
```

For first example we want to filter products by 2 conditions, name has to be equals and description like ignore case.

```java
@Autowired
ProductService service;
...
FilterMap filter = new FilterMap();
filter.put(Product.NAME, HqlConditions.EQUALS);
filter.put(Product.DESCRIPTION, HqlConditions.LIKE_IGNORE_CASE);
		
Product example = new Product();
example.setName("Pizza");
example.setDescription("food");

List<DTO> result = service.findByExample(example, filter); 
```

Execution of that example will result in that hql query:

```
select 
	product 
from 
	Product product 
where 
	(product.name = :name) and 
	(UPPER(product.description) LIKE :description)
```

Setting up parameter `:name` with value `"Pizza"` and parameter `:description`	with value `"%FOOD%"`

#### Annotation: @FilterForField

The main idea of ExampleQuery is to usage the same class that represent the entity has holder for the different values that we want to apply to the custom filters applied. But what happens if we want to use a filter that can't be set directly in the entity, like when we want to filter a field by a range or filter for a value inside of a list. For that purpose ExampleQuery includes the field annotation `@FilterForField`

`@FilterForField` annotation has one value to define that will contain the path to the field that we want to filter, the fields will be separated by `"."`.

#### @FilterForField: First usage

In that case we are going to filter a field by range using two `@Transient` fields.

We have CustomerOrder class:

```java
@Entity
@Table(name = "CUSTOMER")
public class Customer extends BasicDTO<Long> {

	...
	public static final String BIRTH_DATE = "birthDate";

	public static final String BIRTH_DATE_START = "birthDateStart";

	public static final String BIRTH_DATE_END = "birthDateEnd";
	...
	
	@NotNull
	@Column(name = BIRTH_DATE)
	private Date birthDate;

	@Transient
	@FilterForField(BIRTH_DATE)
	private Date birthDateStart;

	@Transient
	@FilterForField(BIRTH_DATE)
	private Date birthDateEnd;
	...		
}
```
We will set up the values for range at the transient fields.
```java
@Autowired
CustomerService service;
...
FilterMap filter = new FilterMap();
filter.put(Customer.BIRTH_DATE_START, HqlConditions.GREATER_EQUALS);
filter.put(Customer.BIRTH_DATE_END, HqlConditions.LOWER_THAN);

Customer example = new Customer();
example.setBirthDateStart(Utils.getDateTime("01/01/1983 00:00:00"));
example.setBirthDateEnd(Utils.getDateTime("12/12/1983 23:59:59"));	

List<DTO> result = service.findByExample(example, filter); 	
```

Execution of that example will result in that hql query:

```
select 
	customer 
from 
	Customer customer 
where 
	(customer.birthDate >= :birthDateStart) and
	(customer.birthDate < :birthDateEnd)
```
Setting up parameter `:birthDateStart` with value `"Sun Dec 26 00:00:00 CET 1982"` and `:birthDateEnd` with value `"Sun Dec 26 23:59:00 CET 1982"`

#### @FilterForField: Second usage

In that case we are going to do an more elaborated query, to retrieve customers that has order "pizza".

We have Customer class:

```java
@Entity
@Table(name = "CUSTOMER")
public class Customer extends BasicDTO<Long> {

	...
	@OneToMany(mappedBy = CustomerOrder.CUSTOMER, targetEntity = CustomerOrder.class)
	private List<CustomerOrder> customerOrders;
	
	...
	@Transient
	@FilterForField(Customer.CUSTOMER_ORDERS + "." + CustomerOrder.PRODUCTS_STOCK + "." + ProductStock.PRODUCT + "." + Product.NAME)
	private String customerOrdersProductName;
	...
	
}
```
We will set up the value of product name at the transient field.
```java
@Autowired
CustomerService service;
...
FilterMap filter = new FilterMap();
filter.put(Customer.ORDERS_PRODUCTS_NAME, HqlConditions.LIKE_IGNORE_CASE);

Customer example = new Customer();
example.setCustomerOrdersProductName("pizza");

List<DTO> result = service.findByExample(example, filter); 
```
Execution of that example will result in that hql query:
```
select 
	customer 
from 
	Customer customer 
	join customer.customerOrders customerOrders  
	join customerOrders.productsStock productsStock  
	join productsStock.product product  
where 
	(UPPER(product.name) LIKE :customerOrdersProductName)
```
Setting up parameter `:customerOrdersProductName` with value `"%PIZZA%"`.
It's not necessary to use a transient field with `@FilterForField` annotation if we don't have to deal with lists in the path. Let's see another example, from CustomerOrder side:
```java
@Entity
@Table(name = "CUSTOMER_ORDER")
public class CustomerOrder extends BasicDTO<Long> {
	...
	public static final String CUSTOMER = "customer";
	...
	@NotNull
	@ManyToOne
	@JoinColumn(name = CUSTOMER, referencedColumnName = CustomerOrder.PK)
	private Customer customer;
	...
}
```
We can built this example:
```java
@Autowired
CustomerOrderService service;
...
FilterMap filter = new FilterMap();
filter.put(CustomerOrder.CUSTOMER + "." + Customer.NAME, HqlConditions.NOT_EQUALS);

CustomerOrder example = new CustomerOrder();
Customer customer = new Customer();
customer.setName("Jesús");
example.setCustomer(customer);

List<DTO> result = service.findByExample(example, filter); 
```
That will result in the next query:
```
select 
	customerOrder 
from 
	CustomerOrder customerOrder 
	join customerOrder.customer customer  
where 
	(customer.name != :customer_name)

```
Setting up parameter `:customer_name` with value `"Jesús"`.

### findCustomByPk
ExampleQuery offers to developer an easy way to perform custom field selection for our query, to do this `Service<BasicDTO<PK>>` provides two methods to perform these queries:
```
public DTO findCustomByPk(Object primaryKey, String[] fields) throws ExampleQueryException;

public List<DTO> findCustomByExample(DTO example, String[] fields, FilterMap filter) throws ExampleQueryException;
```
Just filling an String[] variable we will customize the fields that we want to retrieve. Those fields will be represented with an string that will contain the path to the field that we want to include with `"."` working as field path separator. We can see an example:
```java
CustomerOrderService service;

String field1 = CustomerOrder.PK;
String field2 = CustomerOrder.DATE;
String field3 = CustomerOrder.CUSTOMER + "." + Customer.PK;
String field4 = CustomerOrder.CUSTOMER + "." + Customer.NAME;
String field5 = CustomerOrder.CUSTOMER + "." + Customer.LAST_NAME;
String fields[] = { field1, field2, field3, field4, field5 };


List<CustomerOrder> result = service.findCustomByPk(1L, fields);
```

That will result in the next query:
```
Hibernate: 
	select 
		new map ( 
			customerOrder.pk as pk, 
			customerOrder.date as date, 
			customer.pk as customer_pk, 
			customer.name as customer_name, 
			customer.lastName as customer_lastName
		) 
	from 
		CustomerOrder customerOrder 
		join customerOrder.customer customer  
	where 
		pk = :pk
```
### findCustomByExample
ExampleQuery Service offers and method to find all elements in a table by a given example.
```java
@Autowired
CustomerService service;
...
FilterMap filter = new FilterMap();
filter.put(Customer.NAME, HqlConditions.LIKE);
filter.put(Customer.LAST_NAME, HqlConditions.EQUALS);
filter.put(Customer.DOCUMENT, HqlConditions.EQUALS);
filter.put(Customer.BIRTH_DATE, HqlConditions.LIKE_IGNORE_CASE);
filter.put(Customer.DOCUMENT, HqlConditions.EQUALS);
filter.put(Customer.NOTES, HqlConditions.IS_EMPTY);

Customer example = new Customer();
example.setName("Jesus");
example.setLastName("Lopez");
example.setDocument("XXXXXXX");

int result = service.countByExample(example, filter);
```
That will result in the next query:
```
Hibernate: 
    select 
    	count(*)
    from 
    	Customer customer 
    where  
    	(customer.name like :name) and 
    	(customer.lastName = :lastName) and 
    	(customer.document = :document) and 
    	(customer.birthDate is not null) and 
    	(customer.notes is empty)
```

### countByExample
ExampleQuery Service offers and method to count all element in a table by a given example.
```java
CustomerService service;

Customer example = new Customer();
example.setName("Jesus");
example.setLastName("Lopez");
example.setDocument("XXXXXXX");

int result = service.countByExample(example);
```
That will result in the next query:
```
Hibernate: 
    select 
    	count(*)
    from 
    	Customer customer 
    where  
    	(customer.name like :name) and 
    	(customer.lastName = :lastName) and 
    	(customer.document = :document) and 
    	(customer.birthDate is not null) and 
    	(customer.notes is empty)
```
### findAll
ExampleQuery Service offers and method to find all element in a table.
```java
CustomerService service;
...
List<Customer> result = service.findAll(); 
```
That will result in the next query:
```
select 
	customer 
from 
	com.polvisoft.exampleQuery.domain.Customer customer
```
### countAll
ExampleQuery Service offers and method to count all element in a table.
```java
CustomerService service;
...
int result = service.countAll(); 
```
That will result in the next query:
```
select 
	count(*) 
from 
	Product
```
### delete
ExampleQuery Service offers and method to delete a row of table.
```java
@Autowired
CustomerService service;
Customer customer; // a customer instance
...
boolean result = service.delete(customer); 
```
That will result in the next query:
```
Hibernate: 
    delete 
    from
        CUSTOMER 
    where
        pk=?
```
### update
ExampleQuery Service offers and method to update a row of table.
```java
@Autowired
CustomerService service;
Customer customer;
...
try {
	boolean result = service.update(customer); 
catch(UniqueException e){
	// handle UniqueException
}
```
That will result in the next query:
```
Hibernate: 
    update
        CUSTOMER 
    set
        birthDate=?,
        document=?,
        documentType=?,
        lastName=?,
        name=? 
    where
        pk=?
```
### save
ExampleQuery Service offers and method to save an entity.
```java
@Autowired
CustomerService service;
Customer customer;
...
try {
	boolean result = service.save(customer); 
catch(UniqueException e){
	// handle UniqueException
}
```
That will result in the next query:
```
Hibernate: 
	insert 
    into
        CUSTOMER
        (pk, birthDate, document, documentType, lastName, name) 
    values
        (default, ?, ?, ?, ?, ?)
```
#### UniqueException
ExampleQuery returns `UniqueException` when a unique constraint is violated, this is because the rely on that constraint should be part of database. To consider that behavior part as save action exception, it will be easier for developer to identify that exception and the involved fields and values. 
A unique exception contains: the entity instance that violated the constraint, the class of the entity, the `@UniqueException` annotation instance and a detailed message. `ServiceImpl` needs that the uk constraint name will be defined inside the annotation `@Table` unique constraints array.
```java
@Entity
@Table(name = "CUSTOMER", uniqueConstraints = {
		@UniqueConstraint(name = "DOCUMENT_UK", columnNames = { Customer.DOCUMENT }) })
public class Customer extends BasicDTO<Long> {
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
To create an test class for an Entity and Service you will have to provide a Given Entity Class instance. For example, to test Customer entity and CustomerServiceImpl we only need to provide the GivenCustomer class:

```java
package com.polvisoft.exampleQuery.test.service;

import com.polvisoft.exampleQuery.domain.Customer;
import com.polvisoft.exampleQuery.service.impl.CustomerServiceImpl;
import com.polvisoft.exampleQuery.test.common.TestCommon;
import com.polvisoft.exampleQuery.test.given.GivenCustomer;

public class TestCustomerService extends TestCommon<CustomerServiceImpl, Customer, GivenCustomer> {

}

```

## Given abstract class
`Given` is an abstract class that developer must extends to be able to test how the service deals with the entity in real time. Developer has to fill the abstract methods with code that will represent the use of services in a real environment in order to prove that all works fine.
```java
public abstract void givenExamplesEnvironment() throws UniqueException, ExampleQueryException;

public abstract String[] initCustomFields();

public abstract DTO[] initExamples() throws UniqueException, ExampleQueryException;

public abstract DTO initTestSaveInstance() throws UniqueException, ExampleQueryException;

public abstract FilterMap initFilter();

public abstract Map<String, Object> initTestUpdateValues() throws ExampleQueryException;

public abstract int initPageNumber();

public abstract int initPageSize();
```
When the developer provides content to that methods the unit test can be run. The test should be the most similar to what would happen in the real application usage.

## Built With
* [Git](https://git-scm.com/) Git for downloading source code
* [Hibernate](http://hibernate.org/orm/documentation/4.2/) - The hibernate framework version used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Java SDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java used version

## Authors
* *[Jesús María López Pino](https://www.linkedin.com/in/jesus-lopez-pino/)* 2017

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE.md](LICENSE.md) file for details

