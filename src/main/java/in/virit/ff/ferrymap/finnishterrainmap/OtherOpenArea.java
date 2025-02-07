package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.RgbaColor;

public class OtherOpenArea extends BasicFill {
    public OtherOpenArea() {
        super("muuavoinalue", new RgbaColor(255,255,130, 1.0));

        // TODO e.g. 45° lines or different colors for vesijättö
        /**
         * Muu avoin alue	Maasto/2	Alue
         * Avoin metsämaa	Maasto/2	Alue	70	39110
         * Varvikko	Maasto/2	Alue	70	39120
         * Avoin vesijättö	Maasto/2	Alue	70	39130
         */
    }
}
