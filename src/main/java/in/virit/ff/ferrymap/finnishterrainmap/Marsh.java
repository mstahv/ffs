package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.Any;
import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class Marsh extends BasicFill {
    public Marsh() {
        super("suo", "#BEECEC");
        setFilter(new Any(
                new Equals("kohdeluokka", 35412),
                new Equals("kohdeluokka", 3522)
        ));
        // separate style layer for open
        // TODO difficult, vertical lines ??
        /*
        Suo, helppokulkuinen puuton 	Maasto/1	Alue	64	35411
        Suo, helppokulkuinen metsää kasvava 	Maasto/1	Alue	64	35412
        Suo, vaikeakulkuinen puuton 	Maasto/1	Alue	64	35421
        Suo, vaikeakulkuinen metsää kasvava 	Maasto/1	Alue	64	35422
         */

    }
}
