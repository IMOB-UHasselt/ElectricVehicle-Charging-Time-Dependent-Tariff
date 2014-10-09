/**
 * 
 */
package be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.production;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerConsumTracker;

/**
 * This is the global power tracker.
 * @author MuhammadUsman
 *
 */
class PowerConsumTracker implements IPowerConsumTracker
{
	private float[] maxGlobalPower;
	private float[] consumedPower;
	private ArrayList<PowerGridStation> stations; 
	private HashMap<Integer, PowerGridStation> zoneToGrid;
	private int nPrd = WidrsConstants.NUMBER_OF_PERIODS_IN_ONE_DAY;

	/**
	 * 
	 * @param maxGlobalPowerAvail
	 */
	public PowerConsumTracker()
	{
		maxGlobalPower = new float[nPrd];
		consumedPower = new float[nPrd];
		stations = new ArrayList<PowerGridStation>();
		zoneToGrid = new HashMap<Integer, PowerGridStation>();
	}


	public PowerConsumTracker(PowerConsumTracker pct)
	{
		maxGlobalPower = new float[nPrd];
		consumedPower = new float[nPrd];
		stations = new ArrayList<PowerGridStation>();
		zoneToGrid = new HashMap<Integer, PowerGridStation>();
		for (Integer key : pct.zoneToGrid.keySet())
		{
			zoneToGrid.put(key, pct.zoneToGrid.get(key));
		}
		

		for(int i = 0; i < nPrd; i++)
		{
			maxGlobalPower[i] = pct.maxGlobalPower[i];
			consumedPower[i] = pct.consumedPower[i];
		}

		for(int i = 0; i < pct.stations.size(); i++)
		{
			stations.add(pct.stations.get(i).getDuplicate());
		}
	}


	@Override
	public double getAvailablePwr(int time, short zone)
	{
		if(maxGlobalPower[time] - consumedPower[time]  < 0.005) 
		{
			consumedPower[time] = maxGlobalPower[time]; 
		} 

		double availLocalPwr = zoneToGrid.get((int)zone).getAvailablePower(time);

		double availGlobalPwr = maxGlobalPower[time] - consumedPower[time];
//		System.out.println("globalAvail=["+maxGlobalPower[time]+"], globalcon=["+consumedPower[time]+"], availLocalPwr=["+availLocalPwr+"], availGlobalPwr=["+availGlobalPwr+"]");

		return Math.min(availGlobalPwr, availLocalPwr);
	}



	@Override
	public float getMaxGlobalPower(int time)
	{
		return maxGlobalPower[time];
	}

	@Override
	public float getMaxLocalPower(int loc)
	{
		return zoneToGrid.get(loc).getMaxPowerCap();
	}

	@Override
	public float getGlobalConsumedPower(int time)
	{
		return consumedPower[time];
	}



	@Override
	public void setMaxGlobalAvailPower(float[] maxGlobalPower)
	{
		this.maxGlobalPower = maxGlobalPower;
	}


	@Override
	public void setGlobalConsumedPower(float[] conPower)
	{
		this.consumedPower = consumedPower;
	}


	@Override
	public void addStation(PowerGridStation station)
	{
		stations.add(station);
	}


	@Override
	public void addZoneToGrid(int stationNumber, int zone)
	{
		stations.get(stationNumber).addZone(zone);
		zoneToGrid.put(zone, stations.get(stationNumber));
	}






	@Override
	public ArrayList<PowerGridStation> getGridStation()
	{
		return stations;
	}


	@Override
	public boolean isBlocked(int time, short zone)
	{
		boolean blocked = false;
	
		blocked = isBlocked(time) && zoneToGrid.get((int)zone).isBlocked(time);

		return blocked;
	}


	@Override
	public void addUpConsumedpwr(int timePeriod, short zone, double usedPower)
	{
		//		print("maxPowerAvail=["+maxPowerAvail+"] consumedPower=["+consumedPower+"]  requestedPwr=["+requestedPwr+"] ");
		//	assert((requestedPwr - availPwr) < 0.005) : Constants.PRECOND_VIOLATION + " consumedPwr=["+requestedPwr+"] can not be greater than availPower=["+availPwr+"]";
		consumedPower[timePeriod] += usedPower;
		zoneToGrid.get((int)zone).addLoad(timePeriod, usedPower);
	}



	public void updatePower(IPowerConsumTracker pct)
	{
		for(int i = 0; i < nPrd; i++)
		{
			maxGlobalPower[i] = pct.getMaxGlobalPower(i);
			consumedPower[i] = pct.getGlobalConsumedPower(i);
		}

		ArrayList<PowerGridStation> gridSt = pct.getGridStation();
		for(int i = 0; i < gridSt.size(); i++)
		{
			stations.get(i).updateGridStationonsumption(gridSt.get(i));
		}	    
	}


	public double getMinAvailablePower()
	{
		double min = Double.MAX_VALUE;
		for (Integer key : zoneToGrid.keySet())
		{
			for(int i = 0; i < nPrd; i++)
			{
				min = Math.min(min, zoneToGrid.get(key).getAvailablePower(i));
			}
		}
		
		for(int i = 0; i < nPrd; i++)
		{
			min = Math.min(min, maxGlobalPower[i] - consumedPower[i]);
		}
		return min;
	}


	/**
	 * if globally slot is booked completely
	 * @param prd
	 * @return
	 */
	public boolean isBlocked(int prd)
    {
		boolean blocked = false;
		if(maxGlobalPower[prd] - consumedPower[prd]  < 0.005) 
		{
			blocked = true;
		} 
		return blocked;
    }

}


