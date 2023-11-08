# reminder
Coursework: Reminder Bot
# README.md
![image](https://github.com/YuriPetukhov/reminder/assets/128038157/e0eb6331-74ab-4b22-9098-c7636805004c)

## Project "Telegram Reminder Bot"

This project is an implementation of a Telegram bot that accepts messages from the user in the format 
`01.01.2024 20:00 Do homework` and sends the user a reminder message at the specified time with the text 
"Do homework". The bot also allows you to get the weather forecast for a specified city at the current 
time or report the weather forecast for a specified city at a designated time by sending a message in the 
format `01.01.2024 20:00 City Name`.

## Tech Stack

- Backend
  - Java 11
  - Maven
  - Spring Boot
  - Spring Web
  - Spring JPA
  - Lombok
  - Stream API
  - PostgreSQL
  - Liquibase


## Running the Project

1. Clone the repository.
2. Install the dependencies specified in `pom.xml`.
3. Set up the connection settings for your PostgreSQL database in the `application.properties` file.
4. Run the project via the command line using the command `mvn spring-boot:run` or use your favorite IDE.
5. The project will launch the server on port 8080.

## Using the Bot

1. After starting the server, open Telegram.
2. Search for your bot by name.
3. Send a message in the following formats: 
a) to set a reminder: `01.01.2024 20:00 Your message`, 
b) to get the weather forecast: `01.01.2024 20:00 City` or 
c) to get the current weather forecast: `City`.


The bot will process your message and send you a reminder message with "Your message" at the specified time or 
provide the weather forecast for now or at the designated time.

## Development

The project uses Maven as a build system. The dependency artifacts are listed in the `pom.xml` file. 
Remember to update the dependencies before starting work on new features.

## Testing

The project includes basic tests. Run them to ensure that all the key functions are working correctly. 
If you're adding new functionality, try to also write tests for it. The data obtained is stored in PostgreSQL.


## Known Issues

There are no known issues at the moment.

