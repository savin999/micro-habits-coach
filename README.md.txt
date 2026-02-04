
# Micro-Habits Coach

Micro-Habits Coach is a desktop application developed using **Java** and **JavaFX**.  
The application helps users create, track, and manage daily micro-habits with a simple graphical user interface.

This project was developed as part of the **CSP3341 – Programming Paradigms** unit to demonstrate object-oriented programming concepts, persistence, event handling, and GUI development in Java.

---

## Features
- Create, edit, and delete habits
- Mark habits as completed for the current day
- Automatically calculate habit streaks
- Save and load habit data using file persistence
- Responsive JavaFX user interface with background processing

---

## Technologies Used
- Java (JDK 21)
- JavaFX
- Maven
- Java NIO (file handling)
- Object-Oriented Programming principles

---

## Project Structure
src/main/java
├── com.savin.microhabits
│ ├── model # Domain models (Habit, HabitLog)
│ ├── service # Business logic (HabitService)
│ ├── storage # File persistence (FileStorage)
│ ├── ui # JavaFX UI components
│ └── Main.java # Application entry point


---

## How to Run
Ensure **Java 21** and **Maven** are installed.

From the project root, run:
```bash
mvn clean javafx:run
