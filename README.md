
# Task

The Goal of this Project is to create a
**TODO** using a [Three-tier architecture](https://www.ibm.com/cloud/learn/three-tier-architecture).

# Project Structure

The Project is built as a [Three-tier architecture](https://www.ibm.com/cloud/learn/three-tier-architecture).

![Three-tier diagram](/../media/Schichtendiagram.drawio.svg)

## Persistence Tier

The Persistence Tier is automatically created using
[JDBC](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html)

## Application Tier

The Application Tier is written in **pure Java**.

## Presentation Tier

The Presentation Tier consists of a *[Spring Web](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html) Server* that serves Webpages created using *[Svelte](https://svelte.dev/)*.

The *Svelte-Webpages* request and display data from the Server using **RESTful Endpoints**.
