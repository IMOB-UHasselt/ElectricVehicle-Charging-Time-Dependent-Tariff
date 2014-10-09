package be.uhasselt.imob.feathers2.services.impl.entityManagerService;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.csvreader.CsvWriter;

import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IActivity;
import be.uhasselt.imob.feathers2.services.entityManagerService.IBEV.FeasibleType;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerSupplyManager;
import be.uhasselt.imob.feathers2.services.entityManagerService.ISchedule;
import be.uhasselt.imob.library.base.Constants;


/**
   Daily schedule for a specific {@link be.uhasselt.imob.feathers2.services.impl.entityManagerService.Person Person}.
   <ol>
      <li>All car trips in the schedule are assumed to be driven using the same vehicle. This assumption was made since we do not have a separate <code>Household</code> object. Sooner or later we need to introduce such class but this again wil require more memory. Be very careful when extending the code.</li>
   </ol>
 *
 * @author MuhammadUsman
 */
public class Schedule implements ISchedule
{


	private ArrayList<IActivity> sched;
	//	
	private Logger logger;
	private ICar car = null;


	/** <code>C</code> value (inverse of initial schedule utility). */
	private float c = -1;
	/** Schedule feasible after compression ? */
	private boolean feasibleSchedule = true;
	/** Schedule has been infeasible at some time. */
	private boolean schedHasBeenInfeasible = false;
	/** Affected by network disturbance ? */
	private boolean affected = false;

	private Boolean containsNonZeroLengthCarTrip = null;

	public Schedule(ArrayList<IActivity> actList) 
	{
		this.sched = actList;
		init();
	}


	public Schedule(ArrayList<IActivity> actList, double basicCost) 
	{
		//		this.basicCost = basicCost;
		this.sched = actList;
		init();
	}

	private void init()
	{
		this.feasibleSchedule = true;
		this.affected = false;
		if (logger == null) logger = Logger.getLogger(getClass().getName());
	}


	/** Indicates whether or not the schedule contains trips driven by car. */
	public boolean containsCarTrips()
	{
		if(containsNonZeroLengthCarTrip != null)
			return containsNonZeroLengthCarTrip;

		containsNonZeroLengthCarTrip = false;
		for(IActivity act : sched)
		{
			containsNonZeroLengthCarTrip = (act.getJ_distance() > 0) && (act.getJ_transportMode() == WidrsConstants.tm_car);
			if (containsNonZeroLengthCarTrip) break;
		}
		return containsNonZeroLengthCarTrip;
	}




	@Override
	public IActivity get(int index)
	{
		return sched.get(index);
	}


	public ArrayList<IActivity> getActivitiesList() 
	{
		return sched;
	}




	@Override
	public ICar getCar()
	{
		return car;
	}


	@Override
	public  ISchedule getDuplicate()
	{
		ArrayList<IActivity> newList = new ArrayList<IActivity>();
		for(IActivity origAct : sched)
		{
			newList .add(origAct.getDuplicate());
		}
		Schedule schedule = new Schedule(newList);
		return (ISchedule) schedule;
	}


	/** Returns whether or not the person has a feasible schedule. */
	public boolean hasFeasibleSchedule()
	{
		return feasibleSchedule;
	}


	/**
	 * Returns whether or not the person is affected by the incident(s) applied.
	 */
	public boolean isAffected()
	{
		return affected;
	}

	/** Register the person as being affected by the incident(s) applied. */
	public void markAsAffected(boolean affected)
	{
		this.affected = affected;
	}


	@Override
	/** Register the person as having an infeasible schedule. */
	public void markScheduleFeasible(boolean feasible)
	{
		this.feasibleSchedule = feasible;
		if (!feasible)
			schedHasBeenInfeasible = true;
	}

	public void print() 
	{
		String msg = "";
		int i = 0;
		for(IActivity act : sched)
		{
			i++;
			msg = msg + "A"+i+"=["+(act.getJ_beginningTime() )+", "+(act.getA_beginningTimeF0_minOfDay())+","+(act.getA_beginningTimeF0_minOfDay()+act.getA_durationF0() )+"],  \n ";
		}

		msg.concat("\n");
		print(msg);
	}

