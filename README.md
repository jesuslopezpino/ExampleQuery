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


In the root application folder execute:

	mvn test


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

