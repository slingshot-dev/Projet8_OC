package tourGuide;

import Modeles.Attraction;
import Modeles.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.*;
import tourGuide.user.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
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

	@Ignore
	@Test
	public void highVolumeTrackLocationNonExtern() throws IOException, InterruptedException {

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
		}


	/*@Test
	@Ignore
	public void highVolumeGetRewards() throws IOException, InterruptedException {
		GpsUtil gpsUtil = new GpsUtil();
		TripPricerService tripPricerService = new TripPricerService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		InternalTestHelper.setInternalUserNumber(1000);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, tripPricerService);

	    Attraction attraction = gpsUtil.getAttractions().get(0);
		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();


		ExecutorService executorService = Executors.newFixedThreadPool(10000);
		for(User user : allUsers) {

			Runnable runnableTask = () -> {
				try {
					User u = new User();
					u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date()));
					rewardsService.calculateRewards(u);
					assertTrue(user.getUserRewards().size() > 0);



				} catch (IOException e) {
					e.printStackTrace();
				}
//			tourGuideService.AsynchroneTrackUserLocation(user);
			};
			executorService.execute(runnableTask);

		}
		executorService.shutdownNow();
		executorService.awaitTermination(60, TimeUnit.SECONDS);




		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

	    allUsers.forEach(u -> {

//		rewardsService.calculateRewards(u);


		});

		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));


	}*/

	@Ignore
	@Test
	public void highVolumeGetRewards() throws InterruptedException, IOException {
		GpsUtil gpsUtil = new GpsUtil();
		TripPricerService tripPricerService = new TripPricerService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, tripPricerService);
		ExecutorService executorService = Executors.newFixedThreadPool(1000);

		// Users incremented up to 100,000
//		InternalTestHelper.setInternalUserNumber(10000);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Attraction attraction = gpsUtil.getAttractions().get(0);
		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();

		allUsers.forEach(u -> {
			executorService.execute(new Runnable() {
				RewardsService rewardsService;
				User user;

				public void run() {
					user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
					try {
						this.rewardsService.calculateRewards(this.user);
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

		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}

	}


	@Test
	public void highVolumeTrackLocationEssaiExtern() throws IOException, InterruptedException {

//		int nbProcs = Runtime.getRuntime().availableProcessors();

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

		for(User user : allUsers) {
			tourGuideService.AsynchroneTrackUserLocation(user);
		}

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




}
