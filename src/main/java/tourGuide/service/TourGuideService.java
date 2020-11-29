package tourGuide.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import Modeles.Attraction;
import Modeles.Location;
import Modeles.Provider;
import Modeles.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.Modeles.AttractionsFromUser;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserLocation;
import tourGuide.user.UserReward;


@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
//	private final TripPricer tripPricer = new TripPricer();
	private final TripPricerService tripPricer;
	public final Tracker tracker;
	boolean testMode = true;



	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService, TripPricerService tripPricer) {
		this.gpsUtil = gpsUtil;
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
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) throws IOException {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List <Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public void AsynchroneTrackUserLocation(User user) throws IOException {
		ExecutorService executorService = Executors.newFixedThreadPool(32);

				executorService.submit(() -> {
					try {
						trackUserLocation(user);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
		}




	public VisitedLocation trackUserLocation(User user) throws IOException {

/*		ExecutorService executorService = Executors.newFixedThreadPool(32);
		VisitedLocation visitedLocation2 = new VisitedLocation();

		executorService.submit(() -> {
			try {*/
				System.out.println("debut tache " + Thread.currentThread().getName());

				logger.debug("start get User Location");
				VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
				logger.debug("Add Visited Location");
				user.addToVisitedLocations(visitedLocation);
				logger.debug("Calculate Rewards");
				rewardsService.calculateRewards(user);
/*			} catch (IOException e) {
				e.printStackTrace();
			}
		});*/
		return visitedLocation;
	}

		public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) throws IOException {
		List<Attraction> nearbyAttractions = new ArrayList<>();

		for(Attraction attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		
		return nearbyAttractions;
	}


	public List<AttractionsFromUser> getFiveAttrations(VisitedLocation visitedLocation, User user) throws IOException {

		List<Attraction> attractions = gpsUtil.getAttractions();
		List<AttractionsFromUser> allAttractions = attractions.parallelStream()
				.map(a -> {
					try {
						return new AttractionsFromUser(a.attractionName, a.latitude, a.longitude, visitedLocation.location,
								rewardsService.getDistance(a, visitedLocation.location),
								rewardsService.getRewardPoints(a, user));
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				})
				.sorted(Comparator.comparingDouble(AttractionsFromUser::getDistance))
				.collect(Collectors.toList());

		List<AttractionsFromUser> closestAttractions = allAttractions.stream().
				limit(user.getUserPreferences().getNumberOfAttractions()).collect(Collectors.toList());

		return closestAttractions;

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
