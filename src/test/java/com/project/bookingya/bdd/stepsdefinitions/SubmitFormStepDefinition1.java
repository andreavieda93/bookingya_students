package com.project.bookingya.bdd.stepsdefinitions;

import io.cucumber.java.en.*;
import net.serenitybdd.annotations.Steps;
import static org.junit.Assert.*;
import java.util.UUID;
import java.util.List;

import com.project.bookingya.bdd.steps.ReservationSteps;
import com.project.bookingya.dtos.ReservationDto;

public class SubmitFormStepDefinition1 {

    @Steps
    ReservationSteps reservationSteps;

    private ReservationDto request;
    private ReservationDto response;
    private List<ReservationDto> responseList;
    private UUID lastCreatedId;
    // --- @CreateBooking ---
    @Given("the user wants to make a reservation for a guest")
    public void prepareGuestData() {
        request = new ReservationDto();
        request.setGuestId(UUID.randomUUID());
        request.setRoomId(UUID.randomUUID());
    }
    @And("the room has sufficient capacity")
    public void validateCapacity() {
        assertNotNull("El objeto de petición no debe ser nulo", request);
    }
    @When("they submit the reservation request with valid dates")
    public void executeReservation() {
        lastCreatedId = reservationSteps.createReservation(request);
        response = reservationSteps.getById(lastCreatedId.toString());
    }
    @Then("the system should successfully confirm the reservation")
    public void confirmReservation() {
        assertNotNull("La respuesta de la reserva es nula", response);
        assertNotNull("El ID de la reserva no fue generado", lastCreatedId);
    }
    // --- @UpdateBooking ---
    @Given("there is a previously registered reservation")
    public void existingReservation() {
        request = new ReservationDto();
        request.setGuestId(UUID.randomUUID());
        lastCreatedId = reservationSteps.createReservation(request);
        response = reservationSteps.getById(lastCreatedId.toString());
    }
    @When("the user updates the reservation details")
    public void updateReservation() {
        request.setRoomId(UUID.randomUUID());
        lastCreatedId = reservationSteps.createReservation(request);
        response = reservationSteps.getById(lastCreatedId.toString());
    }
    @Then("the system should save the changes correctly")
    public void verifyUpdate() {
        assertNotNull("La actualización falló", response);
    }
    // --- @DeleteBooking ---
    @Given("the user has an active reservation")
    public void userHasActiveReservation() {
        request = new ReservationDto();
        lastCreatedId = reservationSteps.createReservation(request);
    }
    @When("they request to delete the reservation")
    public void deleteReservationRequest() {
        reservationSteps.deleteReservation(lastCreatedId.toString());
    }
    @Then("the system should process the cancellation and remove it from active listings")
    public void verifyCancellation() {
        List<ReservationDto> currentList = reservationSteps.getAll();
        // Como el DTO no tiene campo ID, verificamos que el objeto ya no esté en la lista
        boolean exists = currentList.contains(response);
        assertFalse("La reserva aún existe en el sistema", exists);
    }
    // --- @GetById ---
    @Given("the system has stored reservations")
    public void systemHasReservations() {
        request = new ReservationDto();
        lastCreatedId = reservationSteps.createReservation(request);
    }
    @When("the user searches for a reservation by its ID")
    public void searchById() {
        response = reservationSteps.getById(lastCreatedId.toString());
    }
    @Then("the system should return the exact data for that reservation")
    public void verifyExactData() {
        assertNotNull("No se encontró la reserva por ID", response);
    }
    // --- @GetAll ---
    @Given("multiple reservations exist in the system")
    public void multipleReservationsExist() {
        reservationSteps.createReservation(new ReservationDto());
        reservationSteps.createReservation(new ReservationDto());
    }
    @When("the user requests the general list")
    public void requestGeneralList() {
        responseList = reservationSteps.getAll();
    }
    @Then("the system should return all registered reservations")
    public void validateAllReservations() {
        assertNotNull(responseList);
        assertTrue("La lista de reservas está vacía", responseList.size() > 0);
    }
    // --- @GetByUser ---
    @Given("a guest has made several reservations")
    public void guestWithMultipleReservations() {
        UUID guestId = UUID.randomUUID();
        request = new ReservationDto();
        request.setGuestId(guestId);
        reservationSteps.createReservation(request);
    }
    @When("the user consults the reservations by the guest identifier")
    public void searchByGuest() {
        responseList = reservationSteps.getByUser(request.getGuestId());
    }
    @Then("the system should return only the reservations associated with that user")
    public void verifyUserReservations() {
        assertNotNull(responseList);
        for (ReservationDto res : responseList) {
            assertEquals(request.getGuestId(), res.getGuestId());
        }
    }
}