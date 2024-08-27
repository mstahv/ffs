package in.virit.ff;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.dependency.JsModule;
import in.virit.ff.bookingdtos.ReservationDetails;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocalStorageSettings {

    private Map<String,ReservationDetails> savedDetails = new LinkedHashMap<>();
    private String lastRouteId = "2807";
    private int lastHarborFromId = 52; // Default: Granvik
    private int lastHarborToId = 57; // Heisala
    private String lastDetails;
    private String credentials;

    public Map<String,ReservationDetails> getSavedDetails() {
        return savedDetails;
    }

    public String getLastRouteId() {
        return lastRouteId;
    }

    public void setLastRouteId(String lastRouteId) {
        this.lastRouteId = lastRouteId;
    }

    public int getLastHarborFromId() {
        return lastHarborFromId;
    }

    public void setLastHarborFromId(int lastHarborFromId) {
        this.lastHarborFromId = lastHarborFromId;
    }

    public int getLastHarborToId() {
        return lastHarborToId;
    }

    public void setLastHarborToId(Integer lastHarborToId) {
        this.lastHarborToId = lastHarborToId;
    }

    public void setLastReservationDetails(ReservationDetails rd) {
        this.lastDetails = rd.name();
    }

    @JsonIgnore
    public Optional<ReservationDetails> getLastReservationDetails() {
        return Optional.ofNullable(savedDetails.get(lastDetails));
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
