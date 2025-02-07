package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.In;

import static in.virit.ff.ferrymap.finnishterrainmap.LocalMaastokartta.ROAD_OVERALL_BREAKPOINT;


public class Tie2A2BReuna extends BasicLine {

    public Tie2A2BReuna() {
        super("tieviiva-numeroidut", "#000000");
        setId(getClass().getName());
        setFilter(new In("kohdeluokka",Kohdeluokka.AUTOTIE_IIA, Kohdeluokka.AUTOTIE_IIB));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_EDGE_M);
        setMinZoom(ROAD_OVERALL_BREAKPOINT);
    }
}
