/**
 * 
 */
package be.uhasselt.imob.feathers2.services.impl.commonsService;

import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.services.commonsService.IEVC_Conventions;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.library.base.Constants;

/**
 * This class provides the implementations of the interface which provides the definition of conventions used to transform data from FEATHERS to be used for EVC.
 * @author MuhammadUsman
 *
 */
public class EVC_Conventions implements IEVC_Conventions
{
	private final int firstMinDay = 0;
	private final int minsInDay  = WidrsConstants.MINUTES_IN_DAY;
	private final int f0MinOffset = F0Constants.FEATHERS0_TIME_OFFSET;
	private final int F0StartOfDayMin = firstMinDay + f0MinOffset;
	private final int F0EndOfDayMin = minsInDay + f0MinOffset;
	private final int prdLen = WidrsConstants.PERIOD_LEN;
	private final int prdsInDay = WidrsConstants.NUMBER_OF_PERIODS_IN_ONE_DAY;
	/**
	 * convention that periods are from 0 to periods in one day - 1 ;
	 */
	private final int maxprdNum = prdsInDay - 1;

//	/*
//	 * converts fethers minOfDay[180,1620] to normal minOfDay[180,1440]
//	 * (non-Javadoc)
//	 * @see be.uhasselt.imob.feathers2.services.commonsService.IEVC_Conventions#timeOfDayFromShiftedTime(double)
//	 */
//	@Override
//	public double timeOfDayFromShiftedTime(double shiftedTime)
//	{
//		assert(shiftedTime>=F0StartOfDayMin && shiftedTime <=F0EndOfDayMin) : Constants.PRECOND_VIOLATION +"shiftedTime=["+shiftedTime+"] does not lie between["+F0StartOfDayMin+","+F0EndOfDayMin+"] ";
//		shiftedTime -= f0MinOffset;
//		assert(shiftedTime >= 0 && shiftedTime <= minsInDay) : Constants.POSTCOND_VIOLATION +"shiftedTime=["+shiftedTime+"] does not lie between["+0+","+minsInDay+"] ";
//		return shiftedTime;
//	}

	/*
	 * converts the time[0,1440] to period number[0,95] of the day
	 * (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.commonsService.IEVC_Conventions#prdFromTime(double)
	 */
	@Override
	public int prdFloorValueFromTime(double time)
	{
//		if(time == minsInDay) time = time - 0.01;
//		assert(time >= 0 && time < minsInDay) : Constants.PRECOND_VIOLATION +"shiftedTime=["+time+"] does not lie between["+0+","+minsInDay+"] ";
		int prd = (int) Math.floor(time/prdLen);
//		assert(prd >= 0 && prd <= maxprdNum) : Constants.POSTCOND_VIOLATION +"Period=["+prd+"] does not lie between["+0+","+maxprdNum+"] ";
		return prd;
	}

//	/*
//	 * calculates the next period from given period number 
//	 * (non-Javadoc)
//	 * @see be.uhasselt.imob.feathers2.services.commonsService.IEVC_Conventions#nextPeriod(int)
//	 */
//	@Override
//	public int nextPeriod(int p)
//	{
//		assert(p >= 0 && p <= maxprdNum) : Constants.PRECOND_VIOLATION +"Period=["+p+"] does not lie between["+0+","+maxprdNum+"] ";
//		p += 1;
//
//		//		System.out.println("\tp=["+p+"] maxprdInDay=["+maxprdNum+"]");
//		p = (p < prdsInDay) ? p : p-prdsInDay;
//		//		System.out.println("\tp=["+p+"] prdsInDay=["+prdsInDay+"]");
//		assert(p >= 0 && p <= maxprdNum) : Constants.POSTCOND_VIOLATION +"Period=["+p+"] does not lie between["+0+","+maxprdNum+"] ";
//		return p;
//	}

//	@Override
//	public double convertTimePeriodicNormalDay(double time)
//	{
//		time = (time > minsInDay) ? time - minsInDay : time ;
//		assert (time >= firstMinDay && time < minsInDay) : Constants.POSTCOND_VIOLATION + "time=["+time+"] does not lie between["+firstMinDay+","+minsInDay+"] ";
//		return time;
//	}
//
//	@Override
//	public double convertTimePeriodicF0Day(double time)
//	{
//		time = (time > F0EndOfDayMin) ?  (time - F0EndOfDayMin + F0StartOfDayMin) : time;
//		assert (time >= F0StartOfDayMin && time < F0EndOfDayMin) : Constants.POSTCOND_VIOLATION + "time=["+time+"] does not lie between["+F0StartOfDayMin+","+F0EndOfDayMin+"] ";
//		return time;
//	}


	@Override
	public int minOfDayFromPrd(int  prd)
	{
//		assert ( prd >= 0 && prd < prdsInDay) : Constants.PRECOND_VIOLATION + "prd=["+prd+"] is out of acceptable bounds ["+0+","+(prdsInDay-1)+"].";
		int time = prd * prdLen;
//		assert (time >= firstMinDay && time < minsInDay) : Constants.POSTCOND_VIOLATION + "time=["+time+"] does not lie between["+firstMinDay+","+minsInDay+"] ";
		return time;
	}

	@Override
	public boolean doesTimeIntervalsOverlap(double st, double et, double actST, double actET)
	{
//		assert(st>= 0 && st<= minsInDay):Constants.PRECOND_VIOLATION+"st=["+st+"] is out of bounds ["+0+","+minsInDay+"]";
//		assert(et>= 0 && et<= minsInDay):Constants.PRECOND_VIOLATION+"et=["+et+"] is out of bounds ["+0+","+minsInDay+"]";
//		assert(actST>= 0 && actST<= minsInDay):Constants.PRECOND_VIOLATION+"actST=["+actST+"] is out of bounds ["+0+","+minsInDay+"]";
//		assert(actET>= 0 && actET<= minsInDay):Constants.PRECOND_VIOLATION+"actET=["+actET+"] is out of bounds ["+0+","+minsInDay+"]";
		
		assert(actET >= actST) : Constants.PRECOND_VIOLATION+" actET=["+actET+"] should be greater than or equal to  actST=["+actST+"]";
		assert(et >= st) : Constants.PRECOND_VIOLATION+" et=["+et+"] should be greater than or equal to  st=["+st+"]";
		boolean overlaps = false;
		if(actET >= actST)
		{
			if(st >= actST && st < actET  || et > actST && et <= actET)
				overlaps = true;
		}
//		else // activity Ending Time is in next day, circular time applied.
//		{
//			//split the activity starting and ending time into two pieces. [activity start time, 1440] && [0, activity end time], and check overlap for both chunks.
//			if( (st >= actST && st < minsInDay || et > actST && et <= minsInDay) ||
//					(st >= 0 && st < actET || et > 0 && et <= actET) )
//				overlaps = true;
//		}
		return overlaps;
	}

	@Override
    public int converCyclicPeriod(int stP)
    {
	    return stP%prdsInDay;
    }


}
