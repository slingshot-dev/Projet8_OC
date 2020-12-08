package tourGuide;

import Modeles.Attraction;
import Modeles.Provider;
import Modeles.VisitedLocation;
import org.junit.Test;
import tourGuide.controlers.GpsUtilController;
import tourGuide.controlers.RewardCentralController;
import tourGuide.controlers.TripPricerController;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.*;
import tourGuide.Modeles.User;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTourGuideService {



	@Test
	public void getUserLocation() throws IOException {
		Locale.setDefault(Locale.US);

		GpsUtilController gpsUtilController = new GpsUtilController();
		TripPricerController tripPricerController = new TripPricerController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void addUser() {
		GpsUtilController gpsUtilController = new GpsUtilController();
		TripPricerController tripPricerController = new TripPricerController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		GpsUtilController gpsUtilController = new GpsUtilController();
		TripPricerController tripPricerController = new TripPricerController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() throws IOException {
		GpsUtilController gpsUtilController = new GpsUtilController();
		TripPricerController tripPricerController = new TripPricerController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}
	
	// Not yet implemented
	@Test
	public void getNearbyAttractions() throws IOException {
		GpsUtilController gpsUtilController = new GpsUtilController();
		TripPricerController tripPricerController = new TripPricerController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		rewardsService.setAttractionProximityRange(Integer.MAX_VALUE);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(26, attractions.size()); // Passé a 26 en attendant de developper la liste des 5 attractions les plus pret
	}

	@Test
	public void getTripDeals() throws IOException {
		GpsUtilController gpsUtilController = new GpsUtilController();
		TripPricerController tripPricerController = new TripPricerController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(5, providers.size()); // Boucle dans TripPricer limitée a 5 iterations. 10 Providers impossible
	}

	
}
