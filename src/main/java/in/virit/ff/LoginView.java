package in.virit.ff;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VPasswordField;
import org.vaadin.firitin.components.textfield.VTextField;

@Route
public class LoginView extends VVerticalLayout {

    private VTextField username = new VTextField("Username");
    private VPasswordField password = new VPasswordField("Password");

    public LoginView(Session session) {
        add(new H1("FoolFerries Sailor Login"));
        add(new RichText().withMarkDown("""
        This very experimental app impersonates you in booking.finferries.fi and gives you a better UX. If you choose to 
        use this app, you should be aware that it is not an official app, and it is not endorsed by Finferries.
        Also you must accept the terms of the booking site. Also note that this app can easily break if
        the booking site changes. If you are not comfortable with this, please do not use this app.
        
        The app needs your username and password to work. The credentials are stored in your browser's local storage
        base64 encoded (which means they are not encrypted) and during the session, they are used to log in to the 
        booking site. Note, that if I would be evil, I could steal your credentials by changing the source code 🧸 Also
        note that it makes no sense to use the same father's birthday as a password on the finferries app as you use in 
        your net bank and gmail. The source code is available at [GitHub](https://github.com/mstahv/ffs/) if you
        wish to host it yourself or submit a PR 🤓
        
        And!! Before logging in, I suggest to install this to your phone's home screen for best experience.
        """));

        add(username);
        add(password);
        add(new Button("Login", e -> {
            session.register(username.getValue(), password.getValue());

        }));
    }
}
