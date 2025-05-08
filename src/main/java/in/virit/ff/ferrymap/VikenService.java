package in.virit.ff.ferrymap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttWebSocketConfig;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.lifecycle.MqttClientAutoReconnect;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import jakarta.annotation.PreDestroy;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class VikenService {

    private final Mqtt5Client client;

    LinkedList<VesselData> lastStatuses = new LinkedList<>();

    GeometryFactory gf = new GeometryFactory();

    Set<Consumer<VesselData>> listeners = new HashSet<>();

    public VikenService() {

        //String wssUrl = "wss://meri.digitraffic.fi/mqtt";
        client = MqttClient.builder()
                .automaticReconnect(MqttClientAutoReconnect.builder().build())
                .identifier(UUID.randomUUID().toString())
                .useMqttVersion5()
                .serverHost("meri.digitraffic.fi")
                .serverPort(443)
                .sslWithDefaultConfig()
                .webSocketConfig(MqttWebSocketConfig.builder()
                        .serverPath("mqtt").build())
                .build();

        Mqtt5BlockingClient c = client.toBlocking();
        Mqtt5ConnAck connect = c.connect();
        Mqtt5SubAck send = c.subscribeWith()
                .topicFilter("vessels-v2/230987260/location")
                .qos(MqttQos.AT_LEAST_ONCE)
                .send();

        var mqtt5AsyncClient = c.toAsync();

        ObjectMapper om = new ObjectMapper();

        mqtt5AsyncClient.subscribe(Mqtt5Subscribe.builder()
                .topicFilter("vessels-v2/230987260/location")
                .qos(MqttQos.EXACTLY_ONCE)
                .build(),  mqtt5Publish -> {

            ByteBuffer payload = mqtt5Publish.getPayload().get();
            byte[] arr = new byte[payload.remaining()];
            payload.get(arr);
            try {
                var lastStatus = om.readValue(arr, VesselData.class);
                listeners.forEach((Consumer<VesselData> l) -> l.accept(lastStatus));
                // A bit of history is maintained for new subscribers
                lastStatuses.add(lastStatus);
                if(lastStatuses.size() > 10) {
                    lastStatuses.removeFirst();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @PreDestroy
    void cleanup() {
        client.toBlocking().disconnect();
    }

    public void registerListener(Consumer<VesselData> listener) {
        lastStatuses.forEach(s -> listener.accept(s));
        listeners.add(listener);
    }

    public void unregisterListener(Consumer<VesselData> listener) {
        listeners.remove(listener);
    }

}
