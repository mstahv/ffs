package in.virit.ff.bookingdtos;

import java.time.LocalTime;

public record Tour(LocalTime start,
                   String vesselId,
                   String vessel,
                   String startHarbour,
                   String route,
                   String departureTimeHarbour,
                   boolean departureTimeIsEstimate,
                   String tourStartTime
) {

    @Override
    public String toString() {
        return start + " from " + startHarbour + ", " + vessel;
    }
}
