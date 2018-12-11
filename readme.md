# Otto-teller

A simple banknote dispenser like application written in Java with swing and a home made simple Model / Repository system that hide the database actions

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

In order to use this software you need to have the Java virtual machine installed on your machine. Moreover if the application is not compiled yet, your would need the Java SDK

### Installing

The project use Gradle as building tool. You can find a embeded version within this repository if you just need to compile it.

```
gradle build
```

Then import the sql schema inside your database (database.sql)

**note that you might need to update the database connection information inside the utils/DatabaseProvide.java class**

You can also import the project directly into Intellij or Eclipse using gradle as project source.

## Running the tests

There is no tests on this project for the moment

## Contributing

Please feel free to open a pull request for any improvement!

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
