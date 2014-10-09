/**
 * 
 */
package be.uhasselt.imob.feathers2.services.impl.entityManagerService;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.csvreader.CsvWriter;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IActivity;
import be.uhasselt.imob.feathers2.services.environmentService.IEnvironmentService;
import be.uhasselt.imob.feathers2.services.environmentService.IIncident;
import be.uhasselt.imob.feathers2.services.environmentService.IRandoms;

/**
 * 
 * 
 * This is a bean class which provide all fields for one activity from predicted schedule file from feathers.
   <ol>
      <li>Note that the private variables names reflect the terminology used in <code>Feathers0</code>.</li>
      <li>Definitions of <code>[f0_tod]</code> and <code>[minOfDay]</code> are in {@link be.uhasselt.imob.feathers2.services.reschedulingService} package info.</li>
      <li>Time values are kept and returned as [f0_tod]. Some methods allow times to be specified in <code>[minOfDay]</code>.</li>
      <li>As much variables as possible are made <code>static</code> in order to save memory. Millions of <em>episode</em> objects are instantiated simultaneously.</li>
      <li>Times used here all are <em>shiftedTime</em>s : see {@link be.uhasselt.imob.feathers2.services.commonsService.IConventions}</li>
   </ol>

 * @author MuhammadUsman
 *
 */
public class Activity implements IActivity 
{

	// =============================================================================================================
	/** Logger */
	private static Logger logger = null;
	/** Set of application specific widrsConstants. */
	private static WidrsConstants widrsConstants;
	/** Reference to EnvironmetService (is static to avoid millions of copied references to same object). */
	private static IEnvironmentService environmentService = null;

	// =============================================================================================================
	// Immutable inherited from Feathers
	/** Feathers0 assigned activity identifier : immutable. */
	private int A_activityCounter;
	/** Feathers0 encoded activity type : immutable. */
	private short A_activityType;
	// Mutable inherited Feathers0 stuff
	/** Activity start time [minOfDay] : modified by WIDRS. */
	private double A_beginningTime;
	/** Original activity start time [minOfDay] : not modified by WIDRS. */
	private short A_beginningTimeFromF0;
	/** Day-of-week : used only to be able to write new schedule using same format and contents as the original one. */
	private int A_day;
	/** Activity duration [min] : modified by WIDRS. */
	private double A_duration;

	/** Original activity duration [min] : not modified by WIDRS. */
	private double A_durationFromF0;
	/** Activity location identifier : immutable. */
	private short A_location;
	/**Alpha value for activity*/
	private double alpha = Double.NEGATIVE_INFINITY ;

	/**k value specific to the activity*/
	private float k = Float.NEGATIVE_INFINITY ;

	/** Incident descriptor that applies to this episode; the information is available only after notifTime. */
	private IIncident incident = null;
	/** Trip distance [km] : immutable. */
	private double J_distance;

	/** Episode duration [min] : modified by WIDRS. */
	private double J_duration = -1;

	/** Original episode duration [min] : not modified by WIDRS. */
	private double J_durationFromF0;

	/** Feathers0 encoded transportation mode : immutable. */
	private short J_transportMode;

	// WIDRS concepts
	// TODO:{LK} To be moved to Person (after considering how to handle multiple incidents)
	/** Notification time [minOfDay] for network disturbance; value equals Integer.MAX_VALUE if person is not yet notified. */
	private int notifTime = Integer.MAX_VALUE;

	/** Trip completion fraction: relative time part of the trip already completed.
       It has been avoided explicitly to make use of trip distance because impedance matrices
       specify trip duration only. */
	private double tripFractionCompleted;


	// =============================================================================================================
   private void init(IEnvironmentService envirSrv)
   {
      if (environmentService == null) environmentService = envirSrv;
		if (logger == null) logger = Logger.getLogger(getClass().getName());
		tripFractionCompleted = 0.0;
   }

	// =============================================================================================================
	public Activity(IEnvironmentService envirSrv)
   {
      this.init(envirSrv);
	}

