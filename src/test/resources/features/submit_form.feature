# language: en
Feature: Booking management in BookingYa

  @CreateBooking
  Scenario: Successfully create a room reservation
    Given the user wants to make a reservation for a guest
    And the room has sufficient capacity
    When they submit the reservation request with valid dates
    Then the system should successfully confirm the reservation

  @UpdateBooking
  Scenario: Successfully update an existing reservation
    Given there is a previously registered reservation
    When the user updates the reservation details
    Then the system should save the changes correctly

  @DeleteBooking
  Scenario: Successfully cancel a reservation
    Given the user has an active reservation
    When they request to delete the reservation
    Then the system should process the cancellation and remove it from active listings

  @GetById
  Scenario: Obtain a reservation by its unique identifier
    Given the system has stored reservations
    When the user searches for a reservation by its ID
    Then the system should return the exact data for that reservation

  @GetAll
  Scenario: Consult all reservations in the system
    Given multiple reservations exist in the system
    When the user requests the general list
    Then the system should return all registered reservations

  @GetByUser
  Scenario: Consult reservations linked to a specific user
    Given a guest has made several reservations
    When the user consults the reservations by the guest identifier
    Then the system should return only the reservations associated with that user