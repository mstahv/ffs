package in.virit.ff.ferrymap.finnishterrainmap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.maplibre.LinePaint;
import org.vaadin.addons.maplibre.MapLibre;
import org.vaadin.addons.maplibre.dto.CircleLayerDefinition;
import org.vaadin.addons.maplibre.dto.CirclePaint;
import org.vaadin.addons.maplibre.dto.LineLayerDefinition;
import org.vaadin.addons.maplibre.dto.RawColor;
import org.vaadin.addons.maplibre.dto.RgbaColor;
import org.vaadin.addons.maplibre.dto.RootDefinition;
import org.vaadin.addons.maplibre.dto.SymbolLayerDefinition;
import org.vaadin.addons.maplibre.dto.SymbolLayout;
import org.vaadin.addons.maplibre.dto.SymbolPaint;
import org.vaadin.addons.maplibre.dto.VectorMapSource;
import org.vaadin.addons.maplibre.dto.expressions.Equals;
import org.vaadin.addons.maplibre.dto.expressions.Interpolate;
import org.vaadin.addons.maplibre.dto.expressions.ZoomStep;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Route
public class LocalMaastokartta extends MapLibre {

    public static int OVERALL_BREAKPOINT = 11;
    public static String OVERALL_POSTFIX = "-overall";
    public static int ROAD_OVERALL_BREAKPOINT = OVERALL_BREAKPOINT;

    //public static Expression WIDTH_SMALL = 0;
    public static int WIDTH_MEDIUM = 1;
    public static int WIDTH_LARGE = 2;

    public static class LocalMapDef extends RootDefinition {
        {
            setName("Maastokartta");
            setGlyphs("https://demotiles.maplibre.org/font/{fontstack}/{range}.pbf");
        }

        @JsonIgnore
        public InputStream str() {
            String json = toString();
            return new ByteArrayInputStream(json.getBytes());
        }
    }

