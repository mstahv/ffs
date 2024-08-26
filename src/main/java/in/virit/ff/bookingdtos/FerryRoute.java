package in.virit.ff.bookingdtos;

import java.util.List;

public record FerryRoute(
    String id,
    String name
) {

    public static List<FerryRoute> routes() {
        return List.of(
            new FerryRoute("2807", "Parainen"),
            new FerryRoute("4219", "Velkua"),
            new FerryRoute("1088", "Nauvo"),
            new FerryRoute("183", "Utö"),
            new FerryRoute("239", "Kotka-Pyhtää")
        );
    }

}