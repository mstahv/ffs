package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.RgbaColor;
import org.vaadin.addons.maplibre.dto.expressions.Any;
import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class Ajopolku extends BasicLine {

    public Ajopolku() {
        super("tieviiva-muut", new RgbaColor(14, 14, 14, 1));
        setId("ajopolku");

        setFilter(new Any(
                new Equals("kohdeluokka", 12316),
                new Equals("kohdeluokka", 12313)
        ));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_WIDTH_S);
        getPaint().setLineDasharray(8, 2);
        setMinZoom(12);
    }
}
