package in.virit.ff.ferrymap;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.time.Instant;

public record VesselData(
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    Instant time,
    double sog,
    double cog,
    int navStat,
    int rot,
    boolean posAcc,
    boolean raim,
    int heading,
    double lon,
    double lat
) {

    static GeometryFactory gf = new GeometryFactory();

    public Point point() {
        return gf.createPoint(new Coordinate(lon, lat));
    }

    public Coordinate coordinate() {
        return new Coordinate(lon, lat);
    }

    public NavigationStatus status() {
        return NavigationStatus.fromCode(navStat);
    }
}