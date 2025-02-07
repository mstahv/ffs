package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.Expression;
import org.vaadin.addons.maplibre.dto.expressions.Interpolate;
import org.vaadin.addons.maplibre.dto.expressions.ZoomStep;

public class RoadWidthConstants {

    public static final Expression ROAD_WIDTH_XS = Interpolate.exponential(1.55).zoom(
            new ZoomStep(6, 0.5),
            new ZoomStep(20, 16)
    );

    public static final Expression ROAD_WIDTH_S = Interpolate.exponential(1.55).zoom(
            new ZoomStep(6, 0.7),
            new ZoomStep(20, 20)
    );

    // TODO tune these values. These are just copied from NLS demo, but probably are there just some trials and errors...
    public static final Expression ROAD_EDGE_L = Interpolate.exponential(1.55).zoom(
            new ZoomStep(6, 6),
            new ZoomStep(20, 74)
    );
    public static final Expression ROAD_FILL_L = Interpolate.exponential(1.55).zoom(
            new ZoomStep(3, 4),
            new ZoomStep(20, 70)
    );

    public static final Expression ROAD_EDGE_M = Interpolate.exponential(1.55).zoom(
            new ZoomStep(6, 4),
            new ZoomStep(20, 53)
    );

    public static final Expression ROAD_FILL_M = Interpolate.exponential(1.55).zoom(
            new ZoomStep(3, 2.5),
            new ZoomStep(20, 50)
    );

    public static final Expression ROAD_EDGE_S = Interpolate.exponential(1.55).zoom(
            new ZoomStep(6, 3),
            new ZoomStep(20, 30)
    );


    public static final Expression ROAD_FILL_S = Interpolate.exponential(1.55).zoom(
            new ZoomStep(6, 1.5),
            new ZoomStep(20, 30)
    );


    public static final Expression ROAD_OVERALL = Interpolate.linear().zoom(
            new ZoomStep(2, 1),
            new ZoomStep(13, 14)
    );


}