	private void print(String msg) 
	{
		System.out.println(msg);
		logger.info(msg);
	}


	/**
	 * Returns whether or not the person's schedule has been (temporarily)
	 * infeasible.
	 */
	public boolean schedHasBeenInfeasible()
	{
		return this.schedHasBeenInfeasible;
	}


	@Override
	public void setCar(ICar car) 
	{
		this.car = car;
	}




	//================================================================================
	@Override
	/*
	 * (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.entityManagerService.ISchedule#analyseBatChargConsum(short[])
	 */
	public void setC(float c)
	{
		this.c = c;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.entityManagerService.ISchedule#analyseBatChargConsum(short[])
	 */
	public float getC()
	{
		return this.c;
	}


	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		String str = "";
		int i = 0;

		for(IActivity act : this.sched)
		{
			str = str.concat("\n\t "+(i++)+" =>"+act.toString());
		}
		return str;
	}


	@Override
	public double getTotalActDuration()
	{
		double totDur = 0;
		for(IActivity act : sched)
		{
			totDur += act.getA_duration();
		}
		return totDur;
	}


	@Override
	public double getTotalJourneyDuration()
	{
		double totDur = 0;
		for(IActivity act : sched)
		{
			totDur += act.getJ_duration();
		}
		return totDur;
	}


	@Override
	public int size()
	{
		return sched.size();
	}


	@Override
	public void writeValues(CsvWriter writer) throws IOException
	{
		for(IActivity act : sched)
			act.writeValues(writer);
	}





	@Override
	public void redefFirstAndLastHomeActivity()
	{
		if(sched.size() > 0)
		{
			int f0Shift = F0Constants.FEATHERS0_TIME_OFFSET;
			int size = sched.size();
			IActivity fAct = sched.get(0);
			IActivity lAct = sched.get(size-1); 

			assert(fAct.getA_activityType() == lAct.getA_activityType()) : Constants.PRECOND_VIOLATION+" first and last activities are not of same type.";
			assert(fAct.getA_activityType() == F0Constants.at_Home) : Constants.PRECOND_VIOLATION+" first activity is not the home activity";

			double redefST = lAct.getA_beginningTime_minOfDay()  - f0Shift;
			double dur = lAct.getA_duration();

			//			print("lact_ST=["+lAct.getA_beginningTime_minOfDay()+"], redefST=["+redefST+"]");
			int actInd = 0;
			for(IActivity act : sched)
			{
				if(actInd == 0)
					act.setA_beginningTime_minOfDay(act.getA_beginningTime_minOfDay() - f0Shift + redefST);
				else
					act.setA_beginningTime_minOfDay(act.getA_beginningTime_minOfDay() - f0Shift + redefST + dur);
				actInd++;
			}


			lAct.setA_durationToZero();
			fAct.setA_duration(dur + fAct.getA_duration());
		}
	}

	
	

	@Override
	public FeasibleType analyseBatChargConsum(int p_personCounter)
	{
		return car.analyseBatChargConsum(p_personCounter, this, 0);
	}



	@Override
	public ISchedule clone()
	{
		ArrayList<IActivity> actList = new ArrayList<IActivity>();
		for(IActivity act : sched)
		{
			actList.add(act.clone());
		}
		ISchedule scd = new Schedule(actList);
		scd.setC(c);
		if(car != null)
		{
			scd.setCar(car.clone());
		}
		return scd;
	}


	@Override
    public IActivity getActivityAtPeriod(int prd)
    {
		for(IActivity act : sched)
		{
			if(act.getFirstAssoccNSEprdWithJourney() <= prd && act.getLastAssoccNSEprd() >= prd) return act;
		}
	    return null;
    }


	@Override
    public double getTotalDistance()
    {
		double dis = 0;
		for(IActivity act : sched)
		{
			dis += act.getJ_distance();
		}
	    return dis;
    }


}
