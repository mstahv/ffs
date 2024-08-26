package in.virit.ff;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import in.virit.ff.bookingdtos.FerryRoute;
import in.virit.ff.bookingdtos.Harbor;
import in.virit.ff.bookingdtos.ReservationDetails;
import in.virit.ff.bookingdtos.Tour;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.select.VSelect;

import java.util.ArrayList;
import java.util.List;

@Route
public class MainView extends VVerticalLayout {
    private final Session session;
    private final BookingService bookingService;
    private final DatePicker datePicker = new DatePicker();

    VSelect<FerryRoute> routeSelect = new VSelect<>();
    VSelect<Harbor> from = new VSelect<Harbor>().withLabel("From")
            .withItemLabelGenerator(Harbor::name);
    VSelect<Harbor> to = new VSelect<Harbor>().withLabel("To")
            .withItemLabelGenerator(Harbor::name);

    VSelect<Tour> tours = new VSelect<Tour>().withLabel("Tour");

    VSelect<ReservationDetails> reservationDetails = new VSelect<ReservationDetails>().withLabel("Detail preset")
            .withItemLabelGenerator(rd -> rd.name());

    ReservationDetails newDetailsValue = new ReservationDetails("New...", null, 1, "");
    private ReservationDetailsForm reservationDetailsForm;
    private Details details;

    public MainView(Session session, BookingService bookingService) {
        this.session = session;
        this.bookingService = bookingService;
        if (!session.isLoggedIn()) {
            session.login();
        } else {
            init();
        }

         tours.withItemEnabledProvider(t ->
            t.start().atDate(datePicker.getValue()).isAfter(bookingService.nowFinland())
        );
    }

    public void init() {
        add("FoolFerries:" + session.getUserName() + " " + bookingService.nowFinland().toLocalTime());
        reservationDetailsForm = new ReservationDetailsForm(session, bookingService);
        routeSelect.setItemLabelGenerator(FerryRoute::name);
        routeSelect.setItems(FerryRoute.routes());

        // TODO read defaults from local storage
        routeSelect.setValue(FerryRoute.routes().get(0)); // hardcoded to Viken-Parainen
        add(routeSelect);
        add(new VHorizontalLayout(
                new VVerticalLayout(from, to).withPadding(false)
                        .withSpacing(false),
                new VButton(VaadinIcon.ROTATE_LEFT, e -> {
                    Harbor temp = from.getValue();
                    from.setValue(to.getValue());
                    to.setValue(temp);
                })
        ).withAlignItems(Alignment.CENTER).withPadding(false));

        List<Harbor> harbors = bookingService.getHarbors(routeSelect.getValue());
        from.setItems(harbors);
        to.setItems(harbors);
        add(datePicker);
        add(tours);

        List<ReservationDetails> savedDetails = new ArrayList<>(session.getLocalStorageSettings().getSavedDetails());
        savedDetails.add(newDetailsValue);
        reservationDetails.setItems(savedDetails);
        add(reservationDetails);

        reservationDetails.addValueChangeListener(e -> {
            if(e.getValue() == newDetailsValue) {
                details.setOpened(true);
            } else {
                reservationDetailsForm.setEntity(e.getValue());
            }
        });
        if(!savedDetails.isEmpty()) {
            session.getLocalStorageSettings().getLastReservationDetails().ifPresent(reservationDetails::setValue);
        } else {
            reservationDetails.setValue(newDetailsValue);
        }
        details = new Details();
        details.setSummaryText("Vehicle etc");
        details.add(reservationDetailsForm);
        add(details);

        int hToid = session.getLocalStorageSettings().getLastHarborFromId();
        harbors.stream().filter(h -> h.id() == hToid).findFirst().ifPresent(to::setValue);
        int hFromid = session.getLocalStorageSettings().getLastHarborToId();
        harbors.stream().filter(h -> h.id() == hFromid).findFirst().ifPresent(from::setValue);
        datePicker.setValue(bookingService.nowFinland().toLocalDate());

        datePicker.addValueChangeListener(e -> {
            updateTimes();
        });
        from.addValueChangeListener(e -> updateTimes());
        to.addValueChangeListener(e -> updateTimes());
        updateTimes();

        add(new Button("Book", e -> {
            session.book(
                routeSelect.getValue(),
                datePicker.getValue(),
                from.getValue(),
                to.getValue(),
                tours.getValue(),
                reservationDetails.getValue()
            );
        }));

    }

    private void updateTimes() {
        List<Tour> tours1 = bookingService.getTours(datePicker.getValue(), from.getValue(), to.getValue());
        this.tours.setItems(tours1);
        tours1.stream().filter(
                t -> t.start().atDate(datePicker.getValue()).isAfter(bookingService.nowFinland())
        ).findFirst().ifPresent(tours::setValue);
    }

    public void selectReservationDetails(ReservationDetails rd) {
        details.setOpened(false);
        reservationDetails.getListDataView().addItem(rd);
        reservationDetails.setValue(rd);
    }
}
