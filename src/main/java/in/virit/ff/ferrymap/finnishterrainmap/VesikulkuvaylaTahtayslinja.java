package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class VesikulkuvaylaTahtayslinja extends BasicLine {
    public VesikulkuvaylaTahtayslinja() {
        super("vesikulkuvayla", "green");
        setId("vkt");
        getPaint().setLineWidth(0.75);
        getPaint().setLineDasharray(4,2);
        setFilter(new Equals("kohdeluokka", 16521));
        setMinZoom(13);
    }
}
