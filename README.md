# ExampleQuery

Example Query is a library to easily query databases elements without spend time creating sql or hql queries. 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

Find by Examples

Example Query will use a combination of an example entity with a maps of filter values as main parameters that will be applied if it is necessary to the result query. 
	
Custom queries.

Example Query also provides manual field selection for query.

Unique Exception Processing 
Returning controlled Unique Exceptions at persistence of entities
	
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

## Running the tests

ExampleQuery includes a in-memory database to test all the ExampleQuery API. The basic examples includes a simple sample of database. That database includes a table system that represent a Note - Customer - CustomerOrder - ProductStock - Product. It is provided an test class for each entity that test the full service based on a Given<Entity> class instance. For example:

```
package foo.bar.test.service;

import foo.bar.domain.Customer;
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.test.common.TestCommon;
import foo.bar.test.given.GivenCustomer;

public class TestCustomerService extends TestCommon<CustomerServiceImpl, Customer, GivenCustomer> {

}

```


In the root application folder execute:

	mvn test

The test of entities and services will be perform by a class that extends TestCommon class. The test are automatized so to create a test instance create a class like that.

```
package foo.bar.test.service;

import foo.bar.domain.Product;
import foo.bar.service.impl.ProductServiceImpl;
import foo.bar.test.common.TestCommon;
import foo.bar.test.given.GivenProduct;

public class TestProductService extends TestCommon<ProductServiceImpl, Product, GivenProduct> {

}
```

## HqlConditions

HqlConditions is an enum that contains the allowed filtering types to use with ExampleQuery. They are basically the most common jpql conditions clause.

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

## Annotation: @Reference

The main idea of ExampleQuery is to usage the same class that represent the entity has holder for the different values that we want to apply to the custom filters applied. But what happens if we want to use a filter that can't be set directly in the entity, like a filter for a value inside a list. For that purpose ExampleQuery includes the field annotation @Reference

```
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


## Usage

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

## Built With

* [Git](https://git-scm.com/) Git for downloading source code
* [Hibernate](http://hibernate.org/orm/documentation/4.2/) - The hibernate framework version used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Java SDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java used version

## Authors

* **Jesús María López Pino**


## License

This project is licensed under the Apache License 2.0 - see the [LICENSE.md](LICENSE.md) file for details

