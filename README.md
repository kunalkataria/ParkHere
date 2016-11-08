# ParkHere

Airbnb for parking - Android Mobile Application (in Progress)

## About

Class Project for USC Viterbi CSCI 310 - Software Engineering

## Running the tests

Unit tests using JUnit and Espresso (to be implemented).

## Deployment

Run on an android phone (certain image upload capabilities are not supported on all simulators).

## Built With

* [Firebase](https://firebase.google.com/)
* [Google App Engine](https://cloud.google.com/appengine/) - For server-side functions

External Libraries used: 

* [Butter Knife](http://jakewharton.github.io/butterknife/) - Field and method binding for Android views 
* [Retrofit](https://square.github.io/retrofit/) - For making server http requests and recieving response
* [Picasso](http://square.github.io/picasso/) - For easy image downloading in the application
* [GSON](https://github.com/google/gson) - Serializing java objects to and from JSON

## Authors
Johnson Hui, Kunal Kataria, Wyatt Kim, Eddie Lou, Justin Lu, Jonathan Wang

[Contributions Here](https://github.com/kunalkataria/ParkHere/graphs/contributors)

This project needs to be run on an actual phone, many features do not work on the emulator.

Tests are in the androidTest and Test folder, each test file has to be run individually, running the tests as a whole package does not work.