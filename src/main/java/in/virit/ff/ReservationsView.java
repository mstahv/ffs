package in.virit.ff;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.DeleteButton;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.util.ArrayList;

@Route(layout = Layout.class)
@MenuItem(icon = VaadinIcon.GRID)
public class ReservationsView extends VVerticalLayout {
    private final Session session;
    private final BookingService bookingService;

    public ReservationsView(Session session, BookingService bookingService) {
        this.session = session;
        this.bookingService = bookingService;
        if (!session.isLoggedIn()) {
            session.login();
        } else {
            init();
        }
        setPadding(false);
    }

    public void init() {
        removeAll();
        ArrayList<Session.Reservation> reservations = session.fetchReservations();
        addAndExpand(new VGrid<Session.Reservation>(Session.Reservation.class, false) {{
            addColumn(r -> r.str())
                    .setHeader("Details");
            addComponentColumn(r ->
                    new VerticalLayout(
                        new DeleteButton(() -> {
                            session.cancelReservation(r);
                            init();
                        })
                    ) {{
                      add(new Div(r.status()));
                      add(new Emphasis(""+r.id()));
                      setPadding(false);
                      setSpacing(false);
                    }}
            ).setAutoWidth(true).setFlexGrow(0).setHeader("");
            addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
            setItems(reservations);
        }});
    }
}
