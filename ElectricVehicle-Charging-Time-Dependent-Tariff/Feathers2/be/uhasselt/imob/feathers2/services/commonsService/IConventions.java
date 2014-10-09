package be.uhasselt.imob.feathers2.services.commonsService;

/** Conventions used in FEATHERS : this interface provides conversions based on the conventions.
<ol>
   <li>TimeOfDay[min] is in the range [0,1440)</li>
   <li>ShiftedTimeOfDay[min] is in the range [0+{@link be.uhasselt.imob.feathers2.core.F0Constants#FEATHERS0_TIME_OFFSET FEATHERS0_TIME_OFFSET},1440+FEATHERS0_TIME_OFFSET)</li>
   <li>Note that <em>shiftedTime</em> is time-of-day expressed in a special range. Not every time value is shifted in the same way. Some subranges are not shifted, others are shifted over 24 hours.</li>
   <li><code>NSE_period</code> zero maps to {@link be.uhasselt.imob.feathers2.core.F0Constants#FEATHERS0_TIME_OFFSET FEATHERS0_TIME_OFFSET}
</ol>
*/

public interface IConventions
{
	// ========================================================================
	/**
      Get <em>shiftedTime</em> from <em>timeOfDay</em>.
      @param timeOfDay time of day [min]
      @precond. <code>0 &lt;= timeOfDay &lt; {@link be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants#MINUTES_IN_DAY MINUTES_IN_DAY} </code>
      @postcond. <code>shiftedTimeFromTimeOfDay(timeOfDayFromShiftedTime(t)) == t</code>
   */
	public int shiftedTimeFromTimeOfDay(int timeOfDay);

	// ========================================================================
	/**
      Get <em>timeOfDay</em> from <em>shiftedTime</em>.
      @param shiftedTime shifted time of day [min]
      @precond. <code>{@link be.uhasselt.imob.feathers2.core.F0Constants#FEATHERS0_TIME_OFFSET FEATHERS0_TIME_OFFSET} &lt;= shiftedTime &lt; {@link be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants#MINUTES_IN_DAY MINUTES_IN_DAY} + FEATHERS0_TIME_OFFSET</code>
      @postcond. <code>timeOfDayFromShiftedTime(shiftedTimeFromTimeOfDay(t)) == t</code>
   */
	public int timeOfDayFromShiftedTime(int shiftedTime);

	// ========================================================================
	/** 
      Returns <em>shifted</em> time of day [minOfDay] for given period offset.
      The first period starts at the <em>Feathers0 day offset</em>.
      @param prd Period offset.
      @precond. <code>prd in [0,NUMBER_OF_PERIODS_IN_ONE_HOUR*24 - 1]</code>
      @postcond. <code>(returnValue-{@link be.uhasselt.imob.feathers2.core.F0Constants#FEATHERS0_TIME_OFFSET FEATHERS0_TIME_OFFSET}) in  [0,NUMBER_OF_MINUTES_IN_DAY</code>
	 */
	public int shiftedTimeFromPrd(int prd);

	// ========================================================================
	/** 
      Returns the sequence number for the NSE_period that contains a given moment in time.
      @param shiftedTimeOfDay FEATHERS-shifted time of day [min] : if <code>timeOfDay</code> coincides with a boundary between NSE_periods, the number for the NSE_period following timeOfDay is returned.
      @precond. <code>(shiftedTimeOfDay-{@link be.uhasselt.imob.feathers2.core.F0Constants#FEATHERS0_TIME_OFFSET FEATHERS0_TIME_OFFSET}) in [0,NUMBER_OF_MINUTES_IN_DAY)</code>
      @postcond. <code>returnValue in [0,{@link be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants#NUMBER_OF_PERIODS_IN_ONE_DAY NUMBER_OF_PERIODS_IN_ONE_DAY}-1]</code>
      @postcond. <code>prdFromShiftedTime(shiftedTimeFromPrd(prd)) == prd</code>
	 */
	public int prdFromShiftedTime(int shiftedTimeOfDay);

	// ========================================================================
	/** 
      Returns the sequence number for the NSE_period that contains a given moment in time.
      @param timeOfDay time of day [min] : if <code>timeOfDay</code> coincides with a boundary between NSE_periods, the number for the NSE_period following timeOfDay is returned.
      @precond. <code>timeOfDay in [0,NUMBER_OF_MINUTES_IN_DAY)</code>
      @postcond. <code>prdFromTime(timeFromPrd(prd)) == prd</code>
	 */
	public int prdFromTime(int timeOfDay);

	// ========================================================================
	/** 
      Returns time of day [minOfDay] for given period offset.
      The first period starts at the <em>Feathers0 day offset</em>.
      @param prd Period offset.
      @precond. <code>prd in [0,NUMBER_OF_PERIODS_IN_ONE_HOUR*24]</code>
	 */
	public int timeFromPrd(int prd);

	// ========================================================================
	/** 
      Returns hour of day [0,23] for given period offset.
      The first period starts at the <em>Feathers0 day offset</em>.
      @param prd Period offset.
      @precond. <code>prd in [0,NUMBER_OF_PERIODS_IN_ONE_HOUR*24]</code>
	 */
	public int hourOfDayFromPrd(int prd);

	// ========================================================================
	/** 
      Returns shifted hour of day [3,26] for given period offset.
      The first period starts at the <em>Feathers0 day offset</em>.
      @param prd Period offset.
      @precond. <code>prd in [0,NUMBER_OF_PERIODS_IN_ONE_HOUR*24]</code>
	 */
	public int shiftedHourOfDayFromPrd(int prd);

}