	// =============================================================================================================
	/**
	 * @param a_activityCounter
	 * @param a_activityType
	 * @param a_location
	 * @param a_beginningTime
	 * @param a_duration
	 * @param j_transportMode
	 * @param j_distance
	 * @param j_duration
	 */
	public Activity(IEnvironmentService envirSrv, int a_activityCounter, short a_activityType, short a_location, short a_beginningTime, double a_duration, short a_day,
			short j_transportMode, double j_distance, double j_duration) 
	{
      this.init(envirSrv);

		A_activityCounter = a_activityCounter;
		A_activityType = a_activityType;
		A_day = a_day;
		A_location = a_location;
		A_duration = a_duration;
		J_transportMode = j_transportMode;
		J_distance = j_distance;
		J_duration = j_duration;

		setA_beginningTimeF0_f0Tod(a_beginningTime);
		A_durationFromF0 = a_duration; 
		J_durationFromF0 = j_duration;
	}

	// =============================================================================================================
	/** Converts unmanagable Feathers0 time-of-day encoding to minOfDay (minutes since midnight).
		       @param a_beginningTimeF0_f0tod Feathers0 encoded time-of-day
		       @return time-of-day expressed as minutes since midnight.
	 */
	public static double convert_F0_to_minOfDay(short a_beginningTimeF0_f0tod) {
		int minutes = (60*(a_beginningTimeF0_f0tod / 100) + (a_beginningTimeF0_f0tod % 100));
		return (double)minutes;
	}
	// =============================================================================================================
	/** Converts unmanagable Feathers0 time-of-day [f0_tod] to time-of-day [minOfDay].
       @param f0_tod [f0_tod]
       @return time-of-day [minOfDay]
	 */
	public static double convert_f0Tod_to_minOfDay(int f0_tod) {
		return (double)(60*(f0_tod/100) + f0_tod%100);
	}
	// =============================================================================================================
	/** Converts time-of-day [minOfDay] to unmanagable Feathers0 time-of-day [f0_tod] encoding.
       @param minOfDay minutes since midnight
       @return time-of-day [f0_tod]
	 */
	public static short convert_minOfDay_to_F0(double minOfDay)
	{
		short iMinOfDay = (short)minOfDay;
		short f0 = (short)(100*(iMinOfDay / 60) + (iMinOfDay % 60));
		return f0;
	}
	// =============================================================================================================
	/** Indicator : specified time t is included in episode activity halfopen interval
	       @param t Time [minOfDay]
	       @return true iff t in open interval [activityStartTime , activityEndTime)
	 */
	public boolean activeAt(double t) {
		return (A_beginningTime <= t) && (t < A_beginningTime + A_duration);
	}

	// =============================================================================================================
	/** Indicator
	       @param t Time [minOfDay]
	       @return true iff t in open interval [episodeBegin, episodeEnd)
	 */
	public boolean containsTime(double t) {
			return (A_beginningTime - J_duration <= t) && (t < A_beginningTime + A_duration);
	}


	// =============================================================================================================
	/** Indicator : specified time t is included in halfopen interval preceding the episode (episode follows t)
	       @param t Time [minOfDay]
	       @return true iff t in open interval [bigBang, episodeBegin)
	 */
	public boolean follows (double t) {
		return (t < A_beginningTime - J_duration);
	}

	// =============================================================================================================
	/**
	 * @return the a_activityCounter
	 */
	public int getA_activityCounter() {
		return A_activityCounter;
	}

	// =============================================================================================================
	/** Returns activity type identifier */
	public short getA_activityType() {
		return A_activityType;
	}

	// =============================================================================================================
	/** Returns activity start time [f0_tod] calculated by WIDRS.
	 */
	public short getA_beginningTime_f0Tod()
	{
		return convert_minOfDay_to_F0(A_beginningTime);
	}

	// =============================================================================================================
	/** Returns activity start time [minOfDay](Double) calculated by WIDRS. */
	public double getA_beginningTime_minOfDay()
	{
		return A_beginningTime;
	}

	// =============================================================================================================
	/** Returns activity start time [f0_tod] predicted by Feathers.
	 */
	public short getA_beginningTimeF0_f0Tod()
	{
		return convert_minOfDay_to_F0(A_beginningTimeFromF0);
	}

	// =============================================================================================================
	/** Returns activity start time [minOfDay] predicted by Feathers. */
	public double getA_beginningTimeF0_minOfDay()
	{
		return (double)A_beginningTimeFromF0;
	}

