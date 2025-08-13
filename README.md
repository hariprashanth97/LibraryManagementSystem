# Library API

A Spring Boot RESTful API to manage library borrowers and books, supporting book borrowing and returning operations with validation and business rules enforcement.

---

## Features

- Register and manage borrowers with unique email addresses.
- Manage books identified by ISBN, with validation rules to ensure data consistency.
- Borrow and return books with business rules enforcement.
- Supports multiple copies of books with the same ISBN.
- Custom exceptions to handle conflicts and resource not found cases.

---

## Technologies

- Java 17+
- Spring Boot
- Spring Data JPA with Hibernate
- H2 in-memory database (default; can be replaced with MySQL)
- Lombok for boilerplate code reduction
- Jakarta Validation API
- JUnit 5 and Mockito for testing

---

## Entity Models

### Borrower

| Field | Type   | Description                     |
| ----- | ------ | ------------------------------- |
| id    | Long   | Auto-generated unique ID        |
| name  | String | Borrower's full name (required) |
| email | String | Unique email address (required) |

### Book

| Field    | Type     | Description                              |
| -------- | -------- | ---------------------------------------- |
| id       | Long     | Auto-generated unique ID                 |
| isbn     | String   | ISBN number (required)                   |
| title    | String   | Book title (required)                    |
| author   | String   | Book author (required)                   |
| borrower | Borrower | Current borrower (nullable if available) |

---

## Business Rules

- **ISBN Uniqueness:** Multiple copies with the same ISBN allowed if title and author match. Otherwise, throws `DuplicateIsbnException`.
- **Borrowing Books:**
    - Only available books can be borrowed.
    - Attempting to borrow an already borrowed book throws `BookAlreadyBorrowedException`.
- **Returning Books:**
    - Only the borrower who borrowed the book can return it.
    - Returning a book not borrowed or by another borrower throws `BookAlreadyReturnedException`.

---

## API endpoint

```
http://localhost:8080/api/borrowers/1/return/1
```

---

## API Endpoints

### Book Endpoints

| Method | URL          | Description           |
| ------ | ------------ | --------------------- |
| POST   | `/api/books` | Add a new book        |
| GET    | `/api/books` | Get list of all books |

#### Example: Add Book

**Request:**

```json
POST /api/books
{
  "isbn": "978-1234567890",
  "title": "Spring Boot in Action",
  "author": "Craig Walls"
}
```

**Response:**

```json
{
  "id": 1,
  "isbn": "978-1234567890",
  "title": "Spring Boot in Action",
  "author": "Craig Walls"
}
```

---

### Borrower Endpoints

| Method | URL                                           | Description                |
| ------ | --------------------------------------------- | -------------------------- |
| POST   | `/api/borrowers`                              | Register a new borrower    |
| GET    | `/api/borrowers`                              | Get list of all borrowers  |
| GET    | `/api/borrowers/{id}`                         | Get borrower details by ID |
| POST   | `/api/borrowers/{borrowerId}/borrow/{bookId}` | Borrow a book              |
| POST   | `/api/borrowers/{borrowerId}/return/{bookId}` | Return a borrowed book     |

#### Example: Register Borrower

**Request:**

```json
POST /api/borrowers
{
  "name": "test",
  "email": "test@example.com"
}
```

**Response:**

```json
{
  "id": 2,
  "name": "test",
  "email": "test@example.com"
}
```

#### Example: Borrow Book

**Request:**

```json
POST /api/borrowers/1/borrow/1
```

**Response:**

```json
{
  "id": 2,
  "name": "test",
  "email": "test@example.com",
  "message": "Book Borrowed Successfully",
  "bookResponse": {
    "id": 1,
    "isbn": "96225",
    "title": "Chair",
    "author": "Da 'neil oman"
  }
}
```

#### Example: Return Book

**Request:**

```json
POST /api/borrowers/1/return/1
```

**Response:**

```json
{
  "id": 2,
  "name": "test",
  "email": "test@example.com",
  "message": "Book Returned Successfully",
  "bookResponse": {
    "id": 1,
    "isbn": "96225",
    "title": "Chair",
    "author": "Da 'neil oman"
  }
}
```

---

## Exception Handling

| Exception                    | HTTP Status | Description                           |
| ---------------------------- | ----------- | ------------------------------------- |
| DuplicateIsbnException       | 422         | ISBN exists but title/author mismatch |
| BookAlreadyBorrowedException | 422         | Book is already borrowed              |
| BookAlreadyReturnedException | 422         | Book is already returned              |
| ResourceNotFoundException    | 404         | Borrower or Book not found            |

**Example Error Response:**

```json
{
  "status": 422,
  "error": "BookAlreadyBorrowedException",
  "message": "This book is already borrowed",
  "timestamp": "2025-08-13T12:34:56"
}
```

---

## Running the Application

### Prerequisites

- Java 17 or above
- Maven

### Steps

```bash
git clone <repository-url>
cd library-api
mvn clean install
mvn spring-boot:run
```

API available at:

```
http://localhost:8080/api
```

---

## Running with Docker

### Build and Run Docker Image

```bash
docker compose up --build
```

- API available at `http://localhost:8080/api`
- To use MySQL instead of H2, mount your `application-prod.properties` and set:

```bash
-e SPRING_PROFILES_ACTIVE=prod
```

---

## Running with Kubernetes

### Build and Push Docker Image (if not already done)

```bash
docker build -t library-api:latest .
docker tag library-api:latest <your-docker-repo>/library-api:latest
docker push <your-docker-repo>/library-api:latest
```

### Deploy MySQL

```bash
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/mysql-service.yaml
kubectl wait --for=condition=ready pod -l app=mysql --timeout=120s
```

### Deploy Library API

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl rollout status deployment/library-api
```

- LoadBalancer Service provides an external IP to access the API:

```bash
kubectl get svc
kubectl get pods
```

## Running Unit Tests and Coverage

### Run Tests

```bash
mvn test
```

### Generate Coverage Report with Jacoco

```bash
mvn jacoco:report
```

- Coverage report will be available at `target/site/jacoco/index.html`
- Open the HTML file in a browser to view detailed coverage metrics for all services, controllers, and repositories.


## Using Postman for API Testing

1. Open Postman.
2. Import the provided Postman collection file (`Library API.postman_collection.json`).
3. Set the `baseUrl` environment variable to your API URL (e.g., `http://localhost:8080/api`).
4. Run individual requests or execute the full collection to test all endpoints.
5. Review responses and ensure HTTP status codes and payloads match expectations.