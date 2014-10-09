package be.uhasselt.imob.feathers2.services.impl.entityManagerService;

import java.io.IOException;
import java.util.ArrayList;

import be.uhasselt.imob.feathers2.services.entityManagerService.IActivity;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar;
import be.uhasselt.imob.feathers2.services.entityManagerService.IHouseHold;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPerson;
import be.uhasselt.imob.feathers2.services.entityManagerService.ISchedule;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar.CarType;

import com.csvreader.CsvWriter;

/**
 * This is a bean class which provide all fields for one person belonging the
 * Feathers synthetic population.
 * <ol>
 * <li>This class contains some household data too (because it was estimated to
 * be cheaper to include them here than to create a separate
 * <code>Household</code> class.</li>
 * </ol>
 * 
 * @author MuhammadUsman
 */
public class Person implements IPerson
{
	/**
	 * Episode being adapted (used while adjusting schedules to make them
	 * consistent with TransCAD derived impedance matrices).
	 */
	private Episode epiBeingAdapted = null;


	private HouseHold house;
	private short P_age;
	private short P_gend;
	private short P_isDriv;

	// Inherited from Feathers
	/** Person (individual) unique identifier. */
	private int P_personCounter;

	private short P_twork;

	private ISchedule schedule = null;

	// ===================================================================
	public Person()
	{
		P_personCounter = -1;
	}

	// ===================================================================

	public Person(int p_personCounter, short p_age, short p_twork,
			short p_gend, short p_isDriv)
	{
		this.P_personCounter = p_personCounter;
		this.P_age = p_age;
		this.P_twork = p_twork;
		this.P_gend = p_gend;
		this.P_isDriv = p_isDriv;
		this.schedule = null;

	}

	public Person(int p_personCounter, short p_age, short p_twork,
			short p_gend, short p_isDriv, ISchedule schedule)
	{
		this.P_personCounter = p_personCounter;
		this.P_age = p_age;
		this.P_twork = p_twork;
		this.P_gend = p_gend;
		this.P_isDriv = p_isDriv;
		this.schedule = schedule;
	}

	/**
	 * Returns reference to episode being adapted during
	 * <em>initial trip time adjustments</em>.
	 */
	public Episode epiBeingAdapted()
	{
		return epiBeingAdapted;
	}

	/**
	 * @return the house
	 */
	@Override
	public IHouseHold getHouse()
	{
		return house;
	}

	/**
	 * @return the p_age
	 */
	public short getP_age()
	{
		return P_age;
	}

	/**
	 * @return the p_gend
	 */
	public short getP_gend()
	{
		return P_gend;
	}

	/**
	 * @return the p_isDriv
	 */
	public short getP_isDriv()
	{
		return P_isDriv;
	}

	/**
	 * @return the p_personCounter
	 */
	public int getP_personCounter()
	{
		return P_personCounter;
	}

	/**
	 * @return the p_twork
	 */
	public short getP_twork()
	{
		return P_twork;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.uhasselt.imob.feathers2.services.impl.entityManagerService.y#getSchedule
	 * ()
	 */
	public ISchedule getSchedule()
	{
		return schedule;
	}






	/**
	 * @param house
	 *            the house to set
	 */
	public void setHouse(HouseHold house)
	{
		this.house = house;
	}

	/**
	 * @param p_age
	 *            the p_age to set
	 */
	public void setP_age(short p_age)
	{
		P_age = p_age;
	}

	/**
	 * @param p_gend
	 *            the p_gend to set
	 */
	public void setP_gend(short p_gend)
	{
		P_gend = p_gend;
	}

	/**
	 * @param p_isDriv
	 *            the p_isDriv to set
	 */
	public void setP_isDriv(short p_isDriv)
	{
		P_isDriv = p_isDriv;
	}

	/**
	 * @param p_personCounter
	 *            the p_personCounter to set
	 */
	public void setP_personCounter(int p_personCounter)
	{
		P_personCounter = p_personCounter;
	}

	/**
	 * @param p_twork
	 *            the p_twork to set
	 */
	public void setP_twork(short p_twork)
	{
		P_twork = p_twork;
	}

	/**
	 * @param schedule
	 *            the schedule to set
	 */
	public void setSchedule(ISchedule schedule)
	{
		this.schedule = schedule;
	}

	public void writeValues(CsvWriter writer) throws IOException
	{
		writer.write(String.valueOf(P_personCounter));
		writer.write(String.valueOf(P_age));
		writer.write(String.valueOf(P_twork));
		writer.write(String.valueOf(P_gend));
		writer.write(String.valueOf(P_isDriv));
	}

	@Override
	public String toString()
	{

		StringBuffer sb = new StringBuffer()
		.append("\nP_personCounter =["+P_personCounter+"], ")
		.append("P_age =["+P_age+"], ")
		.append("P_twork =["+P_twork+"], ")
		.append("P_gend =["+P_gend+"], ")
		.append("P_isDriv =["+P_isDriv+"] ")
		.append(this.schedule.toString());
		return sb.toString();
	}

	@Override
	public boolean hasAllActInZoneList(ArrayList<Short> zones)
	{
		boolean inside = false;
		for(IActivity act : this.schedule.getActivitiesList())
		{
			if(act.hasLocationInZonesList(zones))
			{
				inside = true;
			}
			else
				return false;
		}
		return inside;
	}

	//================================================================================
	/**
	 * function to compare two IPerson objects
	 * it sorts in descending order.
	 */
	@Override
	public int compareTo(IPerson prs)
	{
		//descending order
		return (int) (prs.getSchedule().get(0).getA_beginningTime_minOfDay() - this.schedule.get(0).getA_beginningTime_minOfDay());
	}
	
	//================================================================================
	public IPerson clone()
	{
		IPerson prs = new Person(P_personCounter, P_age, P_twork, P_gend, P_isDriv, schedule.clone());
		return prs;

	}
}
