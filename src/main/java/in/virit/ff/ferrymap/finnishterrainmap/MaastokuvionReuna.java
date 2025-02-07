package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.All;
import org.vaadin.addons.maplibre.dto.expressions.Any;
import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class MaastokuvionReuna extends AbstractMaastokuvionReuna {
    public MaastokuvionReuna() {
        // TODO use Rgba + add methods like darken() & derive from water area color
        super("#000000");
        setId("maastokuvionreuna");
        getPaint().setLineWidth(0.7);
        // TODO kohdeluokka is now string (should be int) for some reason, fix the tippecanoe build
        setFilter(new All(
                new Equals("kohdeluokka", Kohdeluokka.MAASTO_YKSIKASITTEINEN_REUNAVIIVA),
                new Any(
                        new Equals("kartografinenluokka", Kohdeluokka.PELTO),
                        new Equals("kartografinenluokka", Kohdeluokka.PUUTARHA)
                )
        ));

    }

}
