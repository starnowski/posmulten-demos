# Demo project for integration with Spring Boot, Thymeleaf and Posmulten-hibernate

Project is simple application with users that posting text content. 
The idea behind demo is that application use Multi-tenancy architecture with shared schema strategy.
Which means that all tenants share not only the same database but also schemas.
In our example tenant is customer that has domain where his user posts text content.
Posmulten-hibernate library required of using Postgres as database engine.

## 