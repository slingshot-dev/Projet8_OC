package tourGuide;

import Modeles.Attraction;
import Modeles.VisitedLocation;
import org.junit.Test;
import tourGuide.controlers.GpsUtilController;
import tourGuide.controlers.RewardCentralController;
import tourGuide.controlers.TripPricerController;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.*;
import tourGuide.Modeles.User;
import tourGuide.Modeles.UserReward;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRewardsService {

	@Test
	public void userGetRewards() throws IOException {
		Locale.setDefault(Locale.US);

		GpsUtilController gpsUtilController = new GpsUtilController();
		TripPricerController tripPricerController = new TripPricerController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtilController.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void isWithinAttractionProximity() throws IOException {
		GpsUtilController gpsUtilController = new GpsUtilController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		Attraction attraction = gpsUtilController.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	// Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() throws IOException {
		GpsUtilController gpsUtilController = new GpsUtilController();
		TripPricerController tripPricerController = new TripPricerController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);


		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));

		tourGuideService.tracker.stopTracking();
		assertEquals(gpsUtilController.getAttractions().size(), userRewards.size());
	}
	
}
