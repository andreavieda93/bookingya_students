package com.project.bookingya.services;
import com.project.bookingya.dtos.ReservationDto;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import com.project.bookingya.repositories.*;
import com.project.bookingya.entities.*;
import com.project.bookingya.models.Reservation;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReservationServiceTestTdd {

    @Mock
    private ModelMapper mapper;

    @Mock
    private IReservationRepository reservationRepository;

    @Mock
    private IRoomRepository roomRepository;

    @Mock
    private IGuestRepository guestRepository;

    @InjectMocks
    private ReservationService reservationService;

    private UUID idPrueba;
    private ReservationEntity entidadPrueba;
    private ReservationDto dtoPrueba;
    private Reservation modeloPrueba;

    @BeforeEach
    void setup() {
        idPrueba = UUID.randomUUID();
        entidadPrueba = new ReservationEntity();
        entidadPrueba.setId(idPrueba);
        dtoPrueba = new ReservationDto();
        dtoPrueba.setRoomId(UUID.randomUUID());
        dtoPrueba.setGuestsCount(3);
        dtoPrueba.setCheckIn(LocalDateTime.now().plusDays(2));
        dtoPrueba.setCheckOut(LocalDateTime.now().plusDays(6));

        modeloPrueba = new Reservation();
        modeloPrueba.setId(idPrueba);
    }

    @Test
    void buscarReservaPorId() {
        when(reservationRepository.findById(idPrueba)).thenReturn(Optional.of(entidadPrueba));
        when(mapper.map(any(ReservationEntity.class), eq(Reservation.class))).thenReturn(modeloPrueba);

        Reservation resultado = reservationService.getById(idPrueba);

        assertNotNull(resultado);
        assertEquals(idPrueba, resultado.getId());
    }

    @Test
    void crearReservaNuevaOk() {
        RoomEntity habitacion = new RoomEntity();
        habitacion.setAvailable(true);
        habitacion.setMaxGuests(5);

        when(roomRepository.findById(any())).thenReturn(Optional.of(habitacion));
        when(guestRepository.findById(any())).thenReturn(Optional.of(new GuestEntity()));

        // Mock del mapeo DTO -> Entity
        when(mapper.map(any(ReservationDto.class), eq(ReservationEntity.class))).thenReturn(entidadPrueba);
        when(reservationRepository.saveAndFlush(any())).thenReturn(entidadPrueba);

        // Mock del mapeo Entity -> Model
        when(mapper.map(any(ReservationEntity.class), eq(Reservation.class))).thenReturn(modeloPrueba);

        Reservation resultado = reservationService.create(dtoPrueba);

        assertNotNull(resultado);
        verify(reservationRepository).saveAndFlush(any());
    }

    @Test
    void actualizarReservaExistenteOk() {
        // Mock de la reserva
        when(reservationRepository.findById(idPrueba)).thenReturn(Optional.of(entidadPrueba));
        RoomEntity habitacionPrueba = new RoomEntity();
        habitacionPrueba.setAvailable(true);
        habitacionPrueba.setMaxGuests(10);
        when(roomRepository.findById(any())).thenReturn(Optional.of(habitacionPrueba));
        // Mock del cliente
        when(guestRepository.findById(any())).thenReturn(Optional.of(new GuestEntity()));

        // Mock del comportamiento del mapper
        doNothing().when(mapper).map(any(ReservationDto.class), any(ReservationEntity.class));
        when(reservationRepository.saveAndFlush(any(ReservationEntity.class))).thenReturn(entidadPrueba);
        when(mapper.map(any(ReservationEntity.class), eq(Reservation.class))).thenReturn(modeloPrueba);
        Reservation resultado = reservationService.update(dtoPrueba, idPrueba);
        assertNotNull(resultado);
        verify(reservationRepository).findById(idPrueba);
        verify(roomRepository).findById(any()); // Verificamos que validó la habitación
        verify(reservationRepository).saveAndFlush(any(ReservationEntity.class));
    }

    @Test
    void eliminarReservaSiExiste() {
        when(reservationRepository.findById(idPrueba)).thenReturn(Optional.of(entidadPrueba));
        reservationService.delete(idPrueba);
        verify(reservationRepository).delete(entidadPrueba);
    }
}