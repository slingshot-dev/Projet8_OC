package tourGuide.Modeles;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;

/**
 * Modele de données des preferences utilisateurs
 */

public class UserPreferences {
	
	private int attractionProximity; // = Integer.MAX_VALUE;
	private String currency; // = Monetary.getCurrency("USD");
	private Double lowerPricePoint; // = Money.of(0, currency);
	private Double highPricePoint; // = Money.of(Integer.MAX_VALUE, currency);
	private int tripDuration; // = 1;
	private int ticketQuantity; // = 1;
	private int numberOfAdults; // = 1;
	private int numberOfChildren; // = 0;
	private int numberOfAttractions; // = 5;
	
	public UserPreferences() {
	}

	public UserPreferences(int attractionProximity, String currency, Double lowerPricePoint, Double highPricePoint, int tripDuration, int ticketQuantity, int numberOfAdults, int numberOfChildren) {
		this.attractionProximity = attractionProximity;
		this.currency = currency;
		this.lowerPricePoint = lowerPricePoint;
		this.highPricePoint = highPricePoint;
		this.tripDuration = tripDuration;
		this.ticketQuantity = ticketQuantity;
		this.numberOfAdults = numberOfAdults;
		this.numberOfChildren = numberOfChildren;
	}

	public void setAttractionProximity(int attractionProximity) {
		this.attractionProximity = attractionProximity;
	}
	
	public int getAttractionProximity() {
		return attractionProximity;
	}
	
	public Double getLowerPricePoint() {
		return lowerPricePoint;
	}

	public void setLowerPricePoint(Double lowerPricePoint) {
		this.lowerPricePoint = lowerPricePoint;
	}

	public Double getHighPricePoint() {
		return highPricePoint;
	}

	public void setHighPricePoint(Double highPricePoint) {
		this.highPricePoint = highPricePoint;
	}
	
	public int getTripDuration() {
		return tripDuration;
	}

	public void setTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
	}

	public int getTicketQuantity() {
		return ticketQuantity;
	}

	public void setTicketQuantity(int ticketQuantity) {
		this.ticketQuantity = ticketQuantity;
	}
	
	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	public void setNumberOfAdults(int numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

	public int getNumberOfAttractions() {
		return numberOfAttractions;
	}

	public void setNumberOfAttractions(int numberOfAttractions) {
		this.numberOfAttractions = numberOfAttractions;
	}
}
