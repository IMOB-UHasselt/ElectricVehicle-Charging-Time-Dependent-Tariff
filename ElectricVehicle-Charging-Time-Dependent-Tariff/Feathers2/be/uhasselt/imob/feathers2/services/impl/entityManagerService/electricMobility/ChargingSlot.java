
package be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility;

import java.awt.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import com.csvreader.CsvReader;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IChargingSlot;
import be.uhasselt.imob.library.config.Config;

/**
 * @author MuhammadUsman
 *
 */
public class ChargingSlot implements IChargingSlot
{




	/**indicator if charging is planned at at the particular slot*/
	boolean localBlocked = false;


	/**in case of insufficient available cheap energy, extra slots are required, this field is marked true if it is planned extra than available cheap */
	boolean isMarkedExtra = false;

//	private float engDeff = Float.MIN_VALUE;

	private double requestedChargingTime = 0f;

	private double epsilon = 0.05f;

	private double maxPossChargingTime = Float.MAX_VALUE;

	private Logger logger; 



	public ChargingSlot(){}


	public ChargingSlot(IChargingSlot chSlot)
	{
		localBlocked = chSlot.isLocalBlocked();
	}



	@Override
	public boolean isLocalBlocked()
	{
		return localBlocked;
	}


	@Override
	public void markLocalBlocked(boolean set)
	{
		localBlocked = set;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.entityManagerService.IChargingSlot#isMarkedExtra()
	 */
	public boolean isMarkedExtra()
	{
		return isMarkedExtra;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.entityManagerService.IChargingSlot#setMarkedExtra(boolean)
	 */
	public void markExtra(boolean mark)
	{
		isMarkedExtra = mark;
	}


	@Override
    public void resetChargingStamp()
    {
		requestedChargingTime = 0;
    }



	@Override
    public boolean isSaturated()
    {
		return (Math.abs(maxPossChargingTime  - requestedChargingTime) <= epsilon/10);
    }


	@Override
    public void updateSaturation(double requestedChargingTime, double maxPossChargingTime)
    {
		this.requestedChargingTime = requestedChargingTime;
		this.maxPossChargingTime = maxPossChargingTime;
//		print("requestedEnergy=["+requestedEnergy+"], maxEngCharging=["+maxEngCharging+"], ");
    }

	private void print(String msg) 
	{
		if (logger == null) logger = Logger.getLogger(getClass().getName());
		System.out.println(msg);
		logger.info(msg);
	}

	/**
	 * @return the chargedEnergy
	 */
	public double getChargedEnergy()
	{
		return requestedChargingTime;
	}

	/**
	 * @param chargedEnergy the chargedEnergy to set
	 */
	public void setChargedEnergy(double chargedEnergy)
	{
		this.requestedChargingTime = chargedEnergy;
	}


	@Override
    public void reset()
    {
		localBlocked = false;
		isMarkedExtra = false;
		requestedChargingTime = 0f;
		maxPossChargingTime = Float.MAX_VALUE;
    }


	@Override
    public boolean isChargingPlanned()
    {
	    return requestedChargingTime>0;
    }

}

