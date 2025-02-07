package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.RgbaColor;
import org.vaadin.addons.maplibre.dto.expressions.Any;
import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class Ajotie extends BasicLine {

    public Ajotie() {
        super("tieviiva-muut", new RgbaColor(14, 14, 14, 1));
        setId("ajotie");

        setFilter(new Any(
                new Equals("kohdeluokka", Kohdeluokka.AJOTIE),
                new Equals("kohdeluokka", Kohdeluokka.AUTOTIE_IIA),
                new Equals("kohdeluokka", Kohdeluokka.AUTOTIE_IIB),
                new Equals("kohdeluokka", Kohdeluokka.AUTOTIE_IIIA),
                new Equals("kohdeluokka", Kohdeluokka.AUTOTIE_IIIB)
        ));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_WIDTH_S);
        setMinZoom(LocalMaastokartta.ROAD_OVERALL_BREAKPOINT);
    }
}
