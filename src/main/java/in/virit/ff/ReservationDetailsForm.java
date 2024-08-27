package in.virit.ff;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import in.virit.ff.bookingdtos.ReservationDetails;
import in.virit.ff.bookingdtos.VehicleType;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.select.VSelect;
import org.vaadin.firitin.components.textfield.VIntegerField;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.form.BeanValidationForm;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReservationDetailsForm extends BeanValidationForm<ReservationDetails> {

    private VTextField name = new VTextField("Optional: save as for later usage...")
            .withPlaceholder("Optional, fill to quick book later...");
    private VSelect<VehicleType> vehicleType = new VSelect<VehicleType>("Vehicle Type")
            .withItemLabelGenerator(VehicleType::name);
    private VIntegerField passengerCount = new VIntegerField("Passenger Count")
            .withValue(1);
    private VTextField comments = new VTextField("Comments")
            .withPlaceholder("License plate etc...");

    public ReservationDetailsForm(Session session, BookingService bookingService) {
        super(ReservationDetails.class);
        List<VehicleType> vehicleTypes = bookingService.getVehicleTypes();
        vehicleType.setItems(vehicleTypes);
        vehicleTypes.stream().filter(vt -> vt.name().contains("henkilöauto, 4-5")).findFirst().ifPresent(vehicleType::setValue);
        getSaveButton().setText(null);
        getSaveButton().setIcon(VaadinIcon.FILE_ADD.create());
        setSavedHandler(rd -> {
            session.saveReservationDetails(rd);
            findAncestor(MainView.class).selectReservationDetails(rd);
        });
    }

    @Override
    protected List<Component> getFormComponents() {
        List<Component> components = new ArrayList<>();
        components.add(vehicleType);
        components.add(passengerCount);
        components.add(comments);
        // Feeling adventurous? Uncomment the following line in IDE or try to compile after it...
        // TODO: file a JDK bug to fix this
        // components = Arrays.asList(name, vehicleType, passangerCount, comments);
        return components;
    }

    @Override
    public HorizontalLayout getToolbar() {
        return new VHorizontalLayout(name, getSaveButton())
                .withPadding(false).alignAll(FlexComponent.Alignment.BASELINE);
    }
}
