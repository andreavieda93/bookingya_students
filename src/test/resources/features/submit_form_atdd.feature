# language: es
Característica: Gestión de Reservas de Hotel

  Escenario: Creación exitosa de una nueva reserva
    Dado que el huésped accede a la página de reservas
    Cuando completa el formulario con la habitación "1033" para "2" personas
    Y confirma la operación de reserva
    Entonces el sistema debe mostrar el mensaje de confirmación "Reservation confirmed"
    Y la reserva debe figurar en el listado histórico del usuario

  @ConsultaReserva
  Escenario: Consulta detallada de una reserva existente
    Dado que existe una reserva previa en el sistema
    Cuando el usuario solicita los detalles de la reserva por su ID
    Entonces el sistema debe retornar la información correcta de la habitación y el huésped

  @Negativo @Disponibilidad
  Escenario: Intento de reserva en habitación ya ocupada
    Dado que ya existe una reserva confirmada para la habitación "103" en fechas específicas
    Cuando otro huésped intenta reservar la misma habitación "103" para las mismas fechas
    Entonces el sistema no debe permitir la creación de la segunda reserva