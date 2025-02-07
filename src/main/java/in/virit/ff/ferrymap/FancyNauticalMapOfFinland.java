package in.virit.ff.ferrymap;

import com.vaadin.flow.router.Route;
import in.virit.ff.ferrymap.finnishterrainmap.Kohdeluokka;
import in.virit.ff.ferrymap.finnishterrainmap.LocalMaastokartta;
import org.vaadin.addons.maplibre.FillPaint;
import org.vaadin.addons.maplibre.LinePaint;
import org.vaadin.addons.maplibre.dto.CircleLayerDefinition;
import org.vaadin.addons.maplibre.dto.CirclePaint;
import org.vaadin.addons.maplibre.dto.FillLayerDefinition;
import org.vaadin.addons.maplibre.dto.LineLayerDefinition;
import org.vaadin.addons.maplibre.dto.RgbaColor;
import org.vaadin.addons.maplibre.dto.SymbolLayerDefinition;
import org.vaadin.addons.maplibre.dto.SymbolLayout;
import org.vaadin.addons.maplibre.dto.SymbolPaint;
import org.vaadin.addons.maplibre.dto.VectorMapSource;
import org.vaadin.addons.maplibre.dto.expressions.Any;
import org.vaadin.addons.maplibre.dto.expressions.CaseExpression;
import org.vaadin.addons.maplibre.dto.expressions.Equals;
import org.vaadin.addons.maplibre.dto.expressions.In;
import org.vaadin.addons.maplibre.dto.expressions.Interpolate;
import org.vaadin.addons.maplibre.dto.expressions.NotEquals;
import org.vaadin.addons.maplibre.dto.expressions.PropertyEquals;
import org.vaadin.addons.maplibre.dto.expressions.ZoomStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static in.virit.ff.ferrymap.FerryMap.MERIDATA;


@Route
public class FancyNauticalMapOfFinland extends LocalMaastokartta {

    static final List<String> eastMarks = Arrays.asList("k", "l", "Q");
    static final List<String> westMarks = Arrays.asList("j", "O", "i");
    static final List<String> northMarks = Arrays.asList("K", "f", "9");
    static final List<String> southMarks = Arrays.asList("A", "h", "M", "g", "N");
    static final List<String> allCardinalMarks = new ArrayList<>() {{
        addAll(eastMarks);
        addAll(westMarks);
        addAll(southMarks);
        addAll(northMarks);
    }};