	// =============================================================================================================
	public int getA_day() {
		return A_day;
	}

	// =============================================================================================================
	/** Returns activity duration [min] */
	public double getA_duration() {
		return A_duration;
	}

	// =============================================================================================================
	/** Returns activity duration [min] predicted by Feathers */
	public double getA_durationF0() {
		return A_durationFromF0;
	}

	// =============================================================================================================
	/** Returns activity location identifier. */
	public short getA_location() {
		return A_location;
	}

	// =============================================================================================================
	/**
	 * @return the alpha
	 */
	public double getAlpha()
	{
		if (alpha == Double.NEGATIVE_INFINITY )
		{
			String msg = "Tried to read uninitialized alpha value";
			logger.error(msg);
			logger.error(this.toString());
			System.out.println(msg);
			System.out.println(this.toString());
		}
		return alpha;
	}

	/**
	 * calculate the distance traveled in the given period with period length, with subject to journey starting time and ending time.
	 * @param prd  period number
	 * @param prdLen Length of period
	 * @return distance traveled in the given period 
	 */
	public double getDistanceTraveledInPeriod(int prd, int prdLen) {
		return getDurationTraveledInPeriod(prd, prdLen) * J_distance/J_duration;
	}

	public Activity getDuplicate()
	{

		Activity act = new Activity(environmentService);
		// epi.getOwner().setP_personCounter(list.get(i).getOwner().getP_personCounter());
		// make user that the predecessor is not an old object (so, do not simply copy this attribute).
		//		if (origAct.predecessor() == null) predecessor = null;
		//		act.setPredecessor(predecessor);
		//		predecessor = act;

		// attributes that are not changed by schedule compression
		act.setA_activityType(A_activityType);
		act.setA_activityCounter(A_activityCounter);
		act.setA_location(A_location);
		act.setJ_distance(J_distance);
		act.setJ_transportMode(J_transportMode);
		act.registerIncidentNotif(notifTime, incident);
		act.setA_day(A_day);
		// mind the sequence : 'F0' flavors need to come first, they initialize fields that are modified by WIDRS
		act.setA_durationF0(A_durationFromF0);
		act.setJ_durationF0(J_durationFromF0);
		act.setA_beginningTimeF0_f0Tod(getA_beginningTimeF0_f0Tod());

		// now the fields that might have been changed by WIDRS: those shall be copied back if schedule compression succeeded
		act.setAlpha(alpha); //TODO:MU-20140226
		act.setK(k);
		act.setA_beginningTime_minOfDay(getA_beginningTime_minOfDay());
		act.setA_duration(A_duration);
		act.setJ_duration(J_duration);
		act.setTripFractionCompleted(tripFractionCompleted);

		return act;
	}

	/**
	 * calculate the duration traveled in the given period with subject to Journey starting and ending time.
	 * @param prd nse_period
	 * @param prdLen length of one period
	 * @return duration traveled in the given period
	 */
	private double getDurationTraveledInPeriod(int prd, int prdLen) {
		double base_SP = prd*prdLen;
		double base_EP = (prd+1)*prdLen;
		double rel_prd_start = Math.max(base_SP, getJ_beginningTime());
		double rel_Prd_end = Math.min(base_EP, getJ_endingTime());
		double durationInPrd = rel_Prd_end - rel_prd_start;
//				String msg2 = "b_SP=["+base_SP+"], b_EP=["+base_EP+"], r_SP=["+rel_prd_start+"], r_EP=["+rel_Prd_end+"], durInPrd=["+durationInPrd+"], disInPrd=["+durationInPrd* J_distance/J_duration+"]";
//				String msg ="\t   =>  prd=["+prd+"], J_bTime=["+getJ_beginningTime()+"], J_eTime=["+getJ_endingTime()+"], "+ msg2;
//				print(msg);
		return durationInPrd;
	}

	// =============================================================================================================
	/** Returns trip startTime [minOfDay]. */
	public double getJ_beginningTime()
	{
		return A_beginningTime - J_duration;
	}

