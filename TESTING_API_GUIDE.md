

# Flight Booking System – Testing Guide

This guide walks you through end-to-end testing of the Flight Booking System APIs using Postman and MongoDB. It covers **happy paths**, **edge cases**, and **basic regression checks**.

---

## 1. Prerequisites

Make sure all of these are ready **before** you start:

* Spring Boot application running at: `http://localhost:8081`
* MongoDB running at: `mongodb://localhost:27017`
* Postman installed
* Imported collection:

    * `Flight-Booking-API.postman_collection.json`
* (Optional) Postman environment:

    * `FlightBooking.postman_environment.json`
    * Make sure it has variables: `flightId`, `pnr`


---

## 2. Import & Basic Setup

### 2.1 Import Collection

1. Open **Postman**
2. Click **Import → File**
3. Select: `Flight-Booking-API.postman_collection.json`
4. (Optional) Import associated environment JSON and select it in the top-right environment dropdown.

### 2.2 Verify Health Check (Sanity Check – 1 min)

**Request:**

* Collection: `Health Check → Health Check`
* Method: `GET /api/v1/health`

**Expected:**

* HTTP Status: `200 OK`
* Response body: Simple JSON like:

  ```json
  {
    "status": "UP"
  }
  ```

  (Exact fields may vary; just ensure service is up.)

---

## 3. Testing Sequence Overview

1. **Phase 1:** Setup Master Data (Airlines, Airports, Inventory)
2. **Phase 2:** Flight Search & Booking
3. **Phase 3:** Cancellation & Related Operations
4. **Phase 4:** Edge Cases & Negative Testing
5. **Phase 5 (Optional):** Regression / Additional Coverage

Approximate time: **30–40 minutes** for full flow.

---

## Phase 1: Setup Master Data (5–10 minutes)

### Step 1: Create Airlines

**Requests (Admin Operations):**

* `1. Create Airline - IndiGo`
* `2. Create Airline - Air India`
* `3. Create Airline - SpiceJet`

Each is:

* Method: `POST /api/v1/admin/airlines`
* Body: Airline details (code, name, contact info, website, logo URL)

**Verify:**

1. Execute: `4. Get All Airlines`

    * `GET /api/v1/admin/airlines`
2. Optionally execute: `5. Get Airline by Code` (e.g., `6E`)

**Expected:**

* Status: `201 Created` for each create
* `GET` returns an array with at least:

    * `6E` – IndiGo
    * `AI` – Air India
    * `SG` – SpiceJet
* `Get Airline by Code` returns the matching airline object.

>  **Check:** Fields like `airlineCode`, `name`, `contactEmail`, `contactPhone`, `website` are non-null and match what you sent.

---

### Step 2: Create Airports

**Requests (Admin Operations):**

* `7. Create Airport - Delhi`
* `8. Create Airport - Mumbai`
* `9. Create Airport - Bangalore`
* `10. Create Airport - Chennai`

Each is:

* Method: `POST /api/v1/admin/airports`
* Body: `iataCode`, `name`, `city`, `country`, `timezone`

**Verify:**

1. Execute: `11. Get All Airports`
2. Execute: `12. Get Airport by Code` e.g., `DEL`

**Expected:**

* Status: `201 Created` for each create
* `GET /airports` returns at least:

    * DEL – Indira Gandhi International Airport
    * BOM – Chhatrapati Shivaji Maharaj International Airport
    * BLR – Kempegowda International Airport
    * MAA – Chennai International Airport
* `Get Airport by Code` returns correct single airport JSON.

>  **Check:** `iataCode` values are unique and correct (DEL, BOM, BLR, MAA).

---

### Step 3: Add Flight Inventory

**Requests (Admin Operations):**

* `13. Add Flight Inventory - DEL to BOM`
* `14. Add Flight Inventory - DEL to BLR`
* `15. Add Flight Inventory - BOM to DEL`

All are:

* Method: `POST /api/v1/admin/inventory`
* Body includes:

    * `airlineCode`
    * `flightNumber`
    * `origin`, `destination`
    * `departureDateTime`, `arrivalDateTime`
    * `totalSeats`, `baseFare`, `currency`

**Actions:**

1. Send any one of the `Add Flight Inventory` requests.
2. From the response, copy the `id` or `flightId` returned.
3. In Postman:

    * Go to **Variables** (Collection or Environment level)
    * Set: `flightId` = copied value

**Optional: Update Flight Inventory**

* Use `16. Update Flight Inventory`
* Method: `PUT /api/v1/admin/inventory/{{flightId}}`
* Modify `departureDateTime` or `baseFare` and verify update.

**Expected:**

* Status: `201 Created` for initial add
* Status: `200 OK` or `204 No Content` for update (depending on implementation)
* Updated flight reflects changed fields when fetched later.

---

## Phase 2: Flight Search & Booking (10–15 minutes)

### Step 4: Search Flights

**Request:**

* `Flight Operations → 1. Search Flights - DEL to BOM`
* Method: `POST /api/v1/flights/search`
* Example body:

  ```json
  {
    "origin": "DEL",
    "destination": "BOM",
    "departureDate": "2025-11-25",
    "passengers": 2,
    "tripType": "ONEWAY",
    "cabinClass": "ECONOMY"
  }
  ```

**Expected:**

* Status: `200 OK`
* Response: Array of flight options with:

    * `origin`, `destination`
    * `departureDateTime`, `arrivalDateTime`
    * `availableSeats`
    * `baseFare`, `currency`

>  **Check:** `availableSeats` ≤ `totalSeats` that you created in inventory.

---

### Step 5: Get Flight Details by ID

**Request:**

* `Flight Operations → 3. Get Flight by ID`
* Method: `GET /api/v1/flights/{{flightId}}`

**Expected:**

* Status: `200 OK`
* Response: Full flight JSON:

    * Includes `flightNumber`, `airlineCode`, `origin`, `destination`
    * Includes schedule and pricing details

>  **Check:** Matches the inventory you created/updated.

---

### Step 6: Check Seat Map

**Request:**

* `Flight Operations → 4. Get Seat Map`
* Method: `GET /api/v1/flights/{{flightId}}/seats`

**Expected:**

* Status: `200 OK`
* Response: Array of seats e.g.:

  ```json
  [
    {
      "seatNumber": "1A",
      "isAvailable": true,
      "cabinClass": "ECONOMY"
    }
  ]
  ```

>  **Check:** Before booking, seats like `4A`, `5A`, `5B`, `5C` should be `isAvailable: true`.

---

### Step 7: Create Booking – Single Passenger

**Request:**

* `Booking Operations → 1. Create Booking - Single Passenger`
* Method: `POST /api/v1/bookings`
* Example body:

  ```json
  {
    "flightId": "{{flightId}}",
    "contactName": "Rohit Sharma",
    "contactEmail": "rohit.sharma@example.com",
    "passengers": [
      {
        "name": "Rohit Sharma",
        "gender": "MALE",
        "age": 35,
        "seatNumber": "4A",
        "mealPreference": "NON_VEG"
      }
    ],
    "seatNumbers": ["4A"]
  }
  ```

**Actions:**

1. Send request and get response.
2. Copy `pnr` from response.
3. Set Postman variable: `pnr = <copied PNR>`

**Expected:**

* Status: `201 Created`
* Response includes:

    * `pnr` (unique)
    * `status: "CONFIRMED"`
    * `totalAmount`, `currency`
    * `flight` & `passengers` info

>  **Check:** After booking, re-check seat map – `4A` should now have `isAvailable: false` (if implemented that way).

---

### Step 8: Verify Booking by PNR

**Request:**

* `Booking Operations → 3. Get Booking by PNR`
* Method: `GET /api/v1/bookings/pnr/{{pnr}}`

**Expected:**

