package tourGuide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

/**
 * 	 TourGuide : TourGuide est une application Spring Boot et une pièce maîtresse du portfolio d'applications TripMaster. Elle permet aux utilisateurs de voir quelles sont les attractions touristiques à proximité et d’obtenir des réductions sur les séjours à l’hôtel ainsi que sur les billets de différents spectacles..
 * 	 @author C. Guillet
 * 	 @version 1.0 - Dec 2020
 */

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        SpringApplication.run(Application.class, args);
    }

}