	// =============================================================================================================
	/** Returns trip startTime [minOfDay] predicted by Feathers. */
	public double getJ_beginningTimeF0()
	{
		return (A_beginningTimeFromF0 - J_durationFromF0);
	}

	// =============================================================================================================
	/** Returns trip distance [km] */
	public double getJ_distance() {
		return J_distance;
	}

	// =============================================================================================================
	/** Returns trip duration [min] */
	public double getJ_duration() {
		return J_duration;
	}

	// =============================================================================================================
	/** Returns trip duration [min] predicted by Feathers */
	public double getJ_durationF0() {
		return J_durationFromF0;
	}

	// =============================================================================================================
	/** Returns trip endTime [minOfDay]. (It is same as activity begin time, yet it is an intended duplicate function to keep it easy for understanding)*/
	public double getJ_endingTime()
	{
		return A_beginningTime;
	}

	// =============================================================================================================
	public short getJ_transportMode()
	{
		return J_transportMode;
	}

	// =============================================================================================================
	/** Returns notification time for trouble on trip.*/
	public int getNotifTime() {
		return notifTime;
	}

	// =============================================================================================================
	/** Returns the fraction of the trip that has been completed (caluculated as a fraction of the expected total travel time).
      @return 0 &lt;= tripFractionCompleted &lt; 1
	 */
	public double getTripFractionCompleted() {
		return tripFractionCompleted;
	}

	// =============================================================================================================
	public IIncident incident() {
		return this.incident;
	}

	// =============================================================================================================
	/**
	 * @return if alpha value is initialized 
	 */
	public boolean isAlphaUninitialized() {
		return (alpha==Double.MIN_VALUE);
	}

	// =============================================================================================================
	/** Indicates whether or not the traveller was notified about the incident before the currently expected trip start time.
      Note that 
      <ol>
         <li>the current implementation allows for a single incident only (hence for a single <em>notification time</em></li>
         <li>the episode (hence trip) start time can change during schedule execution due to reScheduling</li>
      </ol>
	 */
	public boolean notifiedBeforeTripStart()
	{
		return this.notifTime < this.getJ_beginningTime();
	}

	// =============================================================================================================
	/** Indicator : specified time t is included in halfopen interval following the episode (episode precedes t)
	       @param t Time [minOfDay]
	       @return true iff t in open interval [episodeEnd, bigCrunch)
	 */
	public boolean precedes (double t)
	{
		return (A_beginningTime + A_duration <= t);
	}

	// =============================================================================================================
	private void print(String msg) 
	{
		System.out.println(msg);
		logger.info(msg);
	}

	// =============================================================================================================
	/** Register incident notification with episode.
      Note that the current implementation allows for a single incident only.
      @param notifTime [minOfDay] when person gets notified
      @param incident Incident descriptor
	 */
	public void registerIncidentNotif(int notifTime, IIncident incident) {// TODO:{LK} in Person
		this.notifTime = notifTime;
		this.incident = incident;
		// TODO:{LK,MU} We can handle at most one incident at a time because we do not regsiter the incident here; multiple incidents can affect a single trip during different periods: hence ... keep a ref to the incident here
	}

	// =============================================================================================================
	public void resetAgentProperties(){
		tripFractionCompleted = 0.0;
	}

	// =============================================================================================================
	public void setA_activityCounter(int a_activityCounter) {
		A_activityCounter = a_activityCounter;
	}

	// =============================================================================================================
	/** Register activity type identifier */
	public void setA_activityType(short a_activityType) {
		A_activityType = a_activityType;
	}

	// =============================================================================================================
	/** Register activity start time [minOfDay](Double).
		      @param a_beginningTime_minOfDay time of day expressed as [minOfDay]
	 */
	public void setA_beginningTime_minOfDay(double a_beginningTime_minOfDay)
	{
		A_beginningTime = a_beginningTime_minOfDay;
	}

	// =============================================================================================================
	/** Register activity start time [f0_tod] predicted by Feathers, initializes the mutable J_duration also. Caution, encoding same as in the constructor, value will be converted to F0 time to MinOfDay before assignment.
     @param a_beginningTimeF0_f0tod time of day predicted by Feathers, expressed as [f0_tod]
	 */
	public void setA_beginningTimeF0_f0Tod(short a_beginningTimeF0_f0tod)
	{
		A_beginningTime = convert_F0_to_minOfDay(a_beginningTimeF0_f0tod);
		A_beginningTimeFromF0 = (short)A_beginningTime;
	}

