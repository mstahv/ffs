package in.virit.ff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevModeDemoApplication extends Application {

    // This method should be used to start the application in development mode.
    // You could add other dev mode configs (like Testcontainers) to this class as well
    public static void main(String[] args) {
        SpringApplication.run(DevModeDemoApplication.class, args);
    }
}