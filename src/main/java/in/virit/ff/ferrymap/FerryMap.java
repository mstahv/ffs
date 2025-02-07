package in.virit.ff.ferrymap;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import in.virit.ff.Layout;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.vaadin.addons.maplibre.LineLayer;
import org.vaadin.addons.maplibre.MapLibre;
import org.vaadin.addons.maplibre.components.TrackerMarker;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.notification.VNotification;
import org.vaadin.firitin.geolocation.Geolocation;
import org.vaadin.firitin.geolocation.GeolocationOptions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.function.Consumer;

@Route(layout = Layout.class)
@MenuItem(title = "VikenMap", order = MenuItem.END - 1, icon = VaadinIcon.GLOBE)
public class FerryMap extends VerticalLayout implements Consumer<VesselData> {
    static final String MERIDATA = "meridata";
    GeometryFactory gf = new GeometryFactory();
    MapLibre map = new FancyNauticalMapOfFinland();
    private UI ui;
    private TrackerMarker marker;
    private TrackerMarker viken;

    public FerryMap(VikenService vikenService) throws IOException, URISyntaxException {
        // TODO add selection for ferry (and figure out their vessel MMSI)
        setPadding(false);
        map.getStyle().setBackgroundColor("white"); // TODO support dark mode !?
        addAndExpand(map);
        addAttachListener(a -> {
            a.getUI().setPollInterval(10*1000);
            var locate = new VButton(VaadinIcon.BULLSEYE) {
                boolean zoomeNext = true;
                LinkedList<TailPoint> tailPoints = new LinkedList<>();
                private LineLayer tail;
                private Geolocation geolocation;

                {
                    getStyle().setPosition(Style.Position.ABSOLUTE);
                    getStyle().setRight("1em");

                    addClickListener(e -> {
                        zoomeNext = true;
                        if (geolocation == null) {
                            var options = new GeolocationOptions();
                            options.setEnableHighAccuracy(true);
                            this.geolocation = Geolocation.watchPosition(p -> {
                                System.out.println("New location: " + p);
                                Coordinate coordinate = new Coordinate(p.getCoords().getLongitude(), p.getCoords().getLatitude());
                                if (marker == null) {
                                    marker = new TrackerMarker(map) {
                                        @Override
                                        protected String getDefaultSvgMarkerCss() {
                                            return "stroke: %s; fill: %s; fill-opacity:0.5;"
                                                    .formatted(getColor(), getColor() );
                                        }
                                    };
                                    marker.setColor("purple");
                                }
                                marker.addPoint(coordinate, Instant.now(), p.getCoords().getHeading() == null ? 0 : (int) p.getCoords().getHeading().doubleValue());
                                marker.getMarker().setPopover(() -> {
                                    return new RichText("""
                                            You:
                                            <br>Speed: %s km/h
                                            """
                                            .formatted(
                                                    p.getCoords().getSpeed() == null ?
                                                            "??" :
                                                            Math.round(p.getCoords().getSpeed()*1000/3600)
                                            )
                                    );
                                });
                                if (zoomeNext) {
                                    map.flyTo(coordinate.getX(), coordinate.getY(), 15.0);
                                    zoomeNext = false;
                                }
                            }, err -> {
                                VNotification.prominent("Could not locate you: " + err.getErrorMessage());
                            }, options);
                        }
                    });

                }

                record TailPoint(Point point, Instant time) {
                }
            };
            findAncestor(Layout.class).addToNavbar(locate);

            // Also start listening to Viken service (AIS position of a local ferry)
            vikenService.registerListener(this);
            //and because of Vaadin oddities (getUI() is not thread safer), save UI reference here
            this.ui = a.getUI();

            addDetachListener(e -> {
                locate.removeFromParent();
                vikenService.unregisterListener(this);
            });
        });
    }

    @Override
    public void accept(VesselData vikenStatus) {
        Command command = () -> {
            if (viken == null) {
                viken = new TrackerMarker(map) {

                    @Override
                    protected String getDefaultSvgMarkerCss() {
                        return "stroke: %s; fill: %s; fill-opacity:0.5;"
                                .formatted(getColor(), getColor() );
                    }

                };
                viken.setColor("orange");
                map.flyTo(vikenStatus.coordinate().getX(), vikenStatus.coordinate().getY(), 13.0);
            }
            viken.addPoint(vikenStatus.coordinate(), vikenStatus.time(), (int) vikenStatus.cog());
            viken.getMarker().setPopover(() -> {
                return new RichText("""
                        Viken:
                        <br>Speed: %s knots
                        <br>Status: %s
                        <br>Reported: ~ %s (%s)
                        """
                        .formatted(
                                vikenStatus.sog(),
                                vikenStatus.status(),
                                (Instant.now().getEpochSecond() - vikenStatus.time().getEpochSecond()) + " seconds ago",
                                vikenStatus.time())
                );
            });
        };
        if (ui != null) {
            ui.access(command);
        } else {
            // first sync call
            command.execute();
        }

    }
}