    public FancyNauticalMapOfFinland() {

        addSource(MERIDATA, new VectorMapSource("https://fvtns.dokku1.parttio.org/services/meridata2/"));
        //addSource(MERIDATA, new VectorMapSource("http://localhost:8000/services/meridata2"));

        addSprite("topo", "https://virit.in/maastokartta/toposprite");

        addSourceLayer(new FillLayerDefinition() {{
            setId("shallow"); // This is optional, if not set, the ID is generated
            setSource(MERIDATA);
            setSourceLayer("DepthArea");
            // white with 20% opacity on top of the base maps blue color
            setPaint(new FillPaint("rgba(255, 255, 255, 0.2)"));
            // Show only on zoom level 11 and above
            setMinZoom(11);
            // DRVAL1 property in the feature is the depth range start in meters, -> 0 means shallow
            setFilter(new Equals("DRVAL1", 0));
            // Just to test APIs, this is the same as above (with one rule), could e.g. add more depth areas
            setFilter(new Any(new Equals("DRVAL1", 0)));
        }});

        addSourceLayer(new LineLayerDefinition() {{
            setSource(MERIDATA);
            setSourceLayer("DepthContour");
            setPaint(new LinePaint(new RgbaColor(109, 109, 184, 0.5), 1.0));
            setMinZoom(12);
        }});
        // Above is the same as below
            /*
            addLayer("""
                            {
                                "id": "dcontour",
                                "type": "line",
                                "source": "meridata",
                                "source-layer": "DepthContour",
                                "paint": {
                                    "line-color": "rgba(109, 109, 184, 0.5)"
                                },
                                "minzoom": 12
                            }
                    """);
        */

        // Render the number of the depth contour lines
        addSourceLayer(new SymbolLayerDefinition() {{
            setId("dcontour-label");
            setSource(MERIDATA);
            setSourceLayer("DepthContour");
            setMinZoom(12);
            setFilter(new Equals("$type", "LineString"));

            setLayout(new SymbolLayout() {{
                setSymbolPlacement(SymbolPlacement.lineCenter);
                setTextField("{VALDCO} ");
                setTextFont("Open Sans Semibold");
                setTextSize(Interpolate
                        .exponential(1.4)
                        .zoom(
                                new ZoomStep(10, 9),
                                new ZoomStep(20, 14)
                        )
                );
            }});

            setPaint(new SymbolPaint() {{
                setTextColor("rgba(100, 100, 200, 1)");
                setTextHaloColor("rgba(255, 255, 255, 0.43)");
                setTextHaloWidth(2);
            }});

        }});

        addSourceLayer(new SymbolLayerDefinition() {{
            setId("sounding-point");
            setSource(MERIDATA);
            setSourceLayer("Sounding");
            setMinZoom(12);
            setLayout(new SymbolLayout() {{
                setSymbolPlacement(SymbolPlacement.point);
                setTextField("{DEPTH}");
                setTextFont("Open Sans Semibold");
                setTextSize(Interpolate
                        .exponential(1.4)
                        .zoom(
                                new ZoomStep(10, 8),
                                new ZoomStep(20, 14)
                        )
                );
                setTextRotationAlignment(TextRotationAlignment.map);
                setTextTransform(TextTransform.uppercase);
            }});
            setPaint(new SymbolPaint() {{
                setTextColor("rgba(200, 100, 200, 0.7)");
            }});
        }});

        addSourceLayer(new FillLayerDefinition() {{
            setId("vaylaalueet");
            setSource(MERIDATA);
            setSourceLayer("vaylaalueet");
            setPaint(new FillPaint("rgba(117, 255, 57, 0.2)"));
            setMinZoom(9);
        }});

        addSourceLayer(new LineLayerDefinition() {{
            setId("vaylat");
            setSource(MERIDATA);
            setSourceLayer("vesikulkuvayla");
            setPaint(new LinePaint(new RgbaColor(0, 200, 0, 0.9)));
            setMinZoom(11);
            setFilter(new NotEquals("kohdeluokka", Kohdeluokka.LAIVAVAYLALINJA));
        }});
        addSourceLayer(new LineLayerDefinition() {{
            setId("navigointilinjat");
            setSource(MERIDATA);
            setSourceLayer("vesikulkuvayla");
            setPaint(new LinePaint(new RgbaColor(0, 200, 0, 0.9)) {{
                setLineDasharray(4, 4);
            }});
            setMinZoom(11);
            setFilter(new Equals("kohdeluokka", Kohdeluokka.LAIVAVAYLALINJA));
        }});

        if(false)
        addSourceLayer(new CircleLayerDefinition() {
            {
                setId("muut-merkit");
                setSource(MERIDATA);
                setSourceLayer("turvalaite");
                setMinZoom(13);
                // Filter out some symbols that are handled by some other layer
                var alreadyHandledSymbols = new ArrayList<>(LateralMarks.all);
                //alreadyHandledSymbols.addAll(allCardinalMarks);
                //setFilter(new All(NotEquals.anyValue("symboli", alreadyHandledSymbols)));

                setPaint(new CirclePaint() {{
                    setCircleStrokeColor("#000080");
                    setCircleStrokeWidth(0.5);
                    setCircleOpacity(1);
                    setCircleRadius(10.0);
                    setCircleColor("rgba(0, 0, 0, 0)");
                }});
            }
        });


        addSourceLayer(new WestMarks());
        addSourceLayer(new EastMarks());
        addSourceLayer(new NorthMarks());
        addSourceLayer(new SouthMarks());

        addSourceLayer(new SymbolLayerDefinition() {{
            setId("kummeli");
            setSource(MERIDATA);
            setSourceLayer("turvalaite");
            setFilter(new In("kohdeluokka", Kohdeluokka.KUMMELI));
            setLayout(new SymbolLayout() {{
                setIconSize(0.25);
                setIconImage("topo:SKum");
                setIconAllowOverlap(true);
                setMinZoom(11);
            }});
        }});

        addSourceLayer(new SymbolLayerDefinition() {{
            setId("linjamerkki");
            setSource(MERIDATA);
            setSourceLayer("turvalaite");
            setFilter(new In("kohdeluokka", Kohdeluokka.LINJAMERKKI, Kohdeluokka.MERIMERKKI_ERIKOIS)); // Erikois is mistake in MTK data?
            setLayout(new SymbolLayout() {{
                setIconSize(0.5);
                setIconImage("topo:SVeri");
                setIconAnchor(IconAnchor.bottom);
                setIconAllowOverlap(true);
                setMinZoom(12);
            }});
        }});

        addSourceLayer(new SymbolLayerDefinition() {{
            setId("linjaloisto");
            setSource(MERIDATA);
            setSourceLayer("turvalaite");
            setFilter(new In("kohdeluokka", Kohdeluokka.LINJALOISTO)); // Erikois is mistake in MTK data?
            setLayout(new SymbolLayout() {{
                setIconSize(0.25);
                setIconImage("topo:SLl");
                setIconAnchor(IconAnchor.bottom);
                setIconAllowOverlap(true);
                setMinZoom(12);
            }});
        }});

        addSourceLayer(new LateralMarks());

        // These come from "mml" source (maastotietokanta)
        addSourceLayer(new RockBelowSurface());
        addSourceLayer(new RockSurface());
        addSourceLayer(new RockAboveSurface());
    }

