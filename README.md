***

# Bookstore CLI - Lecture 7 (Winter 2026)
> **Baseline Codebase for Lecture 7: Services, IoC, & Dependency Injection**

[**Click here for the Lecture 7 Notes**](https://docs.google.com/document/d/1CNWeKsbrgE8uDYgdh7PUsoQvBi3bSh4NAqlRU6gNyZk/edit?usp=sharing)

This repository demonstrates the final stage of "decoupling" in a raw Java application. We move away from tight coupling where the application creates its own dependencies, and instead move to an **Inversion of Control (IoC)** model where dependencies are **Injected**.

## 🎯 Lecture 7 Learning Objectives
* **The Service Layer:** Implementing the "Chef" of the application to handle business rules.
* **Inversion of Control (IoC):** Moving the "Wiring Phase" out of the application logic and into a central assembler (`Main.java`).
* **Dependency Injection (DI):** Using Constructor Injection to pass repositories into services.
* **Plug-and-Play Architecture:** Swapping between different storage strategies at runtime without changing a single line of business logic.

## 🍽 The Restaurant Metaphor
To understand this architecture, we use the **Chef and Waiter** analogy:
*   **The Waiter (`App.java`):** The Presentation Layer. Takes orders (User Input) but doesn't know how to cook.
*   **The Pantry (`IRepository`):** The Data Access Layer. Holds the ingredients (Data) but doesn't know the recipes.
*   **The Chef (`BookstoreService`):** The Business Logic Layer. Receives the order from the Waiter, gets ingredients from the Pantry, applies the recipe (rules), and hands the result back.

## 🛠 Supported Storage Strategies
This codebase allows you to "plug in" different Pantries into the Chef at startup:
1.  **In-Memory (ArrayList):** A volatile storage strategy using standard Java collections.
2.  **H2 Database:** An in-memory SQL engine that runs inside your app (No Docker required!).
3.  **MySQL Database:** Persistent production storage (Requires Docker).

---

## 🚀 How it Works (The IoC Container)
The logic for choosing the repository has been moved to **`Main.java`**. When you start the application, you will see a prompt:

```text
Select Data Persistence Strategy:
1. In-Memory (ArrayList)
2. H2 Database (In-Memory SQL)
3. MySQL Database (Production)
```

Once you choose, `Main.java` instantiates the specific repository class and **injects** it into the `App` and `Service` layers via their constructors. The `App` layer never uses the `new` keyword to create a repository.

---

## 📋 Prerequisites
* **Java JDK:** 24
* **Maven:** 3.9+
* **Docker:** (Optional) Only required if choosing the MySQL strategy.

## 🚦 Getting Started

### 1. (Optional) Start Docker
If you intend to use Option 3 (MySQL):
```bash
docker-compose up -d
```

### 2. Compile and Run
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="csd214.bookstore.Main"
```

---

## 🏗 Project Structure
```text
src/main/java/csd214/bookstore/
├── App.java                 # Presentation Layer (The Waiter)
├── Main.java                # IoC Container (The Assembler/Wiring)
├── entities/                # Database Models
├── pojos/                   # UI Data Transfer Objects
├── services/                # Business Logic Layer (The Chef)
│   └── BookstoreService.java
└── repositories/            # Data Access Layer (The Pantry)
    ├── IRepository.java     # The Contract
    ├── InMemoryListRepository.java
    ├── H2Repository.java
    └── MySqlRepository.java
```

# Lab 5 Reflection

## 1. The Power of the Interface
Once the 3-tier architecture was established, no lines of code were modified in App.java or GuitarService when swapping repositories. <br>
Analysis: Interfaces act as a "contract." Because the Service only knows it is talking to an IRepository, it doesn't care if the data is stored in a List, a Map, or a Database. This decoupling is essential for large teams because it allows developers to work on separate layers (UI vs. Data) simultaneously without creating "merge conflicts" or breaking each other's logic.

## 2. Algorithmic Experience ($O(n)$ vs. $O(1)$)
Implementing the HashMap felt significantly more efficient. <br>
Analysis: In an ArrayList, the system must loop through every item until it finds a match ($O(n)$), which is slow for large datasets. The HashMap uses a key-based lookup ($O(1)$), providing near-instant access regardless of size. The Repository Pattern allows us to hide this complexity; the UI simply calls findById(), while we optimize the speed "under the hood."

## 3. Production Stability vs. Development Speed
While RAM-based storage is faster for development, MySqlRepository is the only choice for production.<br>
Analysis: RAM is volatile; if the application closes or the server restarts, all data is lost. MySQL provides Persistence, ensuring data survives across sessions. In a modern workflow, Docker provides the "Stable Infrastructure" needed to keep these persistent databases consistent across different development and production environments.

## 4. The Manual IoC Experience
Moving all new keywords to Main.java felt like creating a "Wiring Closet" for the entire application.<br>
Analysis: Centralizing instantiation makes the app easier to manage because you can see every dependency in one place. This "Wiring Phase" is exactly what Spring Boot does automatically. When Spring scans for @Service and @Repository, it is essentially building a giant internal version of our Main.java and "injecting" those objects wherever they are needed.

## 5. Single Responsibility & Reusability
Moving logic like applySeasonalTuneUp to the GuitarService made the presentation layer (App.java) much "cleaner" and focused only on user interaction.<br>
Analysis: By moving logic to the Service Layer, it becomes a "Portable Brain." If we built a Mobile App or a Web API tomorrow, we could reuse 100% of the Service and Repository code. Only the "Presentation" (the UI) would need to be rewritten, demonstrating the high reusability of a layered system.

## ⚖️ License
Educational use for CSD214 - Sault College.

