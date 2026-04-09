# Airline Reservation System

This Java implementation converts the DCD into code for the COMP 371 Airline Reservation System project.

## Requirements
The project requires a working implementation with:
- a user interface
- file system or database persistence
- application logic
- components needed for a working system

This solution provides:
- **Swing UI** with tabs for Search Flights, Book Reservation, Confirm Reservation, Cancel Reservation, and View Reservations
- **File persistence** using CSV files in the `data/` folder
- **Application logic** in `ReservationController`
- **DCD-based classes** such as `Reservation`, `Ticket`, `Payment`, `FlightSchedule`, and `Passenger`

## DCD to code mapping
- `ReservationController` -> GRASP Controller
- `Reservation` -> manages reservation state
- `Ticket` with subclasses `FirstClassTicket`, `CoachTicket`, `EconomyTicket` -> polymorphism for fare/refund rules
- `Payment` -> payment record
- `FlightSchedule` -> flight information
- `Passenger` -> passenger details
- `FileDataStore` -> file persistence support

## Assumptions 
- File persistence is used instead of a relational database.
- Sample flights are seeded automatically on first run.
- The system supports one passenger per reservation.
- Reallocation of unconfirmed seats is not automated in this simplified project version.

## Initial test flow
1. Search flights using sample dates such as `2026-04-20`.
2. Book a reservation with a valid flight ID such as `F001`.
3. Copy the reservation ID shown in the booking result.
4. Confirm the reservation using the exact fare shown.
5. Cancel the reservation to test refund behavior.
