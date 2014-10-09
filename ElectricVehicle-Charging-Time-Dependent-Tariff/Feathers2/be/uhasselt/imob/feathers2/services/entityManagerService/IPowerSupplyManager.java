/**
 * 
 */
package be.uhasselt.imob.feathers2.services.entityManagerService;





/**
 * @author MuhammadUsman
 *
 */
public interface IPowerSupplyManager 
{
	public IPowerConsumTracker getDuplicateTracker();
	public void updatePowerTracker(IPowerConsumTracker pcTracker);
	public void writeAvailablePowerGraphData();
	
	/**
	 * return the price of electric charge at each slot
	 * @return price array
	 */
	public float[] getPriceArray();
	
	/**
	 * returns the price of electric charge at any particular period of the day
	 * @param prd period of day 
	 * @return price at give period
	 */
	public float getPriceAtPrd(int prd);
	/**
	 * depending upon the available energy it calculates the degree of parallelism. dop = min(P[t])/maxSwP
	 * @param maxSwPower [KW]
	 * @return degree of parallelism
	 */
	public int getDOP(float maxSwPower);
	/**
	 * reset the consumed power at each slot to zero.
	 */
	public void resetPowerConsuption();
	public void preparePriceAndCapacityData();
	
	public String priceFileName();
	public String priceDir();
	int[] getSortedIndices();
	/**
	 * if power is consumed completely globally
	 * @param prd
	 * @return
	 */
	public boolean isBlocked(int prd);
	
}