    public static class LateralMarks extends CircleLayerDefinition {
        public static List<Integer> reds = Arrays.asList(
                Kohdeluokka.VIITTAPOIJU_VASEN,
                Kohdeluokka.POIJU_VASEN,
                Kohdeluokka.MERIMERKKI_VASEN,
                Kohdeluokka.JAAPOIJU_VASEN,
                Kohdeluokka.VALOJAAPOIJU_VASEN,
                Kohdeluokka.VALOPOIJU_VASEN,
                Kohdeluokka.REUNAMERKKI_VASEN,
                Kohdeluokka.VALAISTU_REUNAMERKKI_VASEN);
        public static List<Integer> greens = Arrays.asList(
                Kohdeluokka.VIITTAPOIJU_OIKEA,
                Kohdeluokka.POIJU_OIKEA,
                Kohdeluokka.MERIMERKKI_OIKEA,
                Kohdeluokka.JAAPOIJU_OIKEA,
                Kohdeluokka.VALOJAAPOIJU_OIKEA,
                Kohdeluokka.VALOPOIJU_OIKEA,
                Kohdeluokka.REUNAMERKKI_OIKEA,
                Kohdeluokka.VALAISTU_REUNAMERKKI_OIKEA
        );
        public static List<Integer> all = new ArrayList<>() {{
            addAll(greens);
            addAll(reds);
        }};

        {
            setId("lateraalit");
            setSource(MERIDATA);
            setSourceLayer("turvalaite");
            setPaint(new CirclePaint() {{
                setCircleRadius(Interpolate
                        .exponential(1.55)
                        .zoom(
                                new ZoomStep(6, 0.5),
                                new ZoomStep(20, 40)
                        )
                );
                setCircleStrokeColor("#000080");
                setCircleStrokeWidth(1.5);
                setCircleOpacity(1);
                setCircleColor(
                        new CaseExpression(
                                // default value red
                                "rgba(255, 0, 0, 1)",
                                // greens
                                PropertyEquals.anyValue("kohdeluokka", "rgba(0, 255, 0, 1)", greens))
                );
            }});
            setFilter(new In("kohdeluokka", all));
            setMinZoom(12);
        }

    }

    private abstract class CardinalMark extends SymbolLayerDefinition {
        {
            setMinZoom(12);
            // TODO take from Väylävirasto (MERIDATA) instead of MML, need more fields there thought
            setSource(MERIDATA);
            setSourceLayer("turvalaite");
            setLayout(new SymbolLayout() {{
                setIconSize(0.75);
                setIconImage(symbolName());
                setIconAllowOverlap(true);
            }});
        }

        abstract String symbolName();
    }

    private class EastMarks extends CardinalMark {
        {
            setId("kardinaalit-itä");
            setFilter(new In("kohdeluokka", Kohdeluokka.VALOPOIJU_ITA, Kohdeluokka.POIJU_ITA, Kohdeluokka.MERIMERKKI_ITA, Kohdeluokka.VIITTAPOIJU_ITA,
                    Kohdeluokka.JAAPOIJU_ITA,
                    Kohdeluokka.REUNAMERKKI_ITA,
                    Kohdeluokka.VALAISTU_REUNAMERKKI_ITA,
                    Kohdeluokka.VALOJAAPOIJU_ITA
            ));
        }

