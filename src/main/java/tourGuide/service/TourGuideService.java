package tourGuide.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import Modeles.Attraction;
import Modeles.Location;
import Modeles.Provider;
import Modeles.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.Modeles.*;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;

/**
 * Service permettant de :
 * - Recuperer les 5 attractions les plus proches
 * - Recuperer les Rewards de l'utilisateur
 * - Recuperer les Localisations de l'utilisateur
 * - Recuperer tous les utilisateurs
 */

@Service
public class TourGuideService {
	private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtilService gpsUtilService;
	private final RewardsService rewardsService;
	public final Tracker tracker;
	boolean testMode = true;
	ExecutorService executorService = Executors.newFixedThreadPool(5000);


	@Autowired
	private final TripPricerService tripPricer;

	public TourGuideService(GpsUtilService gpsUtilService, RewardsService rewardsService, TripPricerService tripPricer) {
		this.gpsUtilService = gpsUtilService;
		this.rewardsService = rewardsService;
		this.tripPricer = tripPricer;


		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocation getUserLocation(User user) throws IOException {
		return (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return new ArrayList<>(internalUserMap.values());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) throws IOException {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public void asynchroneTrackUserLocation(User user) {

		Runnable runnableTask = () -> {
			try {
				trackUserLocation(user);
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		executorService.execute(runnableTask);
		}


	public void asynchroneFinaliseExecutor() throws InterruptedException {
		executorService.shutdown();
		executorService.awaitTermination(250, TimeUnit.SECONDS);
		}



	public VisitedLocation trackUserLocation(User user) throws IOException {
				VisitedLocation visitedLocation = gpsUtilService.getUserLocation(user.getUserId());
				user.addToVisitedLocations(visitedLocation);
				rewardsService.calculateRewards(user);
		return visitedLocation;
	}

		public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) throws IOException {
		List<Attraction> nearbyAttractions = new ArrayList<>();

		for(Attraction attraction : gpsUtilService.getAttractions()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		return nearbyAttractions;
	}


	public List<AttractionsFromUser> getFiveAttrations(VisitedLocation visitedLocation, User user) throws IOException {

		gpsUtilService.getAttractions();

		List<Attraction> attractions = gpsUtilService.getAttractions();
		List<AttractionsFromUser> allAttractions = attractions.parallelStream().map(a -> {
					try {
						return new AttractionsFromUser(a.attractionName, a.latitude, a.longitude, visitedLocation.location,
								rewardsService.getDistance(a, visitedLocation.location),
								rewardsService.getRewardPoints(a, user));
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				})
				.sorted(Comparator.comparingDouble(attractionsFromUser -> attractionsFromUser != null ? attractionsFromUser.getDistance() : 0))
				.collect(Collectors.toList());

		return allAttractions.stream().limit(user.getUserPreferences().getNumberOfAttractions()).collect(Collectors.toList());
	}


	public List<UserLocation> getUserLocation(){

		List<UserLocation> userLocations = new ArrayList<>();
		List<User> userList = getAllUsers();
		for (User user : userList) {

			VisitedLocation lastVisitedLocation = user.getLastVisitedLocation();
			Location location = new Location(lastVisitedLocation.location.latitude,lastVisitedLocation.location.longitude);
			UserLocation userLocation = new UserLocation(user.getUserId(),location);
			userLocations.add(userLocation);
		}
		return userLocations;
	}

	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}




	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
	
}
