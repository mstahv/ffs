package in.virit.ff;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.virit.ff.bookingdtos.FerryRoute;
import in.virit.ff.bookingdtos.Harbor;
import in.virit.ff.bookingdtos.Tour;
import in.virit.ff.bookingdtos.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScope
@Service
public class BookingService {

    @Autowired
    private Session session;

    List<VehicleType> vehicleTypeList;
    Map<String, List<Harbor>> routeIdToHarbors;

    public List<VehicleType> getVehicleTypes() {
        if (vehicleTypeList == null) {
            HttpClient client = session.getClient();
            try {
                HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                        .uri(URI.create("https://booking.finferries.fi/wp-json/wp/v2/ffr_vehicle_type?_fields=id%2Cname&lang=fi&per_page=100"))
                        .GET()
                        .build(), HttpResponse.BodyHandlers.ofString());

                String body = response.body();

                List<VehicleType> vehicleTypes = new ObjectMapper().readValue(body, new TypeReference<List<VehicleType>>() {
                });
                vehicleTypeList = vehicleTypes;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        return vehicleTypeList;
    }

    public List<Harbor> getHarbors(FerryRoute ferryRoute) {
        if(routeIdToHarbors == null) {
            HttpClient client = session.getClient();
            try {
                HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                        .uri(URI.create("https://booking.finferries.fi/wp-json/finferries/v1/harbor/list"))
                        .GET()
                        .build(), HttpResponse.BodyHandlers.ofString()
                );
                String body = response.body();
                routeIdToHarbors = new ObjectMapper().readValue(body, new TypeReference<Map<String, List<Harbor>>>(){});
            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }
        return routeIdToHarbors.get(ferryRoute.id());

    }

    private static DateTimeFormatter df =  DateTimeFormatter.ofPattern("yyyyMMdd");

    // https://booking.finferries.fi/wp-json/finferries/v1/tour/search?date=20240820&from=57&to=52
    public List<Tour> getTours(LocalDate date, Harbor from, Harbor to) {
        HttpClient client = session.getClient();

        try {
            HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                    .uri(new URI("https://booking.finferries.fi/wp-json/finferries/v1/tour/search?date=" + df.format(date) + "&from=" + from.id() + "&to=" + to.id()))
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString());

            JsonNode jsonNode = new ObjectMapper().readTree(response.body());

            /*
            {
	"2807": {
		"id": 2807,
		"name": "Parainen",
		"ship_name": "",
		"tours": [
			{
				"tour": [
					{
						"harbor": "52",
						"time": "07:45:00"
					},
					{
						"harbor": "57",
						"time": ""
					},
					{
						"harbor": "52",
						"time": "08:15:00"
					}
				],
				"vessel": {
					"id": "406",
					"name": "Viken"
				},
				"has_restaurant": false
			},
			{

             */

            List<Tour> availableTours = new ArrayList<>();

            Map.Entry<String, JsonNode> answer = jsonNode.fields().next();
            JsonNode tours = answer.getValue().get("tours");
            for(int i = 0; i< tours.size(); i++) {
                JsonNode tour = tours.get(i);
                JsonNode fromNode = tour.get("tour").get(0);
                String harborId = fromNode.get("harbor").asText();
                String time = fromNode.get("time").asText();
                LocalTime localTime = LocalTime.parse(time);
                String vesselName = tour.get("vessel").get("name").asText();
                String vesselId = tour.get("vessel").get("id").asText();
                String harbor = answer.getValue().get("harbors").get(harborId).get("name").asText();
                availableTours.add(new Tour(localTime,vesselId,vesselName, harbor));
            };
            return availableTours;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    Clock clock = Clock.system(ZoneId.of("Europe/Helsinki"));

    public LocalDateTime nowFinland() {
        return LocalDateTime.now(clock);
    }

}
