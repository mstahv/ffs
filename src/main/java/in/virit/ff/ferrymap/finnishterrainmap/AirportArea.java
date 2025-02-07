package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.CaseExpression;
import org.vaadin.addons.maplibre.dto.expressions.PropertyEquals;

public class AirportArea extends BasicFill {
    public AirportArea() {
        super("lentokenttaalue", "#FFD980");

        getPaint().setFillColor(new CaseExpression(
                "#D1CC41",
                // kiitotie
                PropertyEquals.anyValue("kohdeluokka", "#F26161", 32411, 32412),
                // nurmi yms
                PropertyEquals.anyValue("kohdeluokka", "#FFFF8C", 32413, 32415, 32416, 32441, 32442),
                // rullaustiet
                PropertyEquals.anyValue("kohdeluokka", "#FFE680", 32414, 32417, 32418)
        ));

    }
}
