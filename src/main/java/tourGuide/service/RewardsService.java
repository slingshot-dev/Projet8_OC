package tourGuide.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import Modeles.Attraction;
import Modeles.Location;
import Modeles.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import static org.junit.Assert.assertTrue;

@Service
public class RewardsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private Logger logger = LoggerFactory.getLogger(RewardsService.class);

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 20000;
	private final tourGuide.service.GpsUtil gpsUtil;
	private final tourGuide.service.RewardCentral rewardsCentral;
	ExecutorService executorService = Executors.newFixedThreadPool(1000);

	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardsCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardsCentral;
	}


	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void setAttractionProximityRange(int attractionProximityRange) {
		this.attractionProximityRange = attractionProximityRange;
	}


	public Future AsynchoneCalculateRewards(User user) throws IOException, ExecutionException, InterruptedException {

		ExecutorService executorService = Executors.newFixedThreadPool(10000);

		Callable runnableTask = () -> {
			try {
				calculateRewards(user);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		};
		Future future = executorService.submit(runnableTask);

		return future;

	}


	public synchronized void calculateRewards2(User user) throws IOException {

	CopyOnWriteArrayList<Attraction> attractions = new CopyOnWriteArrayList<>();
	CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();

		attractions.addAll(gpsUtil.getAttractions());
		userLocations.addAll(user.getVisitedLocations());

		ExecutorService executorService = Executors.newFixedThreadPool(10000);

		userLocations.forEach(visitedLocation ->
		{
		attractions.forEach(attraction ->
		{
				if (user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if (nearAttraction(visitedLocation, attraction)) {
						try {
							user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		});
	});

}





	public synchronized void calculateRewardsok(User user) throws IOException {

		CopyOnWriteArrayList<Attraction> attractions = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();

		attractions.addAll(gpsUtil.getAttractions());
		userLocations.addAll(user.getVisitedLocations());


/*		logger.debug("test reward");

		ArrayList<Attraction> attractions = new ArrayList(gpsUtil.getAttractions());
		List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());*/

		for(VisitedLocation visitedLocation : userLocations) {
				for (Attraction attraction : attractions) {
					if (user.getUserRewards().stream().filter(r -> r.attraction.getAttractionName().equals(attraction.getAttractionName())).count() == 0) {
						if (nearAttraction(visitedLocation, attraction)) {
							try {
								user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
		}
	}


/*	public void AsynchroneCalculateRewards() throws IOException {

		Attraction attraction = gpsUtil.getAttractions().get(0);
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

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

	}*/


	public synchronized void calculateRewards(User user) throws IOException {
		CopyOnWriteArrayList<Attraction> attractions = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();
		attractions.addAll(gpsUtil.getAttractions());
		userLocations.addAll(user.getVisitedLocations());

		for (VisitedLocation visitedLocation : userLocations) {
			for (Attraction attraction : attractions) {
				if (user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if (nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				}
			}
		}
	}




	public boolean isWithinAttractionProximity(Attraction attraction, Location visitedLocation) {
		return getDistance(attraction, visitedLocation) < attractionProximityRange;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.getLocation()) < proximityBuffer;
	}
	
	int getRewardPoints(Attraction attraction, User user) throws IOException {
		return rewardsCentral.getAttractionRewardPoints(attraction.getAttractionId(), user.getUserId());
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
