# Memory learning platform

| Main | Dev |
| ---- | --- |
|[![pipeline status](https://git.uibk.ac.at/informatik/qe/swapsws22/group6/g6t1/badges/main/pipeline.svg)](https://git.uibk.ac.at/informatik/qe/swapsws22/group6/g6t1/-/commits/main) | [![pipeline status](https://git.uibk.ac.at/informatik/qe/swapsws22/group6/g6t1/badges/dev/pipeline.svg)](https://git.uibk.ac.at/informatik/qe/swapsws22/group6/g6t1/commits/dev) |

# Description 

This is a learning platform using the [SM-2 Spaced Learning Algorithm](https://en.wikipedia.org/wiki/SuperMemo#Description_of_SM-2_algorithm).

It is a web application that allows users to create and share decks of cards. 
Each card has a question and an answer. 

The user can then use the platform to study the cards. 
The platform keeps track of when the cards where learnt and automatically calculates when the card should be learned the next time. 

The platform is built with Spring Boot as the Backend (in the ***swa***-Folder) and Svelte as the Frontend (in the ***static***-Folder).

Spring Boot creates a Web-, and REST-Server that serves the static Files generated from Svelte and handles API-Requests.

# Installation

Because this project is divided into a Backend and Frontend, both have to be installed seperately.

## Back-End

The Backend can be used using Gradle or Maven.

### Gradle

```
cd swa
gradle init
```

### Maven

```
cd swa
mvn install
```

### Development

For backend development purposes, some Git Hooks were setup.

These Hooks can be registered using the Shell Script in the Project Root:
```
./install_hooks.sh
```

## Front-End

The Frontend is a Svelte Project and thus depends on Node.js and npm (see [NPM Installation Page](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm/)).

Once Node.js and npm are set up, install the needed packages using npm:

```
cd static
npm install
```

# Usage

## Front-End

### Production

The Frontend is served using the Backend.

Because of this the Backend is compiled into ***/swa/main/resources/*** which is automatically served by the Backend when it is running.

The Frontend can be compiled using:

```
cd static
npm run build
```

This will compile the source files in the ***static***-Folder and automatically place them in ***/swa/main/resources/***.

### Development

The Frontend can be started without the Backend using the Vite Development Server:

```
cd static
npm run dev
```

This will run the Development Server (on [localhost:5173](localhost:5173)) which will automatically recompile any changes to the source Files and display them in the browser.

**Keep in mind:** The Backend will not run and thus any API-Calls will not be able to be completed.

## Back-End

### Gradle

```
cd swa
gradle bootRun
```

### Maven

```
cd swa
mvn spring-boot:run
```

## Continous development
You can open 3 terminals or 3 pains in a terminal with following commands, so the server reload automatically on any change in the backend and front end code. 

Als erstes
```
gradle assemble -t
````
Dann 
```
gradle bootRun
```

und 

```
npm run buildC
```

__don't forget these commands must be run simultaneously__

# Contributing

Only members of the group can contribute to this project.
