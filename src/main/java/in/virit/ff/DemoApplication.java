package in.virit.ff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    // This method is for "production mode server" and might need a priming
    // build (mvn package) to be run directly.
    // Use DevModeDemoApplication during development to enable Livereload & Copilot
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}