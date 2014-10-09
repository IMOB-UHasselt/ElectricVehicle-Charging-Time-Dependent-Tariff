/**
 * 
 */
package be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.production;

import java.util.ArrayList;

import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerGridStation;

/**
 * This class serves the definition of local power grid station. Power grid station has the following attributes.
 * <ul>
 * 	<li>Name of the station</li>
 *  <li>Maximum power it can deliver at any moment in time</li>
 *  <li>Price it charges at each unit of time</li>
 *  <li>Total load at any unit of the time, sum of power demand from all zones</li>
 *  <li>List of Zones where it feeds the power</li>
 * </ul>
 * @author MuhammadUsman
 *
 */
public class PowerGridStation implements IPowerGridStation
{
//===============================================================
	/**Name of the station*/
	private String name;
	/**Maximum power it can deliver*/
	private float maxPowerCap;
	/***Price it charges at each unit of time*/
	private float[] price;
	/**Total load at any unit of the time*/
	private float[] load;
	/**List of Zones where it feeds the power*/
	private ArrayList<Integer> zones;
	private int nPrd = WidrsConstants.NUMBER_OF_PERIODS_IN_ONE_DAY;
	//===============================================================
	/**
	 * Constructor with Fields.
	 * @param name
	 * @param maxPowerCap
	 * @param price
	 * @param load
	 * @param zones
	 */
    public PowerGridStation(String name, float maxPowerCap, float[] price, float[] load, ArrayList<Integer> zones)
    {
	    this.name = name;
	    this.maxPowerCap = maxPowerCap;
	    this.price = price;
	    this.load = load;
	    this.zones = zones;
    }
	//===============================================================

	/**
	 * Default Constructor. 
	 * @param maxCap 
	 * @param name 
	 */
    public PowerGridStation(String name, float maxCap)
    {
	    this.name = name;
	    this.maxPowerCap = maxCap;
	    this.zones = new ArrayList<Integer>();
	    load = new float[nPrd];
    }


	//===============================================================
	public PowerGridStation(PowerGridStation pgs)
    {
		name = pgs.name;
		maxPowerCap = pgs.maxPowerCap;
		price = new float[pgs.price.length];
		int  i = 0;
		for(float f : pgs.price) 
		{
			price[i] = f; i++;
		}
		
		i = 0;
		load = new float[pgs.load.length];
		for(float l : pgs.load)
		{
			load[i] = l; i++;
		}
		
		i = 0;
		zones = new ArrayList<Integer>();
		for(int z : pgs.zones)
		{
			zones.add(z);
		}
		
    }

	//===============================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.IPowerGridStation#getName()
	 */
	@Override
    public String getName()
	{
		return name;
	}
	//===============================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.IPowerGridStation#setName(java.lang.String)
	 */
	@Override
    public void setName(String name)
	{
		this.name = name;
	}
	//===============================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.IPowerGridStation#getMaxPowerCap()
	 */
	@Override
    public float getMaxPowerCap()
	{
		return maxPowerCap;
	}
	//===============================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.IPowerGridStation#setMaxPowerCap(double)
	 */
	@Override
    public void setMaxPowerCap(float maxPowerCap)
	{
		this.maxPowerCap = maxPowerCap;
	}
	//===============================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.IPowerGridStation#getPrice()
	 */
	@Override
    public float[] getPrice()
	{
		return price;
	}
	//===============================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.IPowerGridStation#setPrice(double[])
	 */
	@Override
    public void setPrice(float[] price)
	{
		this.price = price;
	}
	//===============================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.IPowerGridStation#getLoad()
	 */
	@Override
    public float[] getLoad()
	{
		return load;
	}
	//===============================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.IPowerGridStation#setLoad(double[])
	 */
	@Override
    public void setLoad(float[] load)
	{
		this.load = load;
	}
	//===============================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.IPowerGridStation#getZones()
	 */
	@Override
    public ArrayList<Integer> getZones()
	{
		return zones;
	}
	//===============================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.IPowerGridStation#setZones(int[])
	 */
	@Override
    public void setZones(ArrayList<Integer> zones)
	{
		this.zones = zones;
	}
	//===============================================================

	public void addZone(int zID)
    {
		zones.add(zID);	    
    }

	public void setConsumedPower(int time, float conPower)
    {
	    load[time] = conPower;
    }

	public PowerGridStation getDuplicate()
    {
		PowerGridStation pgs = new PowerGridStation(this);
	    return pgs;
    }

	/**
	 * returns how much power can be beared more at particular time.
	 * @param time
	 * @return
	 */
	public double getAvailablePower(int time)
    {
		if(maxPowerCap - load[time] < 0.005)
			load[time] = maxPowerCap;
	    return maxPowerCap - load[time];
    }

	public void addLoad(int timePeriod, double usedPower)
    {
		load[timePeriod] += usedPower;
    }

	public void updateGridStationonsumption(PowerGridStation pgs)
    {
		int i = 0;
		for(float l : pgs.load)
		{
			load[i] = l; i++;
		}
    }

	public boolean isBlocked(int time)
    {
		boolean blocked = maxPowerCap - load[time] < 0.005;
	    return blocked;
    }
	
	
	
}
