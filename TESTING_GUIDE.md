# Flight Booking System - Testing Guide

## Prerequisites
- Application running on `http://localhost:8081`
- MongoDB running on `localhost:27017`
- Postman installed

## Import Collection
1. Open Postman
2. Import `FlightBookingSystem.postman_collection.json`
3. (Optional) Import `FlightBooking.postman_environment.json`

## Testing Sequence

### Phase 1: Setup Master Data (5-10 minutes)

**Step 1: Create Airlines**
- Execute: Admin Operations → 1-3 (Create Airlines)
- Verify: Admin Operations → 4 (Get All Airlines)
- Expected: 3 airlines created (IndiGo, Air India, SpiceJet)

**Step 2: Create Airports**
- Execute: Admin Operations → 7-10 (Create Airports)
- Verify: Admin Operations → 11 (Get All Airports)
- Expected: 4 airports created (DEL, BOM, BLR, MAA)

**Step 3: Add Flight Inventory**
- Execute: Admin Operations → 13-15 (Add Flights)
- Note: Copy `flightId` from any response
- Update collection variable: `flightId` = copied ID

### Phase 2: Flight Search & Booking (10-15 minutes)

**Step 4: Search Flights**
- Execute: Flight Operations → 1 (Search DEL to BOM)
- Verify: Response contains flight list with available seats
- Note: Verify `departureDateTime`, `availableSeats`, `baseFare`

**Step 5: Get Flight Details**
- Execute: Flight Operations → 3 (Get Flight by ID)
- Verify: Complete flight information returned

**Step 6: Check Seat Map**
- Execute: Flight Operations → 4 (Get Seat Map)
- Verify: Array of seats with availability status
- Note: Available seats have `isAvailable: true`

**Step 7: Create Booking**
- Execute: Booking Operations → 1 (Single Passenger)
- Verify: PNR generated, status = "CONFIRMED"
- Note: Copy `pnr` from response
- Update collection variable: `pnr` = copied PNR

**Step 8: Verify Booking**
- Execute: Booking Operations → 3 (Get by PNR)
- Verify: Complete ticket details with passenger info

**Step 9: Check Booking History**
- Execute: Booking Operations → 4 (Get History)
- Verify: All bookings for the email address

### Phase 3: Cancellation Testing (5 minutes)

**Step 10: Cancel Booking**
- Execute: Booking Operations → 7 (Cancel Booking)
- Verify:
    - Status changed to "CANCELLED"
    - Refund amount calculated
    - `cancellationDateTime` present

**Step 11: Verify Cancellation**
- Execute: Booking Operations → 3 (Get by PNR)
- Verify: Status shows "CANCELLED"

### Phase 4: Edge Cases (10 minutes)

**Test Case 1: Invalid Flight ID**
- Execute: Get Flight by ID with invalid ID
- Expected: 404 Not Found error

**Test Case 2: Invalid PNR**
- Execute: Get Booking by PNR with "INVALID123"
- Expected: Booking not found error

**Test Case 3: Duplicate Booking**
- Try booking same seats twice
- Expected: Seat unavailable error

**Test Case 4: Past Date Search**
- Search flights with past date
- Expected: Validation error

**Test Case 5: Same Origin-Destination**
- Search with origin = destination
- Expected: Validation error

## Expected Results Summary

| Operation | Expected Status | Expected Response |
|-----------|----------------|-------------------|
| Create Airline | 201 Created | Airline object with ID |
| Create Airport | 201 Created | Airport object with ID |
| Add Inventory | 201 Created | Flight ID |
| Search Flights | 200 OK | Array of flights |