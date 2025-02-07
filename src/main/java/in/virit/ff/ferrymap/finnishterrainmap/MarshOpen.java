package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.Any;
import org.vaadin.addons.maplibre.dto.expressions.CaseExpression;
import org.vaadin.addons.maplibre.dto.expressions.Equals;
import org.vaadin.addons.maplibre.dto.expressions.PropertyEquals;

public class MarshOpen extends BasicFill {
    public MarshOpen() {
        super("suo", "#D1CC41");
        setId("avosuo");
        setFilter(new Any(
                new Equals("kohdeluokka", 35411),
                new Equals("kohdeluokka", 35421)
        ));
        // #A9D785 for 35421 (hard to walk). TODO replace special color with blue horizontal lines
        getPaint().setFillColor(new CaseExpression(
                "#D1CC41",
                new PropertyEquals("kohdeluokka", 35421, "#A9D785")
        ));
    }
}
