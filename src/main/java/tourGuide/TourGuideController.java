package tourGuide;

import java.io.IOException;
import java.util.List;

import Modeles.Provider;
import Modeles.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jsoniter.output.JsonStream;
import org.springframework.web.client.RestTemplate;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserLocation;



@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;
    @Autowired
    RestTemplate restTemplate;


    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }


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

    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) throws IOException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(tourGuideService.getFiveAttrations(visitedLocation, getUser(userName)));
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

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

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws IOException {
        List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }

}