    public LocalMaastokartta() {
        super(new LocalMapDef().str());
        setCenter(22.25, 60.17);
        setZoomLevel(12);
        addSource("mtk", new VectorMapSource("https://fvtns.dokku1.parttio.org/services/mtk-combined"));
        //addSource("kk", new VectorMapSource("http://localhost:8000/services/korkeuskayra"));
        // TODO these are rough, especially 100m ones, figure out proper smoothing and pre-flighting (pieces of contours would need to be joined so that Ramer-Douglas-Peucker from tippecanoe would properly work
        addSource("kk20", new VectorMapSource("https://fvtns.dokku1.parttio.org/services/korkeuskayra20"));
        addSource("kk100", new VectorMapSource("https://fvtns.dokku1.parttio.org/services/korkeuskayra100"));
        addSprite("meri", "https://virit.in/maastokartta/merisprite");

        smallZoomLevelsSpecialRules();
        // sea
        addSourceLayer(new WaterArea("vesi-all"));
        // lakes & ponds
        addSourceLayer(new Vesikuoppa());
        // rivers & streams
        addSourceLayer(new WaterArea("allas"));

        basicTerrainMapAreasFromMMLStyles();

        addSourceLayer(new WaterEdge());
        addSourceLayer(new WaterEdge() {{
            setId("allasreuna");
            setSourceLayer("rakennusreunaviiva");
            setFilter(new Equals("kohdeluokka", Kohdeluokka.ALLAS___ALUE));
        }});
        addSourceLayer(new UnclearWaterEdge());


        // TODO "korkeusmalli" for smallest zoom levels 🤔

        addSourceLayer(new LineLayerDefinition("kk100") {{
            setSource("kk100");
            setSourceLayer("korkeuskayra100");
            setPaint(new LinePaint(new RgbaColor(164,50,50,0.5)) {{
                setLineWidth(Interpolate.linear().zoom(new ZoomStep(6, 0.001), new ZoomStep(8, .5)));
                setMinZoom(6);
            }});
            setMaxZoom(9);
        }});

        addSourceLayer(new LineLayerDefinition("kk20") {{
            setSource("kk20");
            setSourceLayer("korkeuskayra20");
            setPaint(new LinePaint() {{
                setLineColor(Interpolate.linear().zoom(
                        new ZoomStep(OVERALL_BREAKPOINT + -2, new RgbaColor(164,50,50,0.0)),
                        new ZoomStep(OVERALL_BREAKPOINT + 1, new RgbaColor(164,50,50,0.5)),
                        new ZoomStep(OVERALL_BREAKPOINT + 2, new RgbaColor(164,50,50,.0))));

                setLineWidth(Interpolate.linear().zoom(new ZoomStep(6, 0.001), new ZoomStep(11, 1.0)));
            }});
            setMaxZoom(OVERALL_BREAKPOINT + 2);
        }});

        // TODO refactor thse
        // TODO viettoviiva
        addSourceLayer(new LineLayerDefinition("korkeuskayra") {{
            setSource("mtk");
            setSourceLayer("korkeuskayra");
            setPaint(new LinePaint() {{
                setLineColor(Interpolate.linear().zoom(
                        new ZoomStep(OVERALL_BREAKPOINT + 1, new RgbaColor(164,50,50,0.0)),
                        new ZoomStep(OVERALL_BREAKPOINT + 2, new RgbaColor(164,50,50,1.0))));
                setLineWidth(Interpolate.linear().zoom(new ZoomStep(10, 0.05), new ZoomStep(16, 2.0)));
            }});
            setSupplementalJson("""
            {
            "filter": ["==", ["%", ["get","korkeusarvo"], 5000], 0]
            }
            """);
        }});
        addSourceLayer(new LineLayerDefinition("korkeuskayra") {{
            setSource("mtk");
            setId("apukayra");
            setSourceLayer("korkeuskayra");
            final double fraction = 0.6;
            setPaint(new LinePaint() {{
                setLineColor(Interpolate.linear().zoom(
                        new ZoomStep(OVERALL_BREAKPOINT + 1, new RgbaColor(164,50,50,0.0)),
                        new ZoomStep(OVERALL_BREAKPOINT + 2, new RgbaColor(164,50,50,1.0))));
                setLineWidth(Interpolate.linear().zoom(new ZoomStep(10, 0.05*fraction), new ZoomStep(16, 2*fraction)));
                //setLineDasharray(4,2);
            }});
            setSupplementalJson("""
            {
            "filter": ["==", ["%", ["get","korkeusarvo"], 5000],2500]
            }
            """);
        }});


        /*
        addSourceLayer(new SymbolLayerDefinition("vesikivi") {{
            setSource("mtk");
            setSourceLayer("vesikivi");
            setLayout(new SymbolLayout() {{
                setTextField("X");
                setIconAllowOverlap(true);
            }});
        }});
         */

        addSourceLayer(new LineLayerDefinition("vesikivikko") {{
            setSource("mtk");
            setSourceLayer("vesikivikko");
            //TODO support proper colors new RgbaColor(0,0,255,0.5)
            // In nautical map, dotted line is used to indicate shallow water 🤔
            setPaint(new LinePaint() {{
                setLineColor(new RgbaColor(0,0,0,0.5));
                setLineWidth(1.0);
                setLineDasharray(1,1);
            }});
        }});


//        addLineLayer("kk", "korkeuskayrafgb", new LinePaint(new RgbaColor(0,0,0,1.0), 2.0));
//        addMoveEndListener(e -> {
//            VNotification.show("ZL:" + e.getZoomLevel());
//        });
    }

    private void smallZoomLevelsSpecialRules() {
        addSourceLayer(new WaterArea("vesi"+OVERALL_POSTFIX) {{
            // bit darker color for simplified overall water areas, "terrain map standard" is quite light for small levels
            // TODO make it expression so that it is the standard when changing to "full details".
            Interpolate color = Interpolate.linear().zoom(
                    new ZoomStep(6, new RawColor("#023E9C")),
                    new ZoomStep(OVERALL_BREAKPOINT, new RawColor("#80FFFE"))
            );
            getPaint().setFillColor(color);
            setMaxZoom(OVERALL_BREAKPOINT);
        }});
        addSourceLayer(new BasicFill("maatalousmaa"+OVERALL_POSTFIX, "#FFD980") {{
            setMaxZoom(OVERALL_BREAKPOINT);
        }});

        addSourceLayer(new BasicFill("taajaanrakennettualue"+OVERALL_POSTFIX, "#b2977e") {{
            setMaxZoom(OVERALL_BREAKPOINT);
        }});

        addSourceLayer(new Tie1A1BReuna() {{
            setId("tieviiva-numeroidut-1r");
            setSourceLayer("tieviiva-numeroidut"+OVERALL_POSTFIX);
            setMinZoom(0);
            setMaxZoom(ROAD_OVERALL_BREAKPOINT);
        }});
        addSourceLayer(new Tie2A2BReuna() {{
            setId("tieviiva-numeroidut-2r");
            setSourceLayer("tieviiva-numeroidut"+OVERALL_POSTFIX);
            setMinZoom(6);
            setMaxZoom(ROAD_OVERALL_BREAKPOINT-1);
        }});
        addSourceLayer(new Tie3A3BReuna() {{
            setId("tieviiva-numeroidut-3r");
            setSourceLayer("tieviiva-numeroidut"+OVERALL_POSTFIX);
            setMinZoom(9);
            setMaxZoom(ROAD_OVERALL_BREAKPOINT);
        }});

        addSourceLayer(new Tie1A1BFill() {{
            setId("tieviiva-numeroidut-1f");
            setSourceLayer("tieviiva-numeroidut"+OVERALL_POSTFIX);
            setMinZoom(0);
            setMaxZoom(ROAD_OVERALL_BREAKPOINT);
        }});
        addSourceLayer(new Tie2A2BFill(){{
            setId("tieviiva-numeroidut-2f");
            setSourceLayer("tieviiva-numeroidut"+OVERALL_POSTFIX);
            setMinZoom(6);
            setMaxZoom(ROAD_OVERALL_BREAKPOINT);
        }});
        addSourceLayer(new Tie3A3BFill(){{
            setId("tieviiva-numeroidut-3f");
            setSourceLayer("tieviiva-numeroidut"+OVERALL_POSTFIX);
            setMinZoom(9);
            setMaxZoom(ROAD_OVERALL_BREAKPOINT);

        }});

        addSourceLayer(new BasicLine("valtakunnanraja"+OVERALL_POSTFIX, "#4D00B3") {{getPaint().setLineWidth(3.0);}});
    }