* Status: `200 OK`
* Response:

    * `pnr` matches variable
    * `status: "CONFIRMED"`
    * Correct `flightId`
    * Passenger list matches booking request

---

### Step 9: Check Booking History by Email

**Request:**

* `Booking Operations → 4. Get Booking History by Email`
* Method: `GET /api/v1/bookings/user/rohit.sharma@example.com`

**Expected:**

* Status: `200 OK`
* Response: Array of bookings made with that email
* Contains the booking you just created.

---

### Step 10 (Optional): Download Ticket PDF

**Request:**

* `Booking Operations → 5. Download Ticket PDF`
* Method: `GET /api/v1/bookings/{{pnr}}/download`

**Expected:**

* Status: `200 OK`
* Content-Type: Likely `application/pdf`
* Binary PDF data returned


---

### Step 11 (Optional): Resend Booking Email

**Request:**

* `Booking Operations → 6. Resend Booking Email`
* Method: `POST /api/v1/bookings/{{pnr}}/resend-email`

**Expected:**

* Status: `200 OK` or `202 Accepted`
* Body may contain message like `"Email sent"` (implementation-specific)

---

## Phase 3: Cancellation Testing (5 minutes)

### Step 12: Cancel Booking

**Request:**

* `Booking Operations → 7. Cancel Booking`
* Method: `DELETE /api/v1/bookings/{{pnr}}`

**Expected:**

* Status: `200 OK` or `204 No Content` (depends on implementation)
* If body returned, check:

    * `status: "CANCELLED"`
    * `refundAmount` present (if implemented)
    * `cancellationDateTime` present

---

### Step 13: Verify Cancellation by PNR

**Request:**

* `GET /api/v1/bookings/pnr/{{pnr}}` (same as Step 8)

**Expected:**

* Status: `200 OK`
* `status: "CANCELLED"`
* (Optional) `refundAmount`, `cancellationDateTime` still visible

> ✅ **Check:** If seat map logic is implemented, previously booked seat (e.g., `4A`) should now be `isAvailable: true` again.

---

## Phase 4: Edge Cases & Negative Tests (10 minutes)

These you can run by editing the existing requests in Postman and observing error responses.

### Test Case 1: Invalid Flight ID

* Use `Flight Operations → 3. Get Flight by ID`
* Set `flightId` to something invalid like `INVALID_FLIGHT_ID`

**Expected:**

* Status: `404 Not Found`
* Error body with message like `"Flight not found"` (text may vary)

---

### Test Case 2: Invalid PNR

* Use `Booking Operations → 3. Get Booking by PNR`
* Set `pnr` = `INVALID123`

**Expected:**

* Status: `404 Not Found`
* Error body message like `"Booking not found"`.

---

### Test Case 3: Duplicate Booking (Seat Already Taken)

1. Ensure one booking is already made on seat `4A` (or another seat).
2. Try **creating another booking** on same flight with **same seatNumber `4A`** using `Create Booking` request.

**Expected:**

* Status: Likely `400 Bad Request` or `409 Conflict`
* Error message: seat unavailable / already booked.

---

### Test Case 4: Past Date Search

* Use `Flight Operations → Search Flights` request.
* Set `departureDate` to a **past date**, e.g. `"2020-01-01"`.

**Expected:**

* Either:

    * Validation error: `400 Bad Request` with message like `"Departure date must be in the future"` **OR**
    * Empty result set with no flights (depending on your service design).


---

### Test Case 5: Same Origin and Destination

* Modify search body:

  ```json
  {
    "origin": "DEL",
    "destination": "DEL"
  }
  ```

**Expected:**

* Status: `400 Bad Request`
* Message indicating origin and destination cannot be the same.

---

### Test Case 6 (Optional): Missing Required Fields

* Remove `origin` or `destination` from request body.

**Expected:**

* Status: `400 Bad Request`
* Validation error with field name.

---

## Phase 5 (Optional): Additional Regression Checks

