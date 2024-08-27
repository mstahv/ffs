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
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.FormElement;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static in.virit.ff.BookingService.yyyyMMdd;

@SessionScope
@Component
public class Session {

    public static final String SETTINGS_KEY = "localStorageSettings";
    HttpClient client = buildHttpClient();
    private LocalStorageSettings localStorageSettings;
    private String username;
    private String password;

    private static HttpClient buildHttpClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    private static String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }

    public boolean isLoggedIn() {
        return username != null;
    }

    public void login() {
        WebStorage.getItem(SETTINGS_KEY, s -> {
            if (s == null) {
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

            if (loginResponse.statusCode() > 399) {
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
            WebStorage.setItem(SETTINGS_KEY, new ObjectMapper().writeValueAsString(localStorageSettings));
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
        if (rd.name() == null || rd.name().isEmpty()) {
            Notification.show("Name must be set!");
            return;
        }
        getLocalStorageSettings().getSavedDetails().put(rd.name(), rd);
        getLocalStorageSettings().setLastReservationDetails(rd);
        persistLocalStorageSettings();
    }

    public LocalStorageSettings getLocalStorageSettings() {
        if (localStorageSettings == null) {
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

        HttpEntity entity = MultipartEntityBuilder.create()
                .setCharset(StandardCharsets.US_ASCII)
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addTextBody("add-to-cart", "35") // 35 is the product id for a ferry trip ??
                .addTextBody("finferries-line", fr.id())
                .addTextBody("finferries-passenger-count", rd.passengerCount() + "")
                .addTextBody("finferries-harbor-from", from.id() + "")
                .addTextBody("finferries-harbor-to", to.id() + "")
                .addTextBody("finferries-departure-date", yyyyMMdd.format(localDate))
                .addTextBody("finferries-departure-time", tour.start().truncatedTo(ChronoUnit.MINUTES).toString())
                .addTextBody("finferries-vehicle-type", rd.vehicleType().id() + "")
                .addTextBody("finferries-pets", "false")
                .addTextBody("finferries-dangerous-goods", "false")
                .addTextBody("finferries-assistant", "false")
                .addTextBody("finferries-animal-transport", "false")
                .addTextBody("finferries-additional-comments", rd.comments())
                .addTextBody("finferries-restaurant-count", "0")
                .addTextBody("finferries-vessel", tour.vesselId())
                .build();

        try {
            String string = IOUtils.toString(entity.getContent());
            System.out.println(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Header contentType = entity.getContentType();

        try {
            HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://booking.finferries.fi/checkout-fi/"))
                    .header("Referer", "https://booking.finferries.fi/?from=%s&to=%s".formatted(from.id(), to.id()))
                    .header(contentType.getName(), contentType.getValue())
                    .POST(HttpRequest.BodyPublishers.ofInputStream(() -> {
                        try {
                            return entity.getContent();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }))
                    .build(), HttpResponse.BodyHandlers.ofString());
            response.previousResponse().ifPresent(r -> {
                // String body = r.body();
                // System.out.println(r.statusCode());

            });
            String bodyHtml = response.body();
            // Only needed for XHR confirmation, not needed for form submit
            // Extract security nonce like this from the body: "update_order_review_nonce":"e9ec9c0cb3"
            // String securityNonce = StringUtils.substringBetween(bodyHtml, "\"update_order_review_nonce\":\"", "\"");

            FormElement formElement1 = Jsoup.parse(bodyHtml).forms().get(0);
            formElement1.selectXpath("//input[@name='terms']").attr("checked", "checked");
            List<Connection.KeyVal> keyVals = formElement1.formData();

            // https://booking.finferries.fi/?wc-ajax=update_order_review // new nonce?
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                    .setCharset(StandardCharsets.US_ASCII)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            for (Connection.KeyVal keyVal : keyVals) {
                multipartEntityBuilder.addTextBody(keyVal.key(), keyVal.value());
            }
            HttpEntity entity1 = multipartEntityBuilder.build();
            Header contentType1 = entity1.getContentType();

            HttpResponse<String> response1 = getClient().send(HttpRequest.newBuilder()
                    .uri(URI.create("https://booking.finferries.fi/?wc-ajax=checkout"))
                    .header(contentType1.getName(), contentType1.getValue())
                    .POST(HttpRequest.BodyPublishers.ofInputStream(() -> {
                        try {
                            return entity1.getContent();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }))
                    .build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ContentBody toStringBody(String str) {
        return new StringBody(str, ContentType.TEXT_PLAIN);
    }

    public void reload() {
        client = buildHttpClient();
        username = null;
        localStorageSettings = null;
        UI.getCurrent().navigate(MainView.class);
    }

    public void saveLastTrip(FerryRoute value, Harbor from, Harbor to) {
        getLocalStorageSettings().setLastRouteId(value.id());
        // Note, the order is reversed here on purpose, you'll probably want to go back to the same route next
        getLocalStorageSettings().setLastHarborFromId(to.id());
        getLocalStorageSettings().setLastHarborToId(from.id());
        persistLocalStorageSettings();
    }
}
