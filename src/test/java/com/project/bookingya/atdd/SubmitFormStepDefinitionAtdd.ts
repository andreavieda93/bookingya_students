import { Given, When, Then, Before } from '@cucumber/cucumber';
import { expect, request } from '@playwright/test';

let apiContext: any;
let guestId: string;
let roomId: string;
let reservationResponse: any;
let consultaResponse: any;
let secondResResponse: any;
let reservaExistenteId: string;

const uniqueId = () => Date.now();

Before(async function () {
    apiContext = await request.newContext({
        baseURL: 'http://localhost:8080'
    });
});

// --- ESCENARIO 1: CREACIÓN EXITOSA ---

Given('que el huésped accede a la página de reservas', async function () {
    const id = uniqueId();
    const guestRes = await apiContext.post('/api/guest', {
        data: { name: "Ximena ATDD", email: `atdd_${id}@test.com`, identification: `ID-${id}` }
    });
    const guestData = await guestRes.json();
    guestId = guestData.id;

    const roomRes = await apiContext.post('/api/room', {
        data: {
            roomNumber: `ROOM-${id}`, name: "Suite Ejecutiva", code: `C-${id}`,
            city: "Cali", maxGuests: 4, nightlyPrice: 200.0, available: true, type: "PREMIUM"
        }
    });
    const roomData = await roomRes.json();
    roomId = roomData.id;
});

When('completa el formulario con la habitación {string} para {string} personas', async function (room, guests) {
    const body = {
        guestId: guestId,
        roomId: roomId,
        checkIn: "2026-06-01T14:00:00.000Z",
        checkOut: "2026-06-05T11:00:00.000Z",
        guestsCount: parseInt(guests),
        notes: "Reserva Exitosa ATDD"
    };
    reservationResponse = await apiContext.post('/api/reservation', { data: body });
});

When('confirma la operación de reserva', async function () {
    expect(reservationResponse.ok()).toBeTruthy();
});

Then('el sistema debe mostrar el mensaje de confirmación {string}', async function (mensaje) {
    const status = reservationResponse.status();
    expect(status === 200 || status === 201).toBeTruthy();
});

Then('la reserva debe figurar en el listado histórico del usuario', async function () {
    const history = await apiContext.get(`/api/reservation/guest/${guestId}`);
    expect(history.ok()).toBeTruthy();
});

// --- ESCENARIO 2: CONSULTA POR ID ---

Given('que existe una reserva previa en el sistema', async function () {
    const id = uniqueId();
    const gRes = await apiContext.post('/api/guest', { data: { name: "Huesped Busca", email: `b_${id}@t.com`, identification: `IDB-${id}` } });
    const rRes = await apiContext.post('/api/room', { data: { roomNumber: `RB-${id}`, name: "Hab simple", code: `CB-${id}`, city: "Cali", maxGuests: 2, nightlyPrice: 50, available: true, type: "STANDARD" } });
    const gData = await gRes.json();
    const rData = await rRes.json();

    const res = await apiContext.post('/api/reservation', {
        data: { guestId: gData.id, roomId: rData.id, checkIn: "2026-07-01T14:00:00.000Z", checkOut: "2026-07-05T11:00:00.000Z", guestsCount: 1 }
    });
    const data = await res.json();
    reservaExistenteId = data.id;
});

When('el usuario solicita los detalles de la reserva por su ID', async function () {
    const res = await apiContext.get(`/api/reservation/${reservaExistenteId}`);
    consultaResponse = await res.json();
});

Then('el sistema debe retornar la información correcta de la habitación y el huésped', async function () {
    expect(consultaResponse.id).toBe(reservaExistenteId);
});

// --- ESCENARIO 3: NEGATIVO (DISPONIBILIDAD) ---

Given('que ya existe una reserva confirmada para la habitación {string} en fechas específicas', async function (roomNumber) {
    const id = uniqueId();
    const gRes = await apiContext.post('/api/guest', { data: { name: "Huesped Uno", email: `h1_${id}@t.com`, identification: `ID1-${id}` } });
    const rRes = await apiContext.post('/api/room', { data: { roomNumber: `ROCC-${id}`, name: "Ocupada", code: `COCC-${id}`, city: "Bogota", maxGuests: 2, nightlyPrice: 100, available: true, type: "STANDARD" } });

    const gData = await gRes.json();
    const rData = await rRes.json();
    roomId = rData.id; // Guardamos para el siguiente paso

    await apiContext.post('/api/reservation', {
        data: { guestId: gData.id, roomId: roomId, checkIn: "2026-12-24T14:00:00.000Z", checkOut: "2026-12-26T11:00:00.000Z", guestsCount: 1 }
    });
});

When('otro huésped intenta reservar la misma habitación {string} para las mismas fechas', async function (roomNumber) {
    const id = uniqueId();
    const g2Res = await apiContext.post('/api/guest', { data: { name: "Huesped Intruso", email: `hi_${id}@t.com`, identification: `IDI-${id}` } });
    const g2Data = await g2Res.json();

    secondResResponse = await apiContext.post('/api/reservation', {
        data: { guestId: g2Data.id, roomId: roomId, checkIn: "2026-12-24T14:00:00.000Z", checkOut: "2026-12-26T11:00:00.000Z", guestsCount: 1 }
    });
});

Then('el sistema no debe permitir la creación de la segunda reserva', async function () {
    // Si el sistema funciona bien, secondResResponse NO debe ser 200 o 201
    const status = secondResResponse.status();
    expect(status).not.toBe(201);
    expect(status).not.toBe(200);
});