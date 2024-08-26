package in.virit.ff.bookingdtos;

import java.time.LocalTime;

public record Tour(LocalTime start, String vesselId, String vessel, String startHarbour) {

    @Override
    public String toString() {
        return start + " from " + startHarbour + "," + vessel;
    }
}
