package in.virit.ff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.WebStorage;
import in.virit.ff.bookingdtos.FerryRoute;
import in.virit.ff.bookingdtos.Harbor;
import in.virit.ff.bookingdtos.ReservationDetails;
import in.virit.ff.bookingdtos.Tour;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Base64;

@SessionScope
@Component
public class Session {

    private LocalStorageSettings localStorageSettings;

    private Connection connection = Jsoup.newSession()
            .followRedirects(true);

    HttpClient client = HttpClient.newBuilder()
            .cookieHandler(new CookieManager())
            .version(HttpClient.Version.HTTP_2)
            //.followRedirects(HttpClient.Redirect.ALWAYS)
            .build();
    private String username;
    private String password;

    public boolean isLoggedIn() {
        return username != null;
    }

    public void login() {
        WebStorage.getItem("localStorageSettings", s -> {
            if(s == null) {
                UI.getCurrent().navigate(LoginView.class);
                return;
            }
            try {
                localStorageSettings = new ObjectMapper().readValue(s, LocalStorageSettings.class);
                String[] split = new String(Base64.getDecoder().decode(localStorageSettings.getCredentials())).split(":");
                loginWithCredentials(split[0], split[1]);
                UI.getCurrent().navigate(MainView.class).ifPresent(n -> n.init());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void loginWithCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        try {
            loginHttpClient();
        } catch (Exception e) {
            Notification.show("Login failed: " + e.getMessage());
            e.printStackTrace();
            username = null;
            password = null;
        }
    }

    private void loginHttpClient() {

        try {
            //name="woocommerce-login-nonce" value="c451a7fa69" /><input type="hidden" name="_wp_http_referer" value="/my-account/" />
            final String NONCE_START = "woocommerce-login-nonce\" value=\"";
            HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://booking.finferries.fi/my-account/"))
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString());
            String loginformBody = response.body();
            loginformBody = loginformBody.substring(loginformBody.indexOf(NONCE_START) + NONCE_START.length());
            String nonce = loginformBody.substring(0, loginformBody.indexOf("\""));

            HttpResponse<String> loginResponse = client.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://booking.finferries.fi/my-account/"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("username=" + username + "&password=" + password + "&login=Login&woocommerce-login-nonce=" + nonce))
                    .build(), HttpResponse.BodyHandlers.ofString());

            if(loginResponse.statusCode()> 399) {
                throw new RuntimeException("Login failed with status code: " + loginResponse.statusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void saveCredentials() {
        String s = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        getLocalStorageSettings().setCredentials(s);
        persistLocalStorageSettings();
    }

    private void persistLocalStorageSettings() {
        try {
            WebStorage.setItem("localStorageSettings", new ObjectMapper().writeValueAsString(localStorageSettings));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserName() {
        return username;
    }

    public HttpClient getClient() {
        return client;
    }

    public void register(String username, String password) {
        loginWithCredentials(username, password);
        saveCredentials();
        UI.getCurrent().navigate(MainView.class);
    }

    public void saveReservationDetails(ReservationDetails rd) {
        if(rd.name() == null || rd.name().isEmpty()) {
            Notification.show("Name must be set!");
            return;
        }
        getLocalStorageSettings().getSavedDetails().add(rd);
        getLocalStorageSettings().setLastReservationDetails(rd);
        persistLocalStorageSettings();
    }

    public LocalStorageSettings getLocalStorageSettings() {
        if(localStorageSettings == null) {
            localStorageSettings = new LocalStorageSettings();
        }
        return localStorageSettings;
    }

    public void book(FerryRoute fr, LocalDate localDate, Harbor from, Harbor to, Tour tour, ReservationDetails rd) {
        /*
        -----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="add-to-cart"

35
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-line"

2807
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-passenger-count"

1
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-harbor-from"

57
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-harbor-to"

52
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-departure-date"

20240820
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-departure-time"

12:15
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-vehicle-type"

181
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-pets"

false
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-dangerous-goods"

false
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-assistant"

false
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-animal-transport"

false
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-additional-comments"

NMK-152
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-restaurant-count"

0
-----------------------------3952403640432312113951113417
Content-Disposition: form-data; name="finferries-vessel"

406
-----------------------------3952403640432312113951113417--
        */

        MultipartEntity entity = new MultipartEntity();
        entity.addPart("add-to-cart", toStringBody("35")); // 35 🤷‍
        entity.addPart("finferries-line", toStringBody(fr.id()));
        entity.addPart("finferries-passenger-count", toStringBody(rd.passengerCount() + ""));
        entity.addPart("finferries-harbor-from", toStringBody(from.id()+""));
        entity.addPart("finferries-harbor-to", toStringBody(to.id()+""));
        entity.addPart("finferries-departure-date", toStringBody(localDate.toString()));
        entity.addPart("finferries-departure-time", toStringBody(tour.start().toString()));
        entity.addPart("finferries-vehicle-type", toStringBody(rd.vehicleType().id()+""));
        entity.addPart("finferries-pets", toStringBody("false"));
        entity.addPart("finferries-dangerous-goods", toStringBody("false"));
        entity.addPart("finferries-assistant", toStringBody("false"));
        entity.addPart("finferries-animal-transport", toStringBody("false"));
        entity.addPart("finferries-additional-comments", toStringBody(rd.comments()));
        entity.addPart("finferries-restaurant-count", toStringBody("0"));
        entity.addPart("finferries-vessel", toStringBody(tour.vesselId()));
        try {
            HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://booking.finferries.fi/checkout-fi/"))
                    .header("Content-Type", "multipart/form-data; boundary=3952403640432312113951113417")
                    .POST(HttpRequest.BodyPublishers.ofInputStream(() -> {
                        try {
                            return entity.getContent();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }))
                    .build(), HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() > 399) {
                throw new RuntimeException("Booking failed with status code: " + response.statusCode());
            }
            String bodyHtml = response.body();

            Connection connection = Jsoup.newSession();

            String body = Jsoup.connect("https://booking.finferries.fi/checkout-fi/")
                    .data("security", "e9ec9c0cb3")
                    .data("country", "FI")
                    .data("s_country", "FI")
                    .data("has_full_address", "true")
                    .data("post_data", "billing_first_name=Matti&billing_last_name=Tahvonen&billing_country=FI&billing_phone=%2B358443029728&billing_email=matti%40tahvonen.com&terms-field=1&woocommerce-process-checkout-nonce=f40f00a963&_wp_http_referer=%2Fcheckout-fi%2F")
                    .execute().body();

            Document docu = Jsoup.parse(bodyHtml);
            FormElement formElement = docu.forms().get(0);

            formElement.submit();

            // security=e9ec9c0cb3
            // &country=FI
            // &s_country=FI
            // &has_full_address=true
            // &post_data=billing_first_name%3DMatti%26
            // billing_last_name%3DTahvonen%26
            // billing_country%3DFI%26
            // billing_phone%3D%252B358443029728%26
            // billing_email%3Dmatti%2540tahvonen.com%26
            // terms-field%3D1%26
            // woocommerce-process-checkout-nonce%3Df40f00a963%26
            // _wp_http_referer%3D%252Fcheckout-fi%252F


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ContentBody toStringBody(String str) {
        return new StringBody(str, ContentType.TEXT_PLAIN);
    }
}