1. **Update Airline and Verify**

    * Use `6. Update Airline` to change `name` or `logoUrl`.
    * Re-call `Get Airline by Code` and check updated value.

2. **Update Flight Inventory and Verify**

    * Use `16. Update Flight Inventory` to change `baseFare` or `departureDateTime`.
    * Re-call `Get Flight by ID` and confirm update.

3. **Multiple Passenger Booking**

    * Use `2. Create Booking - Multiple Passengers`
    * Verify:

        * All passengers present
        * Multiple seatNumbers saved
        * Total amount reflects multiple travelers

---

## Expected Results Summary (Extended)

| Operation                        | Endpoint (Method)                                       | Expected Status   | Expected Response                                  |
| -------------------------------- | ------------------------------------------------------- | ----------------- | -------------------------------------------------- |
| Health Check                     | `GET /api/v1/health`                                    | 200 OK            | Simple JSON (`status: "UP"` or similar)            |
| Create Airline                   | `POST /api/v1/admin/airlines`                           | 201 Created       | Airline object with generated ID                   |
| Get All Airlines                 | `GET /api/v1/admin/airlines`                            | 200 OK            | Array of airlines                                  |
| Get Airline by Code              | `GET /api/v1/admin/airlines/{code}`                     | 200 OK / 404      | One airline or not found error                     |
| Create Airport                   | `POST /api/v1/admin/airports`                           | 201 Created       | Airport object with ID                             |
| Get All Airports                 | `GET /api/v1/admin/airports`                            | 200 OK            | Array of airports                                  |
| Get Airport by Code              | `GET /api/v1/admin/airports/{iata}`                     | 200 OK / 404      | One airport or not found error                     |
| Add Inventory                    | `POST /api/v1/admin/inventory`                          | 201 Created       | Flight object with ID                              |
| Update Inventory                 | `PUT /api/v1/admin/inventory/{flightId}`                | 200/204           | Updated flight or empty body                       |
| Search Flights                   | `POST /api/v1/flights/search`                           | 200 OK            | Array of flights                                   |
| Get Flight by ID                 | `GET /api/v1/flights/{flightId}`                        | 200 OK / 404      | Flight object or error                             |
| Get Seat Map                     | `GET /api/v1/flights/{flightId}/seats`                  | 200 OK            | Array of seat objects                              |
| Create Booking (Single/Multiple) | `POST /api/v1/bookings`                                 | 201 Created       | Booking with PNR, status = CONFIRMED               |
| Get Booking by PNR               | `GET /api/v1/bookings/pnr/{pnr}`                        | 200 OK / 404      | Booking details or not found                       |
| Get Booking History by Email     | `GET /api/v1/bookings/user/{email}`                     | 200 OK            | Array of bookings                                  |
| Download Ticket PDF              | `GET /api/v1/bookings/{pnr}/download`                   | 200 OK / 404      | PDF binary or error                                |
| Resend Booking Email             | `POST /api/v1/bookings/{pnr}/resend-email`              | 200/202 / 404     | Success message or not found                       |
| Cancel Booking                   | `DELETE /api/v1/bookings/{pnr}`                         | 200/204 / 404     | Status changed to CANCELLED or not found           |
| Invalid Flight ID                | `GET /api/v1/flights/{invalidId}`                       | 404 Not Found     | Error JSON                                         |
| Invalid PNR                      | `GET /api/v1/bookings/pnr/INVALID123`                   | 404 Not Found     | Error JSON                                         |
| Duplicate Seat Booking           | `POST /api/v1/bookings` (same seat again)               | 400/409           | Seat unavailable error                             |
| Past Date Search                 | `POST /api/v1/flights/search` (past departureDate)      | 400 / 200 (empty) | Validation error or empty list (document behavior) |
| Same Origin/Destination Search   | `POST /api/v1/flights/search` (`origin == destination`) | 400 Bad Request   | Validation error                                   |

---

