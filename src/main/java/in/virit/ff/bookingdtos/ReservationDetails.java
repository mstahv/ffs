package in.virit.ff.bookingdtos;

import jakarta.validation.constraints.NotNull;

public record ReservationDetails(
        String name,
        @NotNull VehicleType vehicleType,
        int passengerCount,
        String comments
) {
}
