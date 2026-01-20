package at.ac.tgm.gorunovic.warehouse1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Warehouse1Application {

    public static void main(String[] args) {
        SpringApplication.run(Warehouse1Application.class, args);
    }

}
