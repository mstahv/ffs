package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.CaseExpression;
import org.vaadin.addons.maplibre.dto.expressions.PropertyEquals;

public class Maatalousmaa extends BasicFill {
    public Maatalousmaa() {
        super("maatalousmaa", "#FFD980");
        getPaint().setFillColor(new CaseExpression(
                "#FFD980",
                new PropertyEquals("kohdeluokka", 32612, "#bbd897"), // puutarha
                new PropertyEquals("kohdeluokka", 32800, "#FDF27C") // niitty
        ));
    }
}
