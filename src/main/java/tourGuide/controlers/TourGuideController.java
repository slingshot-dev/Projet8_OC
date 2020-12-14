package tourGuide.controlers;

import java.io.IOException;
import java.util.List;

import Modeles.Provider;
import Modeles.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jsoniter.output.JsonStream;
import org.springframework.web.client.RestTemplate;
import tourGuide.Modeles.UserPreferences;
import tourGuide.service.TourGuideService;
import tourGuide.Modeles.User;
import tourGuide.Modeles.UserLocation;

/**
 * Controller TourGuide permettant d'exposer les URL aux utilisateurs
 */

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;
    @Autowired
    RestTemplate restTemplate;


    /**
     * Controller page principale
     * @return : Retour page d'acceuil
     */
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }


    /**
     * Controller permet de recuperer la Localisation de l'utilisateur
     * @param userName : nom de l'utilisateur
     * @return : Retourne les infos de localisation
     * @throws IOException : Exception
     */
    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) throws IOException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    //  TODO: Change this method to no longer return a List of Attractions. : Done
    //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
    //  Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the attractions.
    // The reward points for visiting each Attraction.
    //    Note: Attraction reward points can be gathered from RewardsCentral

    /**
     * Controller permettant de recuperer les 5 plus proches attractions
     * @param userName : Nom de l'utilisateur
     * @return : Retourn les 5 plus proches Attractions
     * @throws IOException : Exception
     */
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) throws IOException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(tourGuideService.getFiveAttrations(visitedLocation, getUser(userName)));
    }

    /**
     * Controller permettant de recuperer les Rewards de l'Utilisateur
     * @param userName : Nom de l'utilisateur
     * @return : Retourne le Rewards de l'utilisateur
     */
    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    /**
     * Controller permettant de recuperer toutes les Localisation
     * @return : Retourne toutes les Localisations de tous les utilisateurs
     */
    @RequestMapping("/getAllCurrentLocations")
    public List<UserLocation> getAllCurrentLocations() {
        // TODO: Get a list of every user's most recent location as JSON : Done
        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the user's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }

/*        List<UserLocation> result = tourGuideService.getUserLocation();
        return JsonStream.serialize(result);*/
        return tourGuideService.getUserLocation();
    }

    /**
     * Controller permettant de recuperer les offres de voyages
     * @param userName : Nom de l'utilisateur
     * @return : Retourne Les offres
     * @throws IOException : Exception
     */
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws IOException {
        List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }


    /**
     * Controller permettant de mettre a jour les preferences utilisateurs
     *
     * @param userName : Nom de l'utilisateur
     * @param attractionProximity : nbr Miles des attractions
     * @param higherPrice : Prix minimum
     * @param lowerPrice : Prix maximum
     * @param adults : Nbr d'Adultes
     * @param childrens : Nbr d'enfants
     * @param nbrAttractions : Nbr d'Attractions
     * @param ticketQuantity : Nbr de Ticket
     * @param tripDuration : Nbr de jour du voyage
     * @return : Retourne l'ensemble des preferences utilisateur
     */
    @PostMapping("/setPreferences")
    public UserPreferences setPreference(@RequestParam String userName, int attractionProximity, Double higherPrice, Double lowerPrice, int adults, int childrens, int nbrAttractions, int ticketQuantity, int tripDuration) {

        User user = getUser(userName);
        UserPreferences userPreferences = new UserPreferences();

        userPreferences.setAttractionProximity(attractionProximity);
        userPreferences.setHighPricePoint(higherPrice);
        userPreferences.setLowerPricePoint(lowerPrice);
        userPreferences.setNumberOfAdults(adults);
        userPreferences.setNumberOfChildren(childrens);
        userPreferences.setNumberOfAttractions(nbrAttractions);
        userPreferences.setTicketQuantity(ticketQuantity);
        userPreferences.setTripDuration(tripDuration);

        user.setUserPreferences(userPreferences);

        return userPreferences;
    }

}