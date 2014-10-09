package be.uhasselt.imob.feathers2.services.commonsService;

/** Conventions used in FEATHERS : this interface provides definitions to adapt the conventions made in FEATHERS..
<ol>
   <li>A normal day starts from 0 minute of the day at midnight and ends at 1440 at next midnight [0,1440].</li>
   <li>Periods limits of the day bounds [0,95]</li>
   <li>person specific definition of the day can be different from normal definition of the day. 
       Person specific day starts from arrival at list home activity in the agenda. i.e [74,73]</li>
   <li></li>
</ol>
 */

public interface IEVC_Conventions
{
//
//	/**
//	 * Unshift the Feathers 3 hours Shift. Converts [180,1620] to normal min of the day [0:1440].
//	 * @param shiftedTime
//	 * @return min of the Day 
//	 * @precond. shiftedTime should be in limits [180,1620]
//	 * @postcond. converted time should be in limits in [0,1440]
//	 */
//	public double timeOfDayFromShiftedTime(double shiftedTime);


	/**
	 * converts the given time to the Period. This function divides the given time with period length and return the floor value.
	 * use this function when you need to take the period in which time occurs.
	 * @param time
	 * @return period number Integer
	 * @precond. time should be a normal minute of the day [0,1440]
	 * @postcond. period should be in limits [0,1440/peridLen] i.e. [0,95]
	 */
	public int prdFloorValueFromTime(double time);

//
//	/**
//	 * Next period is calculated using the following background assumptions. 
//	 * <ol>
//	 * <li>definition of personal day is an overlap of 95 periods over a circular lists of periods of one day</li>
//	 * <li>next period of 95 of person specific definition of day will 0</li>
//	 * </ol>
//	 * @param period
//	 * @return next Period
//	 * @precond. period can not be out of bounds [0,95]
//	 * @postcond. next period can not be out of bounds [0,95]
//	 */
//	public int nextPeriod(int p);

//
//	/**
//	 * in a concept of overlapping personal definition of the day over one regualer day, some time activity which started previous day minute can end in next day min. 
//	 * so in this case, time can get out of limit lastMinuteOfDay=[1440], so this function will make time periodic to bring it back to minOfDayLimits [0,1440]
//	 * @param time
//	 * @return time of the day
//	 * @postcond. time after conversion should again be within day limits [0,1440]
//	 */
//	public double convertTimePeriodicNormalDay(double time);

//
//	/**
//	 * converts the out of bound F0 time to periodic F0 time.
//	 * @param time
//	 * @return cyclic time 
//	 * @postcond. created cyclic time should be in limits [180, 1620]
//	 */
//	public double convertTimePeriodicF0Day(double time);
	/**
	 * returns minute of the day [0:1440] from given prd [0:95]
	 * @param prd
	 * @return min of Day.
	 * @precond. prd should be in limits [0:95]
	 * @postcond. min of the day should be in limits [0:1440]
	 */
	public int minOfDayFromPrd(int prd);


	/**
	 * in circular timing convention this function returns an indicator if time interval overlaps at least '(still to define the units)' with activity starting and ending time (activity time bound);
	 * @param st1 starting time of interval	
	 * @param et1 ending time of interval
	 * @param actST activity starting time
	 * @param actET activity ending time
	 * @return True/False if both times overlap 
	 * @precond. all times are normal minute of the day in bound [0,1440]
	 */
	public boolean doesTimeIntervalsOverlap(double st1, double et1, double actST, double actET);


	public int converCyclicPeriod(int stP);

}

