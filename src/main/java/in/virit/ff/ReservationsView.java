package in.virit.ff;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.Route;
import in.virit.ff.bookingdtos.FerryRoute;
import in.virit.ff.bookingdtos.Harbor;
import in.virit.ff.bookingdtos.ReservationDetails;
import in.virit.ff.bookingdtos.Tour;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.button.DeleteButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.select.VSelect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    }

    public void init() {
        removeAll();
        ArrayList<Session.Reservation> reservations = session.fetchReservations();
        addAndExpand(new VGrid<Session.Reservation>(Session.Reservation.class) {{
            addComponentColumn(r ->
                new DeleteButton(() -> {
                    session.cancelReservation(r);
                    init();
                })
            ).setAutoWidth(true).setFlexGrow(0);
            getColumnByKey("id").setAutoWidth(true).setFlexGrow(0);
            getColumnByKey("status").setAutoWidth(true).setFlexGrow(0);
            addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
            setItems(reservations);
        }});
    }
}
