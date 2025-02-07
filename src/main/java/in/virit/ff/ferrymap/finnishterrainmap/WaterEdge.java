package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.All;
import org.vaadin.addons.maplibre.dto.expressions.Any;
import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class WaterEdge extends AbstractMaastokuvionReuna {

    public static final String WATER_EDGE_COLOR = "#3C98CA";

    public WaterEdge() {
        // TODO use Rgba + add methods like darken() & derive from water area color
        super(WATER_EDGE_COLOR);
        setId("veden-reuna");
        // TODO kohdeluokka is now string (should be int) for some reason, fix the tippecanoe build
        setFilter(new All(
                new Equals("kohdeluokka", 30211),
                new Any(
                        // TODO Move the MTK classes
                        new Equals("kartografinenluokka", 36211),// meri
                        new Equals("kartografinenluokka", 36200), // järvi
                        new Equals("kartografinenluokka", 36313)
                )
        ));

    }

}
