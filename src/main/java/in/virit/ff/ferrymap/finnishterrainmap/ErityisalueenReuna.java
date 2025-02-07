package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.In;
import org.vaadin.addons.maplibre.dto.expressions.Interpolate;
import org.vaadin.addons.maplibre.dto.expressions.ZoomStep;

public class ErityisalueenReuna extends BasicLine {
    public ErityisalueenReuna() {
        super("maastokuvionreuna", "#000000");
        getPaint().setLineWidth(Interpolate.linear().zoom(new ZoomStep(10, 0.05), new ZoomStep(16, 2.0)));
        setId("erityisalueenreuna");
        setFilter(new In("kartografinenluokka",

                32200, // hautausmaa
                32300, // kaatopaikka
                32411, // lentokentän päällystetty kiitotie
                32412, // lentokentän päällystämätön kiitotie
                32413, //
                32414,
                32415,
                32416,
                32417,
                32418, // .. lentokentttäalueita
                33100 // urheilujavirkisitysalue
        ));
        setMinZoom(12);

    }
}
