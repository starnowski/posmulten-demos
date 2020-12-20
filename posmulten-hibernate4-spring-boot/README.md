# posmulten-demos
Demo applications for the [posmulten](https://github.com/starnowski/posmulten) library.
It contains integration with Hibernate 4 and Spring Boot (version 1.X).
Demo project during tests and application startup creates schema so that [shared schema strategy]((https://docs.jboss.org/hibernate/orm/4.3/devguide/en-US/html/ch16.html)) could be used.
Application has only a REST API that allows adding three types of resources (Tenant, User, and User's post).
For security purposes, it uses simply basic authentication.
The important thing is that the application requires a __Postgres__ database in version at least 9.6.

## Prepare database

Project contains database script that creates database:
* __create-database-owner.sql__ - creates database user with name 'posmhib4sb-owner'
* __create-database-user.sql__ - creates database user with name 'posmhib4sb-user'
* __create-database.sql__ - creates database and assigns the posmhib4sb-owner user as database owner

## Project build

After database creation project has to be build.
Project uses maven for a build purpose.

```bash
mvn clean install
```

## Run application

To run application after build, please execute below line:

```bash
mvn spring-boot:run
```

## Send REST API requests

Project contains Postman collection with requests examples in file __posmulten-hibernate4-spring-boot.postman_collection.json__

## Java packages 

* __com.github.starnowski.posmulten.demos.hibernate__ - Integration with Hibernate library
* __com.github.starnowski.posmulten.demos.web__ - Integration with Spring Security and Spring Web configuration
* __com.github.starnowski.posmulten.demos.configurations.PrimaryDataSourceConfiguration__ - Configuration for data source component that is used during schema creation via Hibernate
* __com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration__ - Configuration for data source component that is primary data source for application

## Useful links

* http://websystique.com/spring-security/secure-spring-rest-api-using-basic-authentication/