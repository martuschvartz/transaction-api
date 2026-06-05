# Transactions API

A RESTful web service that stores transactions **in memory** and returns information about them.

Each transaction has a `type` and an `amount`, and can be linked to another one through a `parent_id`.
The service allows you to:

- Create a transaction by its id.
- List the ids of all transactions of a given type.
- Get the sum of the amounts of all transactions transitively connected
  (through `parent_id`) to a given transaction.

It does not use a database: storage is an in-memory `Map`, kept alive for the duration
of the application's execution.

## Tech stack

| | |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 (Spring Web MVC) |
| Build | Maven (via Maven Wrapper `./mvnw`) |
| Utilities | Lombok |
| Tests | JUnit 5, Spring Boot Test (MockMvc) |
| Container | Docker (Eclipse Temurin 21 image) |

## Architecture

The project follows a layered architecture:

```
Controller  (REST / JSON)         →  com.example.transactions_api.controllers
Service     (business logic)      →  com.example.transactions_api.services
DAO         (persistence)         →  com.example.transactions_api.repository
Model / DTO                       →  com.example.transactions_api.models / .dto
```

Layers depend on **interfaces** (`com.example.transactions_api.interfaces`), not on
concrete implementations, to keep them decoupled.

## Build and run

### Local (Maven)

Package the project:

```bash
./mvnw clean package
```

Run the application in development mode:

```bash run
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### Docker

 **Make sure the Docker daemon is running.**

Build the image using the current directory as context:

```bash
docker build -t transactions-api .
```

> ℹ️ **NOTE:** The `Dockerfile` is multi-stage: it compiles the project in a JDK stage
> and ships only the resulting `.jar` running on a JRE in the final image. 
> No need to package beforehand.

Run a container publishing port 8080:

```bash
docker run -p 8080:8080 transactions-api
```

The API will be available at `http://localhost:8080`, same as running locally.

### Tests

```bash
./mvnw test
```

Includes unit tests (Repository and Service) and integration tests (Controller, via MockMvc).

## Endpoints

For all endpoints, base path is `/transactions`

### `PUT /transactions/{id}` - Create transaction

Creates (or replaces) the transaction with the id given in the URL.

**Body:**

```json
{ "amount": 5000, "type": "cars", "parent_id": 10 }
```

| Field | Type | Required | Description |
|---|---|---|---|
| `amount` | double | yes | Transaction amount. |
| `type` | string | yes | Transaction type. |
| `parent_id` | long | no | Id of the parent transaction. If omitted, it is a root transaction. |

**Response:** `200 OK`

```json
{ "status": "ok" }
```

**Example:**

```bash
curl -i -X PUT http://localhost:8080/transactions/10 \
  -H "Content-Type: application/json" \
  -d '{"amount":5000,"type":"cars"}'
```

### `GET /transactions/types` - List existing types

> **Extra endpoint** (not part of the original spec). It is a convenience helper for
> `GET /transactions/types/{type}`: it lets the user discover which type values currently
> exist so they know what to query.
>
> Note: there is intentionally no equivalent "list all" helper for the sum endpoint. A
> plain unfiltered `GET` would return every transaction, which would eventually call for
> pagination (out of scope).

Returns the set of all existing transaction types.

**Response:** `200 OK` (empty array `[]` if there are none)

```json
["cars", "shopping"]
```

**Example:**

```bash
curl -i http://localhost:8080/transactions/types
```

### `GET /transactions/types/{type}` - List ids by type

Returns the ids of all transactions of a given type.

**Response:** `200 OK` (empty list `[]` if there are none)

```json
[10, 11]
```

**Example:**

```bash
curl -i http://localhost:8080/transactions/types/cars
```

### `GET /transactions/sum/{id}` - Transitive sum

Returns the sum of the amounts of all transactions transitively connected to the given
transaction (itself plus all its descendants via `parent_id`).

**Response:** `200 OK`

```json
{ "sum": 20000.0 }
```

**Example:**

```bash
curl -i http://localhost:8080/transactions/sum/10
```

## Example of Complete Flow

```bash
# Create a root transaction
curl -X PUT http://localhost:8080/transactions/10 \
  -H "Content-Type: application/json" \
  -d '{"amount":5000,"type":"cars"}'

# Create a transaction that is a child of 10
curl -X PUT http://localhost:8080/transactions/11 \
  -H "Content-Type: application/json" \
  -d '{"amount":10000,"type":"shopping","parent_id":10}'

# Create a transaction that is a child of 11 (grandchild of 10)
curl -X PUT http://localhost:8080/transactions/12 \
  -H "Content-Type: application/json" \
  -d '{"amount":5000,"type":"shopping","parent_id":11}'

curl http://localhost:8080/transactions/types/cars    # > [10]
curl http://localhost:8080/transactions/sum/10        # > {"sum":20000.0}
curl http://localhost:8080/transactions/sum/11        # > {"sum":15000.0}
```