	// =============================================================================================================
	public void setA_day(int a_day) {
		A_day = a_day;
	}

	// =============================================================================================================
	/** Register activity duration [min]
      @param a_duration activity duration[min]
      @precond. <code>(a_duration &gt; 0) and (a_duration &lt;=  {@link be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants#MINUTES_IN_DAY MINUTES_IN_DAY}) </code>
	 */
	public void setA_duration(double a_duration)
	{
		assert ! Double.isNaN(a_duration);
		assert a_duration <= WidrsConstants.MINUTES_IN_DAY : Constants.PRECOND_VIOLATION + "a_duration=["+a_duration+"]";
		assert a_duration > 0 : Constants.PRECOND_VIOLATION + "a_duration=["+a_duration+"]";
		A_duration = a_duration;
	}

	// =============================================================================================================
	/** Register activity duration [min] predicted by Feathers, initializes the mutable A_duration also.
      @param a_durationFromF02 activity duration[min]
      @precond. <code>(a_duration &gt; 0) and (a_duration &lt;=  {@link be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants#MINUTES_IN_DAY MINUTES_IN_DAY}) </code>
	 */
	public void setA_durationF0(double a_durationFromF02)
	{
		assert a_durationFromF02 <= WidrsConstants.MINUTES_IN_DAY : Constants.PRECOND_VIOLATION + "a_durationF0=["+a_durationFromF02+"]";
		assert a_durationFromF02 > 0 : Constants.PRECOND_VIOLATION + "a_durationF0=["+a_durationFromF02+"]"/*+ "personCounter=["+this.getOwner().getP_personCounter()+"]"*/;
		A_durationFromF0 = a_durationFromF02;
		A_duration = a_durationFromF02;
	}

	// =============================================================================================================
	/** Register activity location identifier. */
	public void setA_location(short a_location) 
	{

		A_location = a_location;
	}

	// =============================================================================================================
	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void setK (float k) {
		this.k = k;
	}


	// =============================================================================================================
	/** Register trip distance [km] 
      @param j_distance journey distance[km]
      @precond. <code>j_distance &gt; 0</code>
	 */
	public void setJ_distance(double j_distance)
	{
		J_distance = j_distance;
	}


	// =============================================================================================================
	/** Register trip duration [min]
      @param j_duration journey duration
      @precond. <code>(j_duration &gt;= 0) and (j_duration &lt;  {@link be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants#MINUTES_IN_DAY MINUTES_IN_DAY}) </code>
	 */
	public void setJ_duration(double j_duration)
	{
		assert (j_duration <= WidrsConstants.MINUTES_IN_DAY) : Constants.PRECOND_VIOLATION + "a_duration=["+j_duration+"]";
		assert (j_duration >= 0) : Constants.PRECOND_VIOLATION + "j_duration=["+j_duration+"]";
		J_duration = j_duration;
	}

	// =============================================================================================================
	/** Register trip duration [min] predicted by Feathers, initializes the mutable J_duration also.
      @param j_durationFromF02 journey duration
      @precond. <code>(j_duration &gt;= 0) and (j_duration &lt;  {@link be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants#MINUTES_IN_DAY MINUTES_IN_DAY}) </code>
	 */
	public void setJ_durationF0(double j_durationFromF02)
	{
		assert (j_durationFromF02 <= WidrsConstants.MINUTES_IN_DAY) : Constants.PRECOND_VIOLATION + "a_durationF0=["+j_durationFromF02+"]";
		assert (j_durationFromF02 >= 0) : Constants.PRECOND_VIOLATION + "j_durationF0=["+j_durationFromF02+"]";
		J_durationFromF0 = j_durationFromF02;
		J_duration = j_durationFromF02;
	}

