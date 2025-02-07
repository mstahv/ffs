package in.virit.ff.ferrymap.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.SymbolLayerDefinition;
import org.vaadin.addons.maplibre.dto.SymbolLayout;
import org.vaadin.addons.maplibre.dto.SymbolPaint;
import org.vaadin.addons.maplibre.dto.expressions.Interpolate;

public class Kaislikko extends SymbolLayerDefinition {

    public Kaislikko() {
        setId("kaislikko");
        setSource("mtk");
        setSourceLayer("kaislikko");
        setPaint(new SymbolPaint(){{
            setTextColor("#666");
        }});
        setLayout(new SymbolLayout(){{
            setTextField("\\|/");
            setTextSize(Interpolate.linear().zoom(
                    new org.vaadin.addons.maplibre.dto.expressions.ZoomStep(11, 1),
                    new org.vaadin.addons.maplibre.dto.expressions.ZoomStep(20, 40)
            ));
        }});
    }
}
