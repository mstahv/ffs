package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.Expression;
import org.vaadin.addons.maplibre.dto.expressions.Interpolate;
import org.vaadin.addons.maplibre.dto.expressions.ZoomStep;

public class Sizes {

    public static final Expression MEDIUM_SCALING_ICON = Interpolate.exponential(1.55).zoom(
            new ZoomStep(1, 0.0001),
            new ZoomStep(15, 0.5)
    );

}
