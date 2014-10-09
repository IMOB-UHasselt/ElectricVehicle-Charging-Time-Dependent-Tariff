/**
 * 
 */
package be.uhasselt.imob.feathers2.services.entityManagerService;

import java.io.IOException;
import java.util.ArrayList;

import com.csvreader.CsvWriter;

import be.uhasselt.imob.feathers2.services.environmentService.IIncident;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.Activity;


/**
 * @author MuhammadUsman
 *
 */
public interface IActivity 
{
	public boolean activeAt(double firstAwareAt);
	public boolean containsTime(double timeBoundary);
	public int getA_activityCounter();
	public short getA_activityType();
	public double getA_beginningTime_minOfDay();
	public double getA_beginningTimeF0_minOfDay();
	public double getA_duration();
	public double getA_durationF0();
	public short getA_location();
	public double getAlpha();
	public double getDistanceTraveledInPeriod(int curPrd, int prdLen);
	public IActivity getDuplicate();
	/**
	 * @return First NSE period associated with the activity start time(offset for first NSE period overlapped by activity)
	 */	
	public int getFirstAssoccNSEprd();
	/**
	 * @return First NSE period associated with the activity travel start time(offset for first NSE period overlapped by activity)
	 */	
	public int getFirstAssoccNSEprdWithJourney();
	public double getJ_beginningTime();
	public double getJ_beginningTimeF0();
	public double getJ_distance();
	public double getJ_duration();
	public short getJ_transportMode();
	/**
	 * return  the value of k
	 * @return float value of k
	 */
	public float getK();
	/**
	 * @return Last NSE period associated with the activity (offset for last NSE period overlapped by activity)
	 */
	public int getLastAssoccNSEprd();
	public int getNotifTime();
	public double getTripFractionCompleted();
	public boolean isAlphaUninitialized();
	public boolean precedes(double firstAwareAt);
	public void registerIncidentNotif(int notifTime, IIncident incident);
	public void setA_beginningTime_minOfDay(double d);
	public void setA_beginningTimeF0_f0Tod(short a_beginningTime);
	public void setA_duration(double d);
	public void setAlpha(double d);
	public void setJ_duration(double j_duration);
	/**
	 * set the value of k
	 * @param k value of type float
	 */
	public void setK(float k);
	public void setTripFractionCompleted(double tripFractionCompleted);
	public boolean travelingAt(double timeBoundary);
	
	public double tripDurationFromIncident(double travelDur, double travelDur2);
	
	public boolean tripFollows(double t1);
	public boolean tripPrecedes(double t0);
	public void writeValues(CsvWriter writer) throws IOException;
	/**
	 * if location of activity lies in list of subzones id
	 * @param zones
	 */
	public boolean hasLocationInZonesList(ArrayList<Short> zones);
	/**
	 * Redefine activity duration, beginning times. activity end time is kept constant while starting time is shifted to add/minus the given duration. 
	 * @param dur duration to add/minus 
	 */
	public void redefActDuration(double dur);
	/**
	 * This function explicitly sets the activity duration to zero. 
	 * in regular setA_duration function it is not allowed to set the activity duration to zero.
	 */
	public void setA_durationToZero();
	/**
	 * creates the clone of the IActivity object
	 * @return IActivity
	 */
	public IActivity clone();
	/**
	 * calculates the utility of the activity as a function of activity duration, alpha, and k values. see document "Agent-based Carpooling Model" for details 
	 * @return utility value of the activity
	 */
	public double getUtility();
}
