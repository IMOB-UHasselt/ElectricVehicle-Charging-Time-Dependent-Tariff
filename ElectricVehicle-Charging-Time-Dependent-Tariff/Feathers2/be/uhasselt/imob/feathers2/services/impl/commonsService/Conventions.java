package be.uhasselt.imob.feathers2.services.impl.commonsService;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.services.commonsService.IConventions;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;

/** Methods {@link #timeOfDayFromShiftedTime(int)}, {@link #prdFromShiftedTime(int)}, {@link #shiftedTimeFromPrd(int)} and {@link #prdFromTime(int)} implement the basic conversions.
    All other depend on those basic conversions.
 */
public class Conventions implements IConventions
{
	private static final int feathersDay_offset = F0Constants.FEATHERS0_TIME_OFFSET;
	private static final int tBasePrd = WidrsConstants.PERIOD_LEN;
	private static final int maxPrd = WidrsConstants.NUMBER_OF_PERIODS_IN_ONE_HOUR * 24 - 1;

	// ========================================================================
	/* inherit from interface */
	@Override
	public int shiftedTimeFromTimeOfDay(int timeOfDay)
	{
		assert ((timeOfDay >= 0) && ( timeOfDay < WidrsConstants.MINUTES_IN_DAY)) : Constants.SOFTWARE_ERROR+" timeOfDay=["+timeOfDay+"] out of range [0,"+WidrsConstants.MINUTES_IN_DAY+"]";
		return (timeOfDay < feathersDay_offset) ? timeOfDay + WidrsConstants.MINUTES_IN_DAY : timeOfDay;
	}

	// ========================================================================
	/* inherit from interface */
	@Override
	public int timeOfDayFromShiftedTime(int shiftedTime)
	{
		assert (shiftedTime - feathersDay_offset >= 0) && (shiftedTime - feathersDay_offset < WidrsConstants.MINUTES_IN_DAY) : Constants.PRECOND_VIOLATION + "shiftedTime=["+shiftedTime+"] out of range";
		return (shiftedTime >= WidrsConstants.MINUTES_IN_DAY) ? shiftedTime - WidrsConstants.MINUTES_IN_DAY : shiftedTime;  
	}

	// ========================================================================
	/* inherit from interface */
	@Override
	public int shiftedTimeFromPrd(int prd)
	{
		assert ((prd >= 0) && ( prd <= maxPrd)) : Constants.SOFTWARE_ERROR+" prd=["+prd+"] out of range [0,"+maxPrd+"]";
		return prd * tBasePrd + feathersDay_offset;
	}

	// ========================================================================
	/* inherit from interface */
	@Override
	public int prdFromShiftedTime(int shiftedTime)
	{
		shiftedTime -= feathersDay_offset;
		assert ((shiftedTime >= 0) && ( shiftedTime < WidrsConstants.MINUTES_IN_DAY)) : Constants.SOFTWARE_ERROR+" shiftedTime=["+shiftedTime+"] out of range [0,"+WidrsConstants.MINUTES_IN_DAY+"]";
		return shiftedTime / tBasePrd;
	}

	// ========================================================================
	/* inherit from interface */
	@Override
	public int prdFromTime(int timeOfDay)
	{
		assert ((timeOfDay >= 0) && ( timeOfDay < WidrsConstants.MINUTES_IN_DAY)) : Constants.SOFTWARE_ERROR+" timeOfDay=["+timeOfDay+"] out of range [0,"+WidrsConstants.MINUTES_IN_DAY+"]";
		return prdFromShiftedTime(shiftedTimeFromTimeOfDay(timeOfDay));
	}

	// ========================================================================
	/* inherit from interface */
	@Override
	public int timeFromPrd(int prd)
	{
		return timeOfDayFromShiftedTime(shiftedTimeFromPrd(prd));
	}

	// ========================================================================
	/* inherit from interface */
	@Override
	public int hourOfDayFromPrd(int prd)
	{
		return timeFromPrd(prd) / 60;
	}

	// ========================================================================
	/* inherit from interface */
	@Override
	public int shiftedHourOfDayFromPrd(int prd)
	{
		return shiftedTimeFromPrd(prd) / 60;
	}

}

