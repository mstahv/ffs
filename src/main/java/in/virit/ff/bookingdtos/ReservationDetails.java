package in.virit.ff.bookingdtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ReservationDetails(
        String name,
        @NotNull VehicleType vehicleType,
        int passengerCount,
        @NotEmpty
        String comments
) {
    public boolean isValid() {
        if (comments == null || comments.isBlank()) {
            return false;
        }
        if(vehicleType == null) {
            return false;
        }
        return true;
    }
}
