package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.SymbolLayerDefinition;
import org.vaadin.addons.maplibre.dto.SymbolLayout;
import org.vaadin.addons.maplibre.dto.SymbolPaint;

public class Vesikuoppa extends SymbolLayerDefinition {

    public Vesikuoppa() {
        setId("vesikuoppa");
        setSource("mtk");
        setSourceLayer("vesikuoppa");
        setPaint(new SymbolPaint() {{
            setTextColor(WaterEdge.WATER_EDGE_COLOR);
        }});

        setLayout(new SymbolLayout(){{
            setTextField("V");
            setTextFont("Open Sans Semibold");
            setTextAllowOverlap(true);
        }});
    }
}
