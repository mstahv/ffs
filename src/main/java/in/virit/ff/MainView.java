package in.virit.ff;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.router.Route;
import in.virit.ff.bookingdtos.FerryRoute;
import in.virit.ff.bookingdtos.Harbor;
import in.virit.ff.bookingdtos.ReservationDetails;
import in.virit.ff.bookingdtos.Tour;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.select.VSelect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Route
public class MainView extends VVerticalLayout {
    private final Session session;
    private final BookingService bookingService;
    private final DatePicker datePicker = new DatePicker();

    VSelect<FerryRoute> routeSelect = new VSelect<FerryRoute>()
            .withFullWidth();
    VSelect<Harbor> from = new VSelect<Harbor>().withLabel("From")
            .withItemLabelGenerator(Harbor::name)
            .withFullWidth()
            .withMinWidth("100px");
    VSelect<Harbor> to = new VSelect<Harbor>().withLabel("To")
            .withFullWidth()
            .withMinWidth("100px")
            .withItemLabelGenerator(Harbor::name);

    VSelect<Tour> tours = new VSelect<Tour>().withLabel("Tour")
            .withFullWidth();

    VSelect<ReservationDetails> reservationDetails = new VSelect<ReservationDetails>()
            .withItemLabelGenerator(rd -> rd.name())
            .withMinWidth("300px");

    ReservationDetails newDetailsValue = new ReservationDetails("New...", null, 1, "");
    private ReservationDetailsForm reservationDetailsForm;
    private Details details;
    private List<Harbor> harbors;
    private VButton book;

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

        setAlignItems(FlexComponent.Alignment.CENTER);
    }

    public void init() {
        add(new H1("FoolFerries 🤪"));
        add(session.getUserName() + " " + bookingService.nowFinland().toLocalTime());
        reservationDetailsForm = new ReservationDetailsForm(session, bookingService);
        routeSelect.setItemLabelGenerator(FerryRoute::name);
        routeSelect.setItems(FerryRoute.routes());
        routeSelect.addValueChangeListener(e -> {
            harbors = bookingService.getHarbors(routeSelect.getValue());
            from.setItems(harbors);
            to.setItems(harbors);
            updateTimes();
        });
        FerryRoute.routes().stream().filter(r -> r.id().equals(session.getLocalStorageSettings().getLastRouteId())).findFirst().ifPresent(routeSelect::setValue);
        add(routeSelect);
        add(new VHorizontalLayout()
                        .withExpanded(from)
                        .withComponents(new VButton(VaadinIcon.ROTATE_LEFT, e -> {
                            Harbor temp = from.getValue();
                            from.setValue(to.getValue());
                            to.setValue(temp);
                        }))
                        .withExpanded(to)
                .withFullWidth()
                .withAlignItems(FlexComponent.Alignment.BASELINE));

        add(datePicker);
        add(tours);
        tours.addValueChangeListener(e -> {
            if(e.getValue() != null) {
                tours.setHelperText(e.getValue().route());
            }
        });

        details = new Details("Reservation details");
        Map<String, ReservationDetails> nameToRd = session.getLocalStorageSettings().getSavedDetails();
        Collection<ReservationDetails> values = nameToRd.values();
        List<ReservationDetails> savedDetails = new ArrayList<>();
        savedDetails.addAll(values);
        savedDetails.add(newDetailsValue);
        reservationDetails.setItems(savedDetails);
        add(reservationDetails);
        reservationDetails.addValueChangeListener(e -> {
            if(e.getValue() == newDetailsValue) {
                details.setOpened(true);
            } else if(e.getValue() != null) {
                reservationDetailsForm.setEntity(e.getValue());
            }
        });
        if(savedDetails.size() > 1) {
            session.getLocalStorageSettings().getLastReservationDetails().ifPresent(reservationDetails::setValue);
        } else {
            reservationDetails.setValue(newDetailsValue);
        }
        //details.setSummaryText("Vehicle etc");
        details.setWidthFull();
        details.add(reservationDetailsForm);
        add(reservationDetails,details);

        int hToid = session.getLocalStorageSettings().getLastHarborFromId();
        harbors.stream().filter(h -> h.id() == hToid).findFirst().ifPresent(to::setValue);
        int hFromid = session.getLocalStorageSettings().getLastHarborToId();
        harbors.stream().filter(h -> h.id() == hFromid).findFirst().ifPresent(from::setValue);
        datePicker.setValue(bookingService.nowFinland().toLocalDate());
        datePicker.addValueChangeListener(e -> {
            updateTimes();
            if(tours.getValue() == null) {
                Notification.show("No tours available for selected time");
            }
        });
        updateTimes();
        if(tours.getValue() == null) {
            Notification.show("No tours available for today, date set for tomorrow");
            datePicker.setValue(datePicker.getValue().plusDays(1));
        }
        tours.focus();

        from.addValueChangeListener(e -> updateTimes());
        to.addValueChangeListener(e -> updateTimes());

        book = new DefaultButton("Book", e -> {
            session.book(
                    routeSelect.getValue(),
                    datePicker.getValue(),
                    from.getValue(),
                    to.getValue(),
                    tours.getValue(),
                    reservationDetails.getValue()
            );
            session.saveLastTrip(routeSelect.getValue(), from.getValue(), to.getValue());
            Notification notification = new Notification();
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.setDuration(8000);
            notification.add(
                    new VVerticalLayout(new RichText().withMarkDown("""
            ## Booking done!
            
            Next time opening the app you ought to have defaults for the reverse trip.
            
            Make sure you got the confirmation sms/email, this app might get broken if the booking site changes
            and there was no budget for decent error handling 🧸
            
            """),new Button("Book another trip...", b -> {
                        book.setEnabled(true);
                        notification.close();
                    })));
            notification.open();
        }).withFullWidth();
        book.setDisableOnClick(true);
        add(book);

        add(new VButton("Reload session", e -> {
            session.reload();
        }).withThemeVariants(ButtonVariant.LUMO_TERTIARY));

        add(new Anchor("https://github.com/mstahv/ffs/", "Source code & bug reports", AnchorTarget.BLANK));

    }

    private void updateTimes() {
        if(from.getValue() == null || to.getValue() == null || datePicker.getValue() == null) {
            return;
        }
        List<Tour> tours1 = bookingService.getTours(datePicker.getValue(), from.getValue(), to.getValue());
        this.tours.setItems(tours1);
        tours1.stream().filter(
                t -> t.start().atDate(datePicker.getValue()).isAfter(bookingService.nowFinland())
        ).findFirst().ifPresent(tours::setValue);
    }

    public void selectReservationDetails(ReservationDetails rd) {
        details.setOpened(false);
        Map<String, ReservationDetails> nameToRd = session.getLocalStorageSettings().getSavedDetails();
        Collection<ReservationDetails> values = nameToRd.values();
        List<ReservationDetails> savedDetails = new ArrayList<>();
        savedDetails.addAll(values);
        savedDetails.add(newDetailsValue);
        reservationDetails.setItems(savedDetails);
        reservationDetails.setValue(rd);
    }
}
