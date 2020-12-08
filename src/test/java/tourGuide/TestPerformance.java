package tourGuide;

import Modeles.Attraction;
import Modeles.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.controlers.GpsUtilController;
import tourGuide.controlers.RewardCentralController;
import tourGuide.controlers.TripPricerController;
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
		GpsUtilController gpsUtilController = new GpsUtilController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		TripPricerController tripPricerController = new TripPricerController();
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(10000);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);

		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// Appel d'une methode Asynchone de TrackUserLocation qui appelle elle meme TrackUserLocation
		for(User user : allUsers) {
			tourGuideService.AsynchroneTrackUserLocation(user);
		}

		// Appel d'une methode Asynchrone pour Executor Shutdown et AwaitTermination
		tourGuideService.AsynchroneFinaliseExecutor();

		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
		for (User user : allUsers) {
//			assertEquals(5, user.getVisitedLocations().size());
			assertTrue(user.getVisitedLocations().size() > 3);
		}
	}


	@Test
	public void highVolumeGetRewards() throws IOException, InterruptedException {
		GpsUtilController gpsUtilController = new GpsUtilController();
		TripPricerController tripPricerController = new TripPricerController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);
		ExecutorService executorService = Executors.newFixedThreadPool(10000);

		// Users incremented up to 100,000
		InternalTestHelper.setInternalUserNumber(10000);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Attraction attraction = gpsUtilController.getAttractions().get(0);
		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();

		allUsers.forEach(u -> {
			executorService.execute(new Runnable() {
				RewardsService rewardsService;
				User user;

				public void run() {
					user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
					try {
						this.rewardsService.AsynchroneCalculateRewards(this.user);
					} catch (IOException e) {
						e.printStackTrace();
					}
					assertTrue(user.getUserRewards().size() > 0);
					System.out.println("ok passed");
				}

				public Runnable init(RewardsService rewardsService, User user) {
					this.rewardsService = rewardsService;
					this.user = user;
					return this;
				}
			}.init(rewardsService, u));
		});

		executorService.shutdown();
		executorService.awaitTermination(20, TimeUnit.MINUTES);
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		//Assert
		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

		for (User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}

	}




	@Ignore
	@Test
	public void highVolumeGetRewardsCyG() throws InterruptedException, IOException {
		GpsUtilController gpsUtilController = new GpsUtilController();
		TripPricerController tripPricerController = new TripPricerController();
		RewardsService rewardsService = new RewardsService(gpsUtilController, new RewardCentralController());
		TourGuideService tourGuideService = new TourGuideService(gpsUtilController, rewardsService, tripPricerController);
		ExecutorService executorService = Executors.newFixedThreadPool(10000);

		// Users incremented up to 100,000
//		InternalTestHelper.setInternalUserNumber(10000);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Attraction attraction = gpsUtilController.getAttractions().get(0);
		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();




		allUsers.forEach(u -> {


			Runnable runnableTask = () -> {
				try {
					rewardsService.calculateRewards(u);

				} catch (IOException e) {
					e.printStackTrace();
				}
			};
			executorService.execute(runnableTask);

		});

		executorService.shutdown();
		executorService.awaitTermination(20, TimeUnit.MINUTES);
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		//Assert
		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}

	}


	/*	@Test
	public void highVolumeTrackLocation() throws IOException, InterruptedException {

		int nbProcs = Runtime.getRuntime().availableProcessors();

		Locale.setDefault(Locale.US);
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		TripPricerService tripPricerService = new TripPricerService();
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(10000);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, tripPricerService);

		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		ExecutorService executorService = Executors.newFixedThreadPool(10000);
		for(User user : allUsers) {

			Runnable runnableTask = () -> {
				try {
					tourGuideService.trackUserLocation(user);
				} catch (IOException e) {
					e.printStackTrace();
				}
//			tourGuideService.AsynchroneTrackUserLocation(user);
		};
			executorService.execute(runnableTask);

		}
		executorService.shutdownNow();
		executorService.awaitTermination(60, TimeUnit.SECONDS);

		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

			System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
			assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
			for (User user : allUsers) {
//				assertEquals(5, user.getVisitedLocations().size());
				assertTrue(user.getVisitedLocations().size() > 3);
			}
		}*/



}
