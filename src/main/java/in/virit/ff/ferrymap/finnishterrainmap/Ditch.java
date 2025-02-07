package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.CaseExpression;
import org.vaadin.addons.maplibre.dto.expressions.PropertyEquals;

public class Ditch extends BasicLine {


    public Ditch() {
        super("virtavesikapea", "#0099FF");

        // TODO interolate on zoom level like contours ??
        getPaint().setLineWidth(new CaseExpression(
                1,
                new PropertyEquals("kohdeluokka", 36312, 2)
        ));
        setMinZoom(12);
    }

}
