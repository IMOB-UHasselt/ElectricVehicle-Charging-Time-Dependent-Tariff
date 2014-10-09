package be.uhasselt.imob.feathers2.services.entityManagerService;

import java.util.ArrayList;

import be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.production.PowerGridStation;

public interface IPowerConsumTracker
{

	/**
	 * 
	 * @param timePeriod
	 * @param zone
	 * @param consumedPower
	 */
	void addUpConsumedpwr(int timePeriod, short zone, double consumedPower);

	/**
	 * get available power at time period and zone
	 * @param time
	 * @param zone
	 * @return
	 */
	double getAvailablePwr(int time, short zone);

	/**
	 *  returns if all of the power (Global or local capacity) is booked at the slot. 
	 * @param time
	 * @param zone
	 * @return
	 */
	boolean isBlocked(int time, short zone);

	/**
	 * returns the maximum power capacity at zone.
	 * @param t time slot
	 * @return
	 */
	float getMaxLocalPower(int t);
	
	/**
	 * returns the global maximum power at time period
	 * @param time
	 * @return
	 */
	float getMaxGlobalPower(int time);

	/**
	 * returns the global consumed power at time period
	 * @param time
	 * @return
	 */
	float getGlobalConsumedPower(int time);

	void setMaxGlobalAvailPower(float[] maxGlobalPower);

	void setGlobalConsumedPower(float[] conPower);
	
	void addStation(PowerGridStation station);

	/**
	 * Add the zone to the sgrid station
	 * @param stationNumber
	 * @param zone
	 */
	void addZoneToGrid(int stationNumber, int zone);

	ArrayList<PowerGridStation> getGridStation();

}
