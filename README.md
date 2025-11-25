

#  Flight Booking System – Reactive Spring WebFlux & MongoDB

A fully functional **reactive flight booking platform** built using **Spring WebFlux** and **MongoDB**, designed for high-throughput, non-blocking performance.
The system supports **flight search**, **real-time seat availability**, **booking & PNR generation**, **cancellation with refund logic**, and complete **admin inventory management**.

---

##  Overview

This project was developed as an end-to-end learning and implementation of **Reactive Programming**, **MongoDB document-based design**, and **REST API engineering** using Spring Boot 3.x.

The system ensures:

* Non-blocking request handling
* Real-time seat status updates
* Atomic booking & cancellation logic
* Highly scalable design suitable for production workloads

**Developed by:** *Aryan Krishna*
**Submission Date:** *November 25, 2025*
**Java Version:** *21*
**Spring Boot Version:** *3.2.6*

---

#  Table of Contents

* [Features](#features)
* [Tech Stack](#tech-stack)
* [Architecture](#architecture)
* [Prerequisites](#prerequisites)
* [Installation](#installation)
* [API Documentation](#api-documentation)
* [Business Rules](#business-rules)
* [Database Schema](#database-schema)
* [Project Structure](#project-structure)
* [Testing Guide](#testing-guide)
* [Future Enhancements](#future-enhancements)

---

#  Features

###  1. Flight Search

* Search by origin, destination, date, cabin class
* Filters for one-way & round trip
* Live seat availability
* Duration calculation

### ️ 2. Booking Management

* Multi-passenger bookings
* PNR generation (`PNRXXXXXX`)
* Fare breakdown (base + seat charges)
* Meal preferences
* Auto seat locking

###  3. Real-Time Seat Management

* 180-seat map generated automatically
* Business (1–3), Economy (4–30)
* Window, aisle, middle seat pricing
* Atomic seat updates (no race conditions)

###  4. Cancellation & Refunds

* 100%, 75%, or 0% refund depending on time left
* <24 hrs → cancellation blocked
* Instant seat release & availability update

###  5. Admin Operations

* Add/update airlines
* Add/update airports
* Add flight inventory
* Modify schedules

###  6. Reactive Architecture

* Fully non-blocking I/O
* Uses Mono/Flux
* Optimized for concurrency

---

#  Tech Stack

### Back-end Technologies

* **Java 21**
* **Spring Boot 3.2.6**
* **Spring WebFlux (Netty)**
* **Spring Data MongoDB Reactive**
* **Reactor Core**

### Libraries

* **Lombok**
* **Jakarta Validation**
* **SpringDoc OpenAPI 3**

### Build Tool

* **Maven 3.6+**

---

#  Architecture

```
Client → Controller → Service → Repository → MongoDB
```

**Reactive Flow Example (Booking):**

```
POST /bookings  
 → BookingService  
   → FlightRepository: check availability  
   → BookingRepository: save  
   → FlightRepository: update seat map  
 → Return: Mono<BookingResponse>
```

---

#  Prerequisites

Make sure you have installed:

| Tool    | Version |
| ------- | ------- |
| Java    | 21+     |
| MongoDB | 4.4+    |
| Maven   | 3.6+    |

---

#  Setup & Installation

###  Clone the Repository

```bash
git clone <your-repo-url>
cd FlightBookingSystemWebFlux
```

###  Start MongoDB

```bash
mongod --dbpath /path/to/data
```

###  Build the Project

```bash
mvn clean install
```

###  Run the Application

```bash
mvn spring-boot:run
```

The API will start at:
👉 **[http://localhost:8081](http://localhost:8081)**

---

#  API Documentation

## Base URL

```
http://localhost:8081/webjars/swagger-ui/index.html#
```

---

#  Flight Operations

###  Search Flights

```
POST /flights/search
```

```json
{
  "origin": "DEL",
  "destination": "BOM",
  "departureDate": "2025-11-26",
  "passengers": 2,
  "tripType": "ONEWAY"
}
```

###  Flight Details

```
GET /flights/{flightId}
```

###  Seat Map

```
GET /flights/{flightId}/seats
```

---

#  Booking Operations

###  Create Booking

```
POST /bookings
```

```json
{
  "flightId": "673ffd61b80b2a1fa4d19cc1",
  "contactName": "John Doe",
  "contactEmail": "john@example.com",
  "passengers": [
    {
      "name": "John Doe",
      "gender": "MALE",
      "age": 30,
      "seatNumber": "4A",
      "mealPreference": "VEG"
    }
  ],
  "seatNumbers": ["4A"]
}
```

###  Fetch by PNR

```
GET /bookings/pnr/{pnr}
```

###  Booking History

```
GET /bookings/user/{email}
```

###  Cancel Booking

```
DELETE /bookings/{pnr}
```

---

#  Admin Operations

###  Add Flight Inventory

```
POST /admin/inventory
```

###  Add Airline

```
POST /admin/airlines
```

###  Add Airport

```
POST /admin/airports
```

---

#  Business Rules

###  PNR Rules

* Format: `PNRXXXXXX`
* 9 characters
* Guaranteed unique

###  Cancellation Policy

| Hours Before Departure | Refund | Allowed? |
| ---------------------- | ------ | -------- |
| ≥48 hrs                | 100%   | Yes      |
| 24–48 hrs              | 75%    | Yes      |
| <24 hrs                | 0%     | ❌ No     |

###  Seat Pricing

| Seat Type      | Charge |
| -------------- | ------ |
| Window (A,F)   | ₹200   |
| Aisle (C,D)    | ₹100   |
| Middle (B,E)   | ₹0     |
| Business Class | +₹2000 |

---

#  MongoDB Schema

### Collections:

* `airlines`
* `airports`
* `flights`
* `bookings`

Each includes automatic timestamps, indexes, and validation structures.

---

#  Project Structure

```
src/main/java/com/
├── flightapp/FlightBookingApplication.java
├── config/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── model/
├── repository/
├── service/
├── validator/
├── exception/
└── util/
```

---

#  Testing Guide (Postman)

1. Create Airlines
2. Create Airports
3. Add Flight Inventory
4. Search Flights
5. Create Booking
6. Retrieve Booking
7. Cancel Booking

Full Postman collection available in:
`/postman/FlightBookingSystem.postman_collection.json`

---

#  Future Enhancements

* JWT authentication
* Multi-flight itinerary support
* Payment gateway simulation
* Real-time WebSocket seat updates
* User profile management
* Dynamic pricing engine

---


