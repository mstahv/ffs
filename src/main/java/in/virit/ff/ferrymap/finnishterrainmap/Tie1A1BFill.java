package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.In;

import static in.virit.ff.ferrymap.finnishterrainmap.LocalMaastokartta.ROAD_OVERALL_BREAKPOINT;


public class Tie1A1BFill extends BasicLine {

    public Tie1A1BFill() {
        super("tieviiva-numeroidut", "#BB271A");
        setId(getClass().getName());
        setFilter(new In("kohdeluokka",  Kohdeluokka.AUTOTIE_IA, Kohdeluokka.AUTOTIE_IB));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_FILL_L);
        setMinZoom(ROAD_OVERALL_BREAKPOINT);
    }
}
