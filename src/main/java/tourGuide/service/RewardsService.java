package tourGuide.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

import Modeles.Attraction;
import Modeles.Location;
import Modeles.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.controlers.GpsUtilController;
import tourGuide.controlers.RewardCentralController;
import tourGuide.Modeles.User;
import tourGuide.Modeles.UserReward;


@Service
public class RewardsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private Logger logger = LoggerFactory.getLogger(RewardsService.class);

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtilController gpsUtilController;
	private final RewardCentralController rewardsCentral;
	private Integer nbAddReward = 0;


	public RewardsService(GpsUtilController gpsUtilController, RewardCentralController rewardsCentral) {
		this.gpsUtilController = gpsUtilController;
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

	ExecutorService executorService = Executors.newFixedThreadPool(5000);

	public void asynchroneCalculateRewards(User user){

			Runnable runnableTask = () -> {
				try {
					nonSynchCalculateRewards(user);
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
			executorService.submit(runnableTask);
		}


		public void asynchroneFinaliseExecutor() throws InterruptedException {
			executorService.shutdown();
			executorService.awaitTermination(250, TimeUnit.SECONDS);
		}



	public void calculateRewards(User user) throws IOException {
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtilController.getAttractions();

		synchronized (this) {
		for (VisitedLocation visitedLocation : userLocations){
			for (Attraction attraction : attractions) {
				if (user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
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
	}


	public void nonSynchCalculateRewards(User user) throws IOException {
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtilController.getAttractions();

			userLocations.forEach(visitedLocation -> {
				attractions.forEach(attraction->{
					if (user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
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



	public boolean isWithinAttractionProximity(Attraction attraction, Location visitedLocation) {
		return getDistance(attraction, visitedLocation) < attractionProximityRange;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.getLocation()) < proximityBuffer;
	}
	
	public int getRewardPoints(Attraction attraction, User user) throws IOException {
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
		return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
	}

}
