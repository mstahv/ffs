package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.In;

import static in.virit.ff.ferrymap.finnishterrainmap.LocalMaastokartta.ROAD_OVERALL_BREAKPOINT;


public class Tie2ReunaNumeroimaton extends BasicLine {

    public Tie2ReunaNumeroimaton() {
        super("tieviiva-muut", "#000000");
        setId(getClass().getName());
        setFilter(new In("kohdeluokka", Kohdeluokka.AUTOTIE_IIA, Kohdeluokka.AUTOTIE_IIB));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_EDGE_M);
        setMinZoom(ROAD_OVERALL_BREAKPOINT);
    }
}
