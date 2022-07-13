# Demo project for integration with Spring Boot, Thymeleaf and Posmulten-hibernate

* [Introduction](#introduction)
* [How to prepare database](#how-to-prepare-database)
* [How to build project](#how-to-build-project)

## Introduction
Project is simple application with users that posts text content. 
The idea behind demo is that application use Multi-tenancy architecture with shared schema strategy.
Which means that all tenants shares not only the same database but also schemas.
In our example tenant is customer that has domain where his user posts text content.
Posmulten-hibernate library required of using Postgres as database engine.

## How to prepare database
Project user Postgres database version. Minimum version is 9.6.
To create databse users and schema execute below script from project in correct order:

- scripts/create-database-owner.sql
- scripts/create-database-user.sql
- scripts/create-database.sql

## How to build project