	// =============================================================================================================
	public void setJ_transportMode(short j_transportMode)
	{
		if ((j_transportMode == widrsConstants.tm_car) || (j_transportMode == widrsConstants.tm_slow) || (j_transportMode == widrsConstants.tm_public) || (j_transportMode == widrsConstants.tm_carPassenger) || (j_transportMode == widrsConstants.tm_undefined)) 
		{
			this.J_transportMode = j_transportMode;
		}
		/*	else
		{
			logger.error("Person=["+owner().getP_personCounter()+"] Household=["+owner().getH_householdCounter()+"] Activity=["+getA_activityType()+"] Invalid mode=["+j_transportMode+"] replaced by car=["+widrsConstants.tm_car+"]");
		}*/
	}


	// =============================================================================================================
	/** Set (relative duration) fraction of trip that has been completed.
       The value is forced to 0.0 or 1.0 whe the respective difference from the target value is sufficiently small.
       @param tfc trip fraction completed
       @precond. <code>0 &lt;== tfc &lt; 1</code>
	 */
	public void setTripFractionCompleted(double tfc)
	{
		Double eps = 1.e-05; // TODO:LK IN WidrsConstants
		assert ((0-eps <= tfc) && (tfc <= 1+eps)) : Constants.PRECOND_VIOLATION + "tripFractionCompleted=["+tfc+"] not in [0,1]";
		tripFractionCompleted = Math.max(0,Math.min(1,tfc));
		if (tripFractionCompleted < eps) tripFractionCompleted = 0;
		if (1 - tripFractionCompleted < eps) tripFractionCompleted = 1;
	}


	// =============================================================================================================
	/** String representation for logging */
	public String toString()
	{
		StringBuffer sb = new StringBuffer()
		.append("A_activityType=["+A_activityType+"] ")
		.append("A_activityCounter=["+A_activityCounter+"] ")
		.append("J_transportMode=["+J_transportMode+"] ")
		.append("A_location=["+A_location+"] ")
		.append("J_distance=["+J_distance+"] ")
		.append("A_beginningTime=["+A_beginningTime+"] ")
		.append("A_duration=["+A_duration+"] ")
		.append("J_duration=["+J_duration+"] ")
		.append("A_beginningTimeF0=["+A_beginningTimeFromF0+"] ")
		.append("A_durationF0=["+A_durationFromF0+"] ")
		.append("J_durationF0=["+J_durationFromF0+"] ")
		.append("notifTime=["+notifTime+"] ")
		.append("tripFractionCompleted=["+tripFractionCompleted+"] ")
		.append("alpha=["+alpha+"]");
		return sb.toString();
	}


	// =============================================================================================================
	/** Indicator
	       @param t Time [minOfDay]
	       @return true iff t in open interval [episodeBegin, activityStartTime)
	 */
	public boolean travelingAt(double t) {
		return (A_beginningTime - J_duration <= t) && (t < A_beginningTime);
	}

	// =============================================================================================================
	/** Returns expected trip duration [min] as derived by the owner from the incident notification. The trip duration depends (using exponential decay) on the length of the time gap between the <em>incident termination</em> and the <em>trip start</em> times.
    <ol>
       <li>This method takes the oiginally predicted trip duration as the expected duration at <code>tTripStart</code>. This is an estimate since the trip start time can have been modified. The problem is that not all impedance matrices can be kept in memory simultaneously.</li>
    </ol>
    @param tripDurNormal Trip duration for the normal (reference) situation.
    @param tripDurActual Trip duration for the actual situation.
	 */
	public double tripDurationFromIncident(double tripDurNormal, double tripDurActual)
	{
		int incEndTime = incident.occurrenceTime() + incident.duration();
		double gap = Math.max(0,getJ_beginningTime() - (double)incEndTime);
		double dur = J_duration;
		if (tripDurActual - tripDurNormal > 0)
		{
			dur += environmentService.randoms().nextGamma(tripDurActual - tripDurNormal)*Math.exp(-incident.attenuationAfter() * gap);
		}
		return dur;
	}


	// =============================================================================================================
	/** Indicator : trip starts at or after given time.
	    @param t Time [minOfDay]
	    @return true iff t in open interval (bigBang, A_beginningTime - J_duration]
	 */
	public boolean tripFollows (double t)
	{
		return (A_beginningTime -J_duration >= t);
	}

