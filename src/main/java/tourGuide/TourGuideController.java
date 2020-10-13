package tourGuide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import org.springframework.web.client.RestTemplate;
import tourGuide.Modeles.Attractions;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tripPricer.Provider;


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
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
    	return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation));
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
    	// TODO: Get a list of every user's most recent location as JSON
    	//- Note: does not use gpsUtil to query for their current location, 
    	//        but rather gathers the user's current location from their stored location history.
    	//
    	// Return object should be the just a JSON mapping of userId to Locations similar to:
    	//     {
    	//        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371} 
    	//        ...
    	//     }
    	
    	return JsonStream.serialize("");
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }


/*    @RequestMapping("/test")
    public ResponseEntity<List<Attractions>> test() {
        ResponseEntity<List<Attractions>> response;
        List<Attractions> Response2;
        try {
            response = restTemplate.exchange("http://localhost:8080/getAttractions", HttpMethod.GET, null, Attractions.class);
        } catch (HttpClientErrorException e) {
            throw e;
        }
        return response;
    }*/

    @RequestMapping("/test2")
    public List<Attractions> test2() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        String ResourceUrl = "http://localhost:8080/getAttractions";
        ResponseEntity<String> response = restTemplate.getForEntity(ResourceUrl, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), new TypeReference<List<Attractions>>(){});
    }



}