# Library API

A Spring Boot RESTful API to manage library borrowers and books, supporting book borrowing and returning operations with validation and concurrency control.

---

## Features

- Register and manage borrowers with unique email addresses.
- Manage books identified by ISBN, with validation rules to ensure data consistency.
- Borrow and return books with business rules enforcement.
- Supports multiple copies of books with the same ISBN.
- Uses optimistic locking to prevent concurrent update conflicts.
- Custom exceptions to handle conflicts and resource not found cases.

---

## Technologies

- Java 17+
- Spring Boot
- Spring Data JPA with Hibernate
- H2 in-memory database (default; can be replaced)
- Lombok for boilerplate code reduction
- Jakarta Validation API
- JUnit 5 and Mockito for testing

---

## Entity Models

### Borrower

| Field | Type   | Description                  |
|-------|--------|------------------------------|
| id    | Long   | Auto-generated unique ID      |
| name  | String | Borrower's full name (required) |
| email | String | Unique email address (required) |

### Book

| Field    | Type     | Description                             |
|----------|----------|-----------------------------------------|
| id       | Long     | Auto-generated unique ID                  |
| isbn     | String   | ISBN number (required)                     |
| title    | String   | Book title (required)                        |
| author   | String   | Book author (required)                     |
| borrower | Borrower | Current borrower (nullable if available) |
| version  | Long     | Version for optimistic locking            |

---

## Business Rules

- **ISBN Uniqueness:**
    - Multiple copies with the same ISBN allowed if they have the **same title and author**.
    - If a new book has an existing ISBN but different title or author, the system throws a `DuplicateIsbnException`.

- **Borrowing Books:**
    - A book can only be borrowed if it’s not currently borrowed by someone else.
    - Attempting to borrow an already borrowed book throws a `BookAlreadyBorrowedException`.
    - Concurrent borrow operations are managed using optimistic locking.

- **Returning Books:**
    - Only the borrower who borrowed the book can return it.
    - Returning a book not currently borrowed throws a `BookAlreadyReturnedException`.

---

## API Endpoints

| Method | URL                                       | Description                         |
|--------|-------------------------------------------|-----------------------------------|
| POST   | `/api/borrowers`                          | Register a new borrower            |
| POST   | `/api/borrowers/{borrowerId}/borrow/{bookId}` | Borrow a book                    |
| POST   | `/api/borrowers/{borrowerId}/return/{bookId}` | Return a borrowed book           |
| GET    | `/api/borrowers`                          | Get list of all borrowers          |
| GET    | `/api/borrowers/{id}`                     | Get borrower details by ID         |

---

## Exception Handling

- **DuplicateIsbnException**  
  Thrown when a new book has an ISBN that exists but mismatched title/author.

- **BookAlreadyBorrowedException**  
  Thrown when trying to borrow a book that is already borrowed.

- **BookAlreadyReturnedException**  
  Thrown when trying to return a book that is not currently borrowed or borrowed by another user.

- **ResourceNotFoundException**  
  Thrown when borrower or book is not found.

- **OptimisticLockingFailureException**  
  Thrown when concurrent updates conflict on the same book record.

---

## Running the Application

### Prerequisites

- Java 17 or above
- Maven

### Steps

1. Clone the repository

   ```bash
   git clone <repository-url>
   cd library-api

2. Build the project and run the Spring Boot application using Maven Wrapper:

   ```bash
   ./mvnw spring-boot:run
3. Once the application starts, the REST API will be available at:
    
    ```bash
    http://localhost:8080/api



