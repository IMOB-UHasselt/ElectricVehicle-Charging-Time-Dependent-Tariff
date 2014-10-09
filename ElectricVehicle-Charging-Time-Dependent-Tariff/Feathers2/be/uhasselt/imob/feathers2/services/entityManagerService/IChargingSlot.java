package be.uhasselt.imob.feathers2.services.entityManagerService;

public interface IChargingSlot
{
	/**
	 * returns wether slot is marked planned during evaluation phase.
	 * @return is slot marked planned
	 */
	public boolean isLocalBlocked();
	/**
	 * slot is marked local blocked is energy is found cheaper at the slot but it is not usable due to local constraints i.e. already battery full. 
	 * @param set
	 */
	public void markLocalBlocked(boolean set);
	/**
	 * @return the isMarkedExtra
	 */
	public boolean isMarkedExtra();
	/**
	 * set the slot as an extra slot
	 * @param mark marker
	 */
	public void markExtra(boolean mark);
	
	/**
	 * reset the charging strategy. erase all charging stamps. 
	 */
	public void resetChargingStamp();
	/**
	 * maximum charging period at any particular slot is equal the the length of NSE_Period. a slot is marked as saturated if it is booked for complete period for charging 
	 * @return isSaturated
	 */
	public boolean isSaturated();
	/**
	 * 
	 * @param requestedChargingTime time for charging requested for a particular slot [min]
	 * @param maxPossChargingTime maximum possible charging time for the vehicle to be charged at the particular slot [min]
	 */
	public void updateSaturation(double requestedChargingTime, double maxPossChargingTime);
	/**
	 * @return the chargedEnergy
	 */
	public double getChargedEnergy();
	/**
	 * @param chargedEnergy the chargedEnergy to set
	 */
	public void setChargedEnergy(double chargedEnergy);
	public void reset();
	public boolean isChargingPlanned();
}