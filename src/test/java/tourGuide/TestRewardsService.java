package tourGuide;

import Modeles.Attraction;
import Modeles.VisitedLocation;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardCentralService;
import tourGuide.service.TripPricerService;
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

@SpringBootTest
public class TestRewardsService {

	@Test
	public void userGetRewards() throws IOException {

		// Arrange
		Locale.setDefault(Locale.US);

		GpsUtilService gpsUtilService = new GpsUtilService();
		TripPricerService tripPricerService = new TripPricerService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentralService());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService, tripPricerService);
//		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService);

		// Act
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtilService.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();

		// Assert
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void isWithinAttractionProximity() throws IOException {
		// Arrange
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentralService());
		// Act
		Attraction attraction = gpsUtilService.getAttractions().get(0);
		// Assert
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	@Test
	public void nearAllAttractions() throws IOException {

		// Arrange
		GpsUtilService gpsUtilService = new GpsUtilService();
		TripPricerService tripPricerService = new TripPricerService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentralService());
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService, tripPricerService);
		tourGuideService.tracker.stopTracking();

		// Act
		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));

		// Assert
		assertEquals(gpsUtilService.getAttractions().size(), userRewards.size());
	}
	
}