	// =============================================================================================================
	/** Indicator : trip finishes before or at given time.
	    @param t Time [minOfDay]
	    @return true iff t in open interval [A_beginningTime(), bigCrunch)
	 */
	public boolean tripPrecedes (double t)
	{
		return (A_beginningTime <= t);
	}
	// =============================================================================================================
	
	public void writeValues(CsvWriter wrtr) throws IOException 
	{
		wrtr.write(String.valueOf(A_activityCounter));
		wrtr.write(String.valueOf(A_day));
		wrtr.write(String.valueOf(A_activityType));
		wrtr.write(String.valueOf( convert_minOfDay_to_F0(A_beginningTime)));
		wrtr.write(String.valueOf(A_duration));
		wrtr.write(String.valueOf(A_location));
		wrtr.write(String.valueOf(J_duration));
		wrtr.write(String.valueOf(J_transportMode));
		wrtr.write(String.valueOf(J_distance));
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.entityManagerService.IActivity#getFirstAssoccNSEprd()
	 */
	public int getFirstAssoccNSEprd()
	{
		//		System.out.println("\n---------------------------------");
		double startT = this.A_beginningTime-F0Constants.FEATHERS0_TIME_OFFSET;
		double prd = Math.max(0, (Math.ceil(startT/widrsConstants.PERIOD_LEN) - 1));
//		print("ABT_F0=["+ A_beginningTime + "], ABT_WIDRS=["+ startT + "], FIRST_NSE_prd=["+ prd +"]");
		return (int) prd;
	}
	// =============================================================================================================
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.entityManagerService.IActivity#getLastAssoccNSEprd()
	 */
	public int getLastAssoccNSEprd()
	{
		double endT = this.A_beginningTime + this.A_duration - F0Constants.FEATHERS0_TIME_OFFSET;
		double prd = Math.max(0, (Math.ceil(endT/widrsConstants.PERIOD_LEN) - 1));
//		print("ENT_F0=["+ (this.A_beginningTime + this.A_duration) + "], ENT_WIDRS=["+ endT + "], LAST_NSE_prd=["+ prd +"]");
		return (int) prd;
	}
	// =============================================================================================================
	
	@Override
	public float getK() 
	{
		assert (k != Float.NEGATIVE_INFINITY ) : Constants.PRECOND_VIOLATION + " K value not initialized yet for activity "+ A_activityCounter;
		return k;
	}
	// =============================================================================================================
	
	@Override
    public boolean hasLocationInZonesList(ArrayList<Short> zones)
    {
		boolean inside = false;
		for(short zone : zones)
		{
			if(A_location == zone)
			{
				inside  = true;
			}
		}
		return inside;
    }

	// =============================================================================================================
	
	
	@Override
    public void redefActDuration(double dur)
    {
		A_duration += dur;
		double nABT = A_beginningTime - dur;
		
		A_beginningTime = (nABT >= 0) ? nABT : (widrsConstants.MINUTES_IN_DAY+nABT) ;
    }
	// =============================================================================================================

	@Override
    public void setA_durationToZero()
    {
		A_duration = 0.0;
    }

	// =============================================================================================================
	
	public IActivity clone()
	{
		IActivity act = new Activity(this.environmentService, A_activityCounter, A_activityType, A_location,getA_beginningTime_f0Tod() , A_duration, (short) A_day, J_transportMode, J_distance, J_duration); 
		act.setK(k);
		act.setAlpha(alpha);
		act.setTripFractionCompleted(tripFractionCompleted);
		return act;
	}

	@Override
    public double getUtility()
    {
		double a = getAlpha();
		double k = getK();
		double d = getA_duration();
	    return k * (1 - Math.exp(-1 * a * d) ) / a;
    }

	@Override
    public int getFirstAssoccNSEprdWithJourney()
    {
		//		System.out.println("\n---------------------------------");
		double startT = this.A_beginningTime - this.J_duration - F0Constants.FEATHERS0_TIME_OFFSET;
		double prd = Math.max(0, (Math.ceil(startT/widrsConstants.PERIOD_LEN) - 1));
//		print("ABT_F0=["+ A_beginningTime + "], ABT_WIDRS=["+ startT + "], FIRST_NSE_prd=["+ prd +"]");
		return (int) prd;
    }

}
