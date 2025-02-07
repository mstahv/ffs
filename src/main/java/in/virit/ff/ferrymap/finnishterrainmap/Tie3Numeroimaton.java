package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.In;

import static in.virit.ff.ferrymap.finnishterrainmap.LocalMaastokartta.ROAD_OVERALL_BREAKPOINT;


public class Tie3Numeroimaton extends BasicLine {

    public Tie3Numeroimaton() {
        super("tieviiva-muut", "#BB271A");
        setId(getClass().getName());
        setFilter(new In("kohdeluokka", Kohdeluokka.AUTOTIE_IIIA, Kohdeluokka.AUTOTIE_IIIB));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_FILL_S);
        setMinZoom(ROAD_OVERALL_BREAKPOINT);
    }
}
