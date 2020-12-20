# posmulten-demos
Demo applications for the [posmulten](https://github.com/starnowski/posmulten) library.
It contains integration with Hibernate 4 and Spring Boot (version 1.X).
Demo project during tests and application startup creates schema so that [shared schema strategy]((https://docs.jboss.org/hibernate/orm/4.3/devguide/en-US/html/ch16.html)) could be used.

## Java packages 

* __com.github.starnowski.posmulten.demos.hibernate__ - Integration with Hibernate library
* __com.github.starnowski.posmulten.demos.web__ - Integration with Spring Security and Spring Web configuration
* __com.github.starnowski.posmulten.demos.configurations.PrimaryDataSourceConfiguration__ - Configuration for data source component that is used during schema creation via Hibernate
* __com.github.starnowski.posmulten.demos.configurations.OwnerDataSourceConfiguration__ - Configuration for data source component that primary data source for application