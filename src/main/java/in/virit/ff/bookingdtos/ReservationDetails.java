package in.virit.ff.bookingdtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReservationDetails(
        String name,
        @NotNull VehicleType vehicleType,
        int passengerCount,
        String comments,
        @NotEmpty
        String licensePlate
) {
    @JsonIgnore
    public boolean isValid() {
        if (licensePlate == null || licensePlate.isBlank()) {
            return false;
        }
        if(vehicleType == null) {
            return false;
        }
        return true;
    }
}
