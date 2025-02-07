package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.LinePaint;
import org.vaadin.addons.maplibre.dto.Color;
import org.vaadin.addons.maplibre.dto.LineLayerDefinition;
import org.vaadin.addons.maplibre.dto.RawColor;

public class BasicLine extends LineLayerDefinition {

    public BasicLine(String layer, String hexColor) {
        this(layer, new RawColor(hexColor));
    }

    public BasicLine(String layer, Color color) {
        super(layer);
        setSource("mtk");
        setSourceLayer(layer);
        setPaint(new LinePaint(color));
    }
}
