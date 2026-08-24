# Travel Booking Management System - Spring Boot REST API

A Spring Boot-based hotel booking system that manages hotels, rooms, and room bookings through a REST API. The application supports CRUD-style operations for hotels, rooms, and bookings, along with business validation such as room availability checks, duplicate-room prevention, and date validation.

## Features

- Manage hotels and their rooms
- Add, update, delete, and fetch hotel records
- Add, update, delete, and fetch room records
- Check room availability within a date range
- Create, update, cancel, delete, and fetch bookings
- Prevent booking conflicts for overlapping room reservations
- Automatic customer creation/reuse based on email
- Validation and standardized error responses
- Swagger/OpenAPI documentation
- H2 in-memory database for local development

## Tech Stack

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Hibernate
- H2 Database
- Bean Validation
- Springdoc OpenAPI / Swagger UI
- Maven

## Project Structure

```text
travelbooking/
├── src/
│   ├── main/
│   │   ├── java/com/example/travelbooking/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── TravelbookingApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/java/com/example/travelbooking/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Core Domain Model

### Hotel
- id
- name
- city

### Room
- id
- hotel
- roomNumber
- roomType
- pricePerNight

### Customer
- id
- name
- email
- phone

### Booking
- id
- customer
- room
- checkInDate
- checkOutDate
- totalAmount
- status

### Enums
- RoomType: SINGLE, DOUBLE, DELUXE, SUITE
- BookingStatus: CONFIRMED, CANCELLED

## Business Rules

The service layer enforces these validations:

- Hotel names and cities cannot be blank
- Room number cannot be blank
- Room price must be greater than zero
- Check-out date must be after check-in date
- Duplicate room numbers are not allowed within the same hotel
- A room cannot be deleted if it has existing bookings
- A hotel cannot be deleted if it still contains rooms
- A room cannot be booked if another confirmed booking overlaps the requested date range
- Cancelled bookings cannot be updated

## Application Configuration

The application runs on port 8080 and uses an H2 in-memory database.

Configuration file:

- travelbooking/src/main/resources/application.properties

Key properties include:

- Server port: 8080
- H2 database URL: jdbc:h2:mem:travelbookingdb
- H2 console enabled at /h2-console
- Swagger UI at /swagger-ui.html
- OpenAPI docs at /v3/api-docs

## Prerequisites

- Java 21+
- Maven 3.9+
- Git

## Run the Application

From the project root:

```bash
cd travelbooking
./mvnw spring-boot:run
```

Alternatively, run the packaged application:

```bash
cd travelbooking
./mvnw clean package
java -jar target/travelbooking-0.0.1-SNAPSHOT.jar
```

## Access Endpoints

Once the app is running:

- API base URL: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console

## REST API Endpoints

### Hotel Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | /api/hotels | Add a new hotel |
| GET | /api/hotels | Get all hotels |
| GET | /api/hotels/{hotelId} | Get hotel by ID |
| PUT | /api/hotels/{hotelId} | Update hotel |
| DELETE | /api/hotels/{hotelId} | Delete hotel |
| POST | /api/hotels/{hotelId}/rooms | Add a room to a hotel |
| GET | /api/hotels/rooms | Get all rooms |
| GET | /api/hotels/{hotelId}/rooms | Get all rooms for a hotel |
| GET | /api/hotels/{hotelId}/rooms/{roomId} | Get room by ID |
| PUT | /api/hotels/{hotelId}/rooms/{roomId} | Update room |
| DELETE | /api/hotels/{hotelId}/rooms/{roomId} | Delete room |
| GET | /api/hotels/{hotelId}/rooms/available?checkIn=dd-MM-yyyy&checkOut=dd-MM-yyyy | Get available rooms |

### Booking Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | /api/bookings | Book a room |
| GET | /api/bookings | Get all bookings |
| GET | /api/bookings/{bookingId} | Get booking by ID |
| PUT | /api/bookings/{bookingId} | Update booking |
| PATCH | /api/bookings/{bookingId}/cancel | Cancel booking |
| DELETE | /api/bookings/{bookingId} | Delete booking |

## Request/Response Examples

### Add Hotel

Request:

```http
POST /api/hotels
Content-Type: application/json

{
  "name": "Grand Horizon",
  "city": "Bengaluru"
}
```

Response:

```json
{
  "id": 1,
  "name": "Grand Horizon",
  "city": "Bengaluru"
}
```

### Add Room

Request:

```http
POST /api/hotels/1/rooms
Content-Type: application/json

{
  "roomNumber": "A101",
  "roomType": "DELUXE",
  "pricePerNight": 2500.00
}
```

Response:

```json
{
  "id": 1,
  "hotelId": 1,
  "hotelName": "Grand Horizon",
  "roomNumber": "A101",
  "roomType": "DELUXE",
  "pricePerNight": 2500.00
}
```

### Book Room

Request:

```http
POST /api/bookings
Content-Type: application/json

{
  "roomId": 1,
  "customerName": "Akarsha",
  "customerEmail": "akarsha@example.com",
  "customerPhone": "9876543210",
  "checkInDate": "15-08-2026",
  "checkOutDate": "18-08-2026"
}
```

Response:

```json
{
  "id": 1,
  "customerName": "Akarsha",
  "customerEmail": "akarsha@example.com",
  "customerPhone": "9876543210",
  "hotelId": 1,
  "hotelName": "Grand Horizon",
  "roomId": 1,
  "roomNumber": "A101",
  "roomType": "DELUXE",
  "checkInDate": "15-08-2026",
  "checkOutDate": "18-08-2026",
  "totalAmount": 7500.00,
  "status": "CONFIRMED"
}
```

### Check Available Rooms

Request:

```http
GET /api/hotels/1/rooms/available?checkIn=15-08-2026&checkOut=18-08-2026
```

## Error Handling

The application returns structured error responses in JSON format with details like timestamp, HTTP status, message, path, and validation errors (when applicable).

Example error response:

```json
{
  "timestamp": "2026-08-21T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Hotel not found with ID: 99",
  "path": "/api/hotels/99",
  "validationErrors": null
}
```

Common HTTP status codes:

- 200 OK: successful read/update requests
- 201 Created: successful insert operations
- 204 No Content: successful delete or cancel actions
- 400 Bad Request: invalid data or invalid date range
- 404 Not Found: resource not found
- 409 Conflict: duplicate or overlapping booking/resource conflict

## Testing

Run the project tests with:

```bash
cd travelbooking
./mvnw test
```

## Notes

This project is built as an in-memory demonstration API for hotel booking workflows and is suitable for learning, testing, and local development. It includes REST controllers, DTO validation, repository-based JPA persistence, and business logic for booking conflicts.
