package in.virit.ff.bookingdtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservationDetails(
        String name,
        @NotNull VehicleType vehicleType,
        int passengerCount,
        @NotEmpty
        String comments
) {
    @JsonIgnore
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
