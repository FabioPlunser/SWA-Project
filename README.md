# Memory learning platform  
Dev [![pipeline status](https://git.uibk.ac.at/informatik/qe/swapsws22/group6/g6t1/badges/dev/pipeline.svg)](https://git.uibk.ac.at/informatik/qe/swapsws22/group6/g6t1/commits/dev) 
Main [![pipeline status](https://git.uibk.ac.at/informatik/qe/swapsws22/group6/g6t1/badges/main/pipeline.svg)](https://git.uibk.ac.at/informatik/qe/swapsws22/group6/g6t1/-/commits/main) 
# Description
This is a platform for learning with specific memory techniques. It is a web application that allows users to create and share decks of cards. Each card has a question and an answer. The user can then use the platform to study the cards. The platform has a built-in scheduler that will show the user cards at the right time. The platform is built with Spring Boot and Svelte.

# Usage
To run the project, use the following command:
```bash
mvn spring-boot:run
``` 
# Development
## Frontend
The frontend is built with Svelte. Please note that this requiers a working node.js installation.

To start the frontend, use the following command:
```bash
cd static 
npm i 
npm run dev 
```
With npm run dev, the frontend will be served on https://localhost:5173. 

Their you can see the live changes you make to the frontend.

Please note that the different pages of the frontend are only reachable 
with example localhost:5173/login<span style="color: red">/</span>.<br/>
The second slash is important, otherwise the page will not be found.
Once the whole application runs and the backend serves the front end, this is not necessary anymore.

## Backend
The backend is built with Spring Boot.
It's main purpose is to serve the frontend and to provide an API for the frontend to communicate with the database.

It's easiest to use IntelliJ for development. <br/>
It will download all the dependencies automatically.


# Contributing
Only members of the group can contribute to this project.
