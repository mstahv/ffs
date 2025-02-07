package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.FillPaint;
import org.vaadin.addons.maplibre.dto.Color;
import org.vaadin.addons.maplibre.dto.FillLayerDefinition;
import org.vaadin.addons.maplibre.dto.RawColor;

public class BasicFill extends FillLayerDefinition {

    public BasicFill(String layer, String hexColor) {
        this(layer, new RawColor(hexColor));
    }

    public BasicFill(String layer, Color color) {
        super(layer);
        setSource("mtk");
        setSourceLayer(layer);
        setPaint(new FillPaint(color));
    }
}
