package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.NotEquals;

public class Vesikulkuvayla extends BasicLine {
    public Vesikulkuvayla() {
        super("vesikulkuvayla", "green");
        getPaint().setLineWidth(0.75);
        setMinZoom(13);
        setFilter(new NotEquals("kohdeluokka", 16521));
    }
}
