package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.In;

import static in.virit.ff.ferrymap.finnishterrainmap.LocalMaastokartta.ROAD_OVERALL_BREAKPOINT;


public class Tie2A2BFill extends BasicLine {

    public Tie2A2BFill() {
        super("tieviiva-numeroidut", "#BB271A");
        setId(getClass().getName());
        setFilter(new In("kohdeluokka", Kohdeluokka.AUTOTIE_IIA, Kohdeluokka.AUTOTIE_IIB));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_FILL_M);
        setMinZoom(ROAD_OVERALL_BREAKPOINT);
    }
}
