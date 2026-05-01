package com.project.bookingya.bdd.steps;

import com.project.bookingya.dtos.ReservationDto;
import net.serenitybdd.annotations.Step;
import java.util.*;

public class ReservationSteps {

    private Map<UUID, ReservationDto> repository = new HashMap<>();

    @Step
    public UUID createReservation(ReservationDto dto) {
        UUID newId = UUID.randomUUID();
        repository.put(newId, dto);
        return newId;
    }

    @Step
    public ReservationDto getById(String id) {
        return repository.get(UUID.fromString(id));
    }

    @Step
    public void deleteReservation(String id) {
        repository.remove(UUID.fromString(id));
    }

    @Step
    public List<ReservationDto> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Step
    public List<ReservationDto> getByUser(UUID guestId) {
        return repository.values().stream()
                .filter(r -> r.getGuestId() != null && r.getGuestId().equals(guestId))
                .toList();
    }
}