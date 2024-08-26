package in.virit.ff;

import in.virit.ff.bookingdtos.ReservationDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocalStorageSettings {

    private List<ReservationDetails> savedDetails = new ArrayList<>();
    private String lastRouteId = "2807";
    private int lastHarborFromId = 52; // Default: Granvik
    private int lastHarborToId = 57; // Heisala
    private int lastDetailsIndex;
    private String credentials;

    public List<ReservationDetails> getSavedDetails() {
        return savedDetails;
    }

    public void setSavedDetails(List<ReservationDetails> savedDetails) {
        this.savedDetails = savedDetails;
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
        this.lastDetailsIndex = savedDetails.indexOf(rd);
    }

    public Optional<ReservationDetails> getLastReservationDetails() {
        try {
            return Optional.of(savedDetails.get(lastDetailsIndex));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
