package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.All;
import org.vaadin.addons.maplibre.dto.expressions.Any;
import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class UnclearWaterEdge extends AbstractMaastokuvionReuna {
    public UnclearWaterEdge() {
        // TODO use Rgba + add methods like darken() & derive from water area color
        super("#3C98CA");
        setId("veden-reuna-es");
        setFilter(new All(
                new Equals("kohdeluokka", Kohdeluokka.MAASTO_EPAMAARAINEN_REUNAVIIVA),
                new Any(
                        new Equals("kartografinenluokka", Kohdeluokka.MERIVESI),
                        new Equals("kartografinenluokka", Kohdeluokka.JARVIVESI),
                        new Equals("kartografinenluokka", Kohdeluokka.VIRTAVESIALUE)
                )
        ));
        getPaint().setLineDasharray(2,2);

        // TODO kartografinenluokka should be checked for water type

    }

}