    private void basicTerrainMapAreasFromMMLStyles() {
        /*
        // TODO This needs to be splitted to use different layers

            {
      "id": "maasto_alue",
      "type": "fill",
      "source": "taustakartta",
      "source-layer": "maasto_alue",
      "filter": [
        "match",
        ["get", "kohdeluokka"],
        [
          35300,
          35411,
          35412,
          35421,
          35422,
          34300,
          34100,
          34700,
          38300,
          38700,
          39110,
          39120,
          39130,
          33000,
          38400,
          38600,
          35401,
          35402
        ],
        true,
        false
      ],
      "paint": {
        "fill-color": [
          "match",
          ["get", "kohdeluokka"],
          [ 35401],
          "#D0CC5A",
          [35412, 35422, 35402],
          "#CEEAEA",
          [35300],
          "#CaEfEa",
          [39130],
          "rgb(255,255,130)",
          [34100, 38600], // kallio, vesikivikko
          "hsla(208, 11%, 75%, 0.5)",
          [35411], // suo!?
          [34700], //kivikko
          "hsl(208, 27%, 85%)",
          [38700, 38400], // matalikko, tulva-alue
          "hsl(207, 100%, 94%)",
          [39110], // avoin metsämaa
          "hsla(44, 100%, 83%, 0.84)",
          [33000], // taytemaa
          "#E8D9A5",
          [39120], // varvikko
          "hsl(65, 57%, 91%)",
          "hsla(360, 100%, 100%, 0)"
        ]
      }
    },
         */
        // suo
        addSourceLayer(new BasicFill("soistuma", "#E6F4F5"));
        addSourceLayer(new Marsh());
        addSourceLayer(new MarshOpen());
        addSourceLayer(new OtherOpenArea());
        addSourceLayer(new BasicFill("taytemaa", "#E8D9A5"));

        /*
        TODO
            {
      "id": "maasto_alue_louhikko",
      "type": "fill",
      "source": "taustakartta",
      "source-layer": "maasto_alue",
      "minzoom": 14,
      "filter": ["match", ["get", "kohdeluokka"], [34700], true, false],
      "paint": {"fill-pattern": "louhikko", "fill-color": "rgba(0, 0, 0, 0)"}
    },
         */

        /*
           {
      "id": "maankaytto",
      "type": "fill",
      "source": "taustakartta",
      "source-layer": "maankaytto",
      "filter": [
        "match",
        ["get", "kohdeluokka"],
        [
          32111,
          32112,
          32113,
          32200,
          32300,
          32500,
          32611,
          32612,
          32800,
          32900,
          33100,
          34300,
          38900,
          40200
        ],
        true,
        false
      ],
      "paint": {
        "fill-color": [
          "match",
          ["get", "kohdeluokka"],
          40200, // taajaanrakenettu alue
          "#f8fbf5",
          32611, // pelto
          "#FCCE58",
          32200, // hautausmaa
          "hsl(87, 45%, 72%)",
          32612, // puutarha
          "hsl(87, 45%, 72%)",
          32800, // niitty
          "#FDF27C",
          32900, // puisto
          "hsl(87, 45%, 72%)",
          33100, //urheilualue
          "hsl(87, 65%, 82%)",
          34300, // hietikko
          "hsl(60, 55%, 80%)",
          32500, // louhos
          "#dde5dd",
          "#f7f7f3"
        ],
        "fill-outline-color": "rgba(0, 0, 0, 1)"
      }
    },
         */

        addSourceLayer(new BasicFill("taajaanrakennettualue", "#b2977e") {{
            setMaxZoom(12);
        }});
        addSourceLayer(new Maatalousmaa());
        addSourceLayer(new BasicFill("puisto", "#FDF27C"));
        addSourceLayer(new BasicFill("urheilujavirkistysalue", "#d4efb3"));
        addSourceLayer(new BasicFill("hietikko", "#e8e8b0"));
        addSourceLayer(new BasicFill("louhos", "#dde5dd"));
        addSourceLayer(new BasicFill("kallioalue", "#c6c6c6"));


        addSourceLayer(new MaastokuvionReuna());

        addSourceLayer(new AirportArea());

        /*
        // TODO figure out if needed, "urheilualueen reuna", black line should be fine ??
            {
      "id": "maankaytto_reuna",
      "type": "line",
      "source": "taustakartta",
      "source-layer": "maankaytto",
      "filter": ["match", ["get", "kohdeluokka"], [33100], true, false],
      "paint": {
        "line-color": [
          "match",
          ["get", "kohdeluokka"],
          [33100],
          "hsl(87, 15%, 52%)",
          "hsl(87, 65%, 82%)"
        ]
      }
    },
         */

        addSourceLayer(new Ditch());

        addSourceLayer(new ErityisalueenReuna());

        // TIET JA RAUTATIET

        // TODO tiet & rautatiet pinnan alla

        addSourceLayer(new Ajotie() {{
            setSourceLayer("tieviiva-muut");
        }});
        addSourceLayer(new Ajopolku() {{
            setSourceLayer("tieviiva-muut");
        }});
        // TODO kavely- ja pyoratie, paallystamaton, pinnalla
        // TODO kavely- ja pyoratie, paallystetty, pinnalla

        addSourceLayer(new Tie1A1BReuna());
        addSourceLayer(new Tie2A2BReuna());
        addSourceLayer(new Tie2ReunaNumeroimaton());
        addSourceLayer(new Tie3A3BReuna());
        addSourceLayer(new Tie3ReunaNumeroimaton());

        addSourceLayer(new Tie1A1BFill());
        addSourceLayer(new Tie2A2BFill());
        addSourceLayer(new Tie2Numeroimaton());
        addSourceLayer(new Tie3A3BFill());
        addSourceLayer(new Tie3Numeroimaton());


        addSourceLayer(new Building());

        // TODO line & fill rakennus, laiturit yms


        addSourceLayer(new SuurjanniteLinja());
        addSourceLayer(new CircleLayerDefinition() {{
            setSource("mtk");
            setSourceLayer("suurjannitelinjanpylvas");
            setPaint(new CirclePaint(){{
                setCircleRadius(5.0);
                setCircleColor("white");
                setCircleStrokeColor("black");
                setCircleStrokeWidth(1);
            }});
            setMinZoom(14);
        }});
        addSourceLayer(new CircleLayerDefinition() {{
            setSource("mtk");
            setSourceLayer("muuntaja");
            setPaint(new CirclePaint(){{
                setCircleRadius(5.0);
                setCircleColor("white");
                setCircleStrokeColor("black");
                setCircleStrokeWidth(1);
            }});
            setMinZoom(14);
        }});
        addSourceLayer(new SymbolLayerDefinition() {{
            setSource("mtk");
            setSourceLayer("sahkolinjansymboli");
            setPaint(new SymbolPaint(){{
                setTextHaloColor("white");
                setTextHaloWidth(1);
            }});
            setLayout(new SymbolLayout(){{
                setTextField("Z");
                setTextSize(12.0);

            }});
            setMinZoom(12);
        }});
        // TODO suurjännitepylväs

//        addSourceLayer(new MaankayttoReuna()); // kaikki, poista!!

        // soistuma

        addSourceLayer(new Parkkipaikka());

        //addSourceLayer(new Vesikulkuvayla());
        //addSourceLayer(new VesikulkuvaylaTahtayslinja());

        addSourceLayer(new Kivi());
        addSourceLayer(new BasicLine("jyrkanne", "#444") {{
            getPaint().setLineWidth(RoadWidthConstants.ROAD_WIDTH_S);
            setMinZoom(13);
        }});
        addSourceLayer(new BasicLine("pistolaituriviiva", "#000") {{
            getPaint().setLineWidth(RoadWidthConstants.ROAD_WIDTH_S);
            setMinZoom(13);
        }});
        addSourceLayer(new Kaislikko());

    }

}