        @Override
        String symbolName() {
            return "meri:beacon_byb";
        }
    }

    private class WestMarks extends CardinalMark {
        {
            setId("kardinaalit-länsi");
            setFilter(new In("kohdeluokka", Kohdeluokka.VALOPOIJU_LANSI, Kohdeluokka.POIJU_LANSI, Kohdeluokka.MERIMERKKI_LANSI, Kohdeluokka.VIITTAPOIJU_LANSI,
                    Kohdeluokka.JAAPOIJU_LANSI,
                    Kohdeluokka.REUNAMERKKI_LANSI,
                    Kohdeluokka.VALAISTU_REUNAMERKKI_LANSI,
                    Kohdeluokka.VALOJAAPOIJU_LANSI
            ));
        }

        @Override
        String symbolName() {
            return "meri:beacon_yby";
        }
    }

    private class NorthMarks extends CardinalMark {
        {
            setId("kardinaalit-pohjoinen");
            setFilter(new In("kohdeluokka",
                    Kohdeluokka.VALOPOIJU_POHJOIS,
                    Kohdeluokka.POIJU_POHJOIS,
                    Kohdeluokka.MERIMERKKI_POHJOIS,
                    Kohdeluokka.VIITTAPOIJU_POHJOIS,
                    Kohdeluokka.JAAPOIJU_POHJOIS,
                    Kohdeluokka.REUNAMERKKI_POHJOIS,
                    Kohdeluokka.VALAISTU_REUNAMERKKI_POHJOIS,
                    Kohdeluokka.VALOJAAPOIJU_POHJOIS
            ));
        }

        @Override
        String symbolName() {
            return "meri:beacon_by";
        }
    }

    private class SouthMarks extends CardinalMark {
        {
            setId("kardinaalit-etelä");
            setFilter(new In("kohdeluokka",
                    Kohdeluokka.VALOPOIJU_ETELA,
                    Kohdeluokka.POIJU_ETELA,
                    Kohdeluokka.MERIMERKKI_ETELA,
                    Kohdeluokka.VIITTAPOIJU_ETELA,
                    Kohdeluokka.JAAPOIJU_ETELA,
                    Kohdeluokka.REUNAMERKKI_ETELA,
                    Kohdeluokka.VALAISTU_REUNAMERKKI_ETELA,
                    Kohdeluokka.VALOJAAPOIJU_ETELA

            ));
        }

        @Override
        String symbolName() {
            return "meri:beacon_yb";
        }
    }

    private abstract class Rock extends SymbolLayerDefinition {
        {
            setSource(MERIDATA);
            setSourceLayer("vesikivi");
            setLayout(new SymbolLayout() {
                {
                    setIconSize(0.125);
                    setIconImage(symbolName());
                    setIconRotate(iconRotate());
                    setIconAllowOverlap(true);
                }

            });
            setFilter(new Equals("kohdeluokka", kohdeluokka()));
            setMinZoom(12);
            setMaxZoom(20);
        }

        int iconRotate() {
            return 0;
        }

        abstract int kohdeluokka();

        abstract String symbolName();
    }

                    /*
                {
      "id": "maasto_poi",
      "type": "circle",
      "source": "taustakartta",
      "source-layer": "maasto_piste",
      "filter": [
        "match",
        ["get", "kohdeluokka"],
        [34600, 72310, 38511, ],
        true,
        false
      ],
      "paint": {"circle-color": "#8d7469", "circle-radius": 3}
    }
             */

    private class RockBelowSurface extends Rock {
        {
        }

        @Override
        int kohdeluokka() {
            return 38511;
        }

        @Override
        String symbolName() {
            return "meri:rock-normal";
        }
    }

    private class RockSurface extends Rock {

        @Override
        int kohdeluokka() {
            return 38512;
        }

        @Override
        String symbolName() {
            return "meri:rock-wl";
        }
    }

    private class RockAboveSurface extends Rock {

        @Override
        int kohdeluokka() {
            return 38513;
        }

        @Override
        String symbolName() {
            return "meri:rock-normal";
        }

        @Override
        int iconRotate() {
            return 180;
        }
    }

}
