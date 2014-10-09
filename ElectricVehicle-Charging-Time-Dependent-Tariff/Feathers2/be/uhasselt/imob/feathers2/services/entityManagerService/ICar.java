/**
 * 
 */
package be.uhasselt.imob.feathers2.services.entityManagerService;

import java.util.ArrayList;
import java.util.BitSet;

import be.uhasselt.imob.feathers2.services.entityManagerService.IBEV.FeasibleType;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.production.PowerSupplyManager;


/**
 * @author MuhammadUsman
 *
 */
public interface ICar
{

	// ===================================================================
	/** Car type based on energy providing subsystem. */
	public enum CarType { /** Battery only Electric Vehicle */ BEV, /** Internal Combustion Engine Vehicle */ ICEV, /** Pluggable Hybrid Electric Vehicle */ PHEV };



	// ===================================================================
	/** Return car type. */
	public abstract CarType carType();

	// ===================================================================
	/** Effective range [km] when starting with full energy (whatever type is applicable) charged, driven by the most heavy user of the car on the most frequently used routes and under most probable environmental conditions. */
	public abstract float effRange();
	/**
	 * 
	 * @return charging cost
	 */
	public double getBasicCost();


	/** @return Distance specific consumption [kWh/km] for electric vehicle.	 */
	public float getEvDistSpecifConsum();

	/** Indicates whether or not the schedule can be driven using a BEV using
    the given chargeOpportunity set. 
    @param bitSet set of activityTypes: this defines the locations where charging is possible. Note that it is not possible to determine charge opportunities by enumerating specific locations.
    @param arrayList List of activities.
	 */
	public boolean is_BEV_feasible(BitSet bitSet, ArrayList<IActivity> arrayList);



	// ===================================================================
	/** Official published range [km] when starting with full energy (whatever type is applicable) charged. */
	public abstract float pubRange();
	/**register the charging slots array for the BEV car*/
	public void registerChargingSlotsArray(IChargingSlot[] chSlotDuplicatArray);

	/**
	 * 
	 * @param calculateChargingPriceForCompleteSchedule
	 */
	public void setBasicCost(double calculateChargingPriceForCompleteSchedule);

	/**
	 * calculates the total energy required to charge for all car trips in the schedule
	 * @param ind index [0, size-1] of activity in schedule
	 * @param sched ArrayList of IActivity
	 * @return total Required Energy [float]
	 */
	public double getEnergyToChargeToTravelByCar(int ind, ArrayList<IActivity> sched);

	// ===================================================================
	/**
	 * 
	 * @param powerCapManager
	 */
	public void registerPowerCapManager(IPowerSupplyManager powerCapManager);

	// ===================================================================
	/**
	 * maximum power of available switch to a specific person at any location of his schedule
	 * @return power [KW] of switch 
	 */
	public float getMaxSwitchPower();

	// ===================================================================
	/**
	 * 
	 */
	public void reset();
	// ===================================================================
	/**
	 * 
	 * @param pCnt personCountID
	 * @param sched ArrayList of Activities
	 * @param itr Iteration number 
	 * @return if car can find an optimized charging strategy
	 */
	public FeasibleType analyseBatChargConsum(int pCnt, ISchedule sched, int itr);

	public abstract boolean isCostOptimal(int pc, ArrayList<IActivity> sched, int[] sorted);

	public abstract ICar clone();

	public abstract double getTotalCharging();




}
