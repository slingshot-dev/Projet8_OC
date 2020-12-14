package tourGuide;

import Modeles.Attraction;
import Modeles.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardCentralService;
import tourGuide.service.TripPricerService;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.*;
import tourGuide.Modeles.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;

public class TestPerformance {




	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */
	private Logger logger = LoggerFactory.getLogger(TestPerformance.class);

	@Test
	public void highVolumeTrackLocation() throws IOException, InterruptedException {

//		int nbProcs = Runtime.getRuntime().availableProcessors();

		Locale.setDefault(Locale.US);
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentralService());
		TripPricerService tripPricerService = new TripPricerService();
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(5000);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService, tripPricerService);

		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// Appel d'une methode Asynchone de TrackUserLocation qui appelle elle meme TrackUserLocation
		for(User user : allUsers) {
			tourGuideService.asynchroneTrackUserLocation(user);
		}

		// Appel d'une methode Asynchrone pour Executor Shutdown et AwaitTermination
		tourGuideService.asynchroneFinaliseExecutor();

		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
		for (User user : allUsers) {
			assertTrue(user.getVisitedLocations().size() > 3);
		}
	}




	@Test
	public void highVolumeGetRewards() throws IOException, InterruptedException {
		GpsUtilService gpsUtil = new GpsUtilService();
		TripPricerService tripPricerService = new TripPricerService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralService());

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		InternalTestHelper.setInternalUserNumber(100000);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, tripPricerService);

		Attraction attraction = gpsUtil.getAttractions().get(0);
		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();
		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

		for(User user : allUsers) {
			rewardsService.asynchroneCalculateRewards(user);
		}
		rewardsService.asynchroneFinaliseExecutor();

		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

	}


}
