/**
 * Schedule : agenda for a particular person.
    <ol>
       <li><code>alpha</code>, <code>k</code> and <code>C</code> values:</li>
       <li>
          <ol>
             <li>The C value for a schedule is a utility scaling factor.</li>
             <li><code>alpha</code>-values are calulated from ratios between activity durations in the initial unmodified schedule (predicted by <code>FEATHERS</code>) equals 1.</li>
             <li>The <code>k</code>-value for the i-th activity is calculated from <code>k[i]=C/d[i]</code> where <code>d[i]</code> designates the activity duration.</li>
             <li>The <code>C</code>-value is calulated so that the utility for the initial unmodified schedule (predicted by <code>FEATHERS</code>) equals 1. Hence, <code>C</code> is the inverse of the value of the utility of the initial schedule.</li>
          </ol>
       </li>
    </ol>
 */
package be.uhasselt.imob.feathers2.services.entityManagerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import com.csvreader.CsvWriter;

import be.uhasselt.imob.feathers2.services.entityManagerService.IBEV.FeasibleType;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.production.PowerSupplyManager;


/**
 * @author MuhammadUsman
 * @version $Id:$
 */
public interface ISchedule 
{

	/**
	 * 
	 * @return list of the activities contained in the schedule
	 */
	public ArrayList<IActivity> getActivitiesList();

	public void markScheduleFeasible(boolean feasible);

	/**
	 * Create a deep copy of activities list.
	 */
	public ISchedule getDuplicate();

	/**
	 * Set C-value for the schedule.
	 */
	public void setC(float c);

	/**
	 * C-value for the schedule.
	 */
	public float getC();

	public void markAsAffected(boolean b);


	/**
	 * @param index Activity index in schedule
	 * @return the activity from the schedule at the provided index
	 */
	public IActivity get(int index);

	/** Get car used to drive this schedule. */
	public ICar getCar();

	/**
	 *  calculates whether the schedule contains atleast one activity where transport mode was car.
	 * @return if schedule contains any activity with car as transport mode
	 */
	public boolean containsCarTrips();

	/** 
	 * Set car to use for all car (as driver) trips in this schedule. 
	 * 
	 */
	public void setCar(ICar bev);



	/**
	 *  total activity duration of the schedule
	 * @return total activites duratio in the schedule
	 */
	public double getTotalActDuration();

	/**
	 *  total journey duration of the schedule
	 * @return total journey duration in the schedule
	 */
	public double getTotalJourneyDuration();

	/**
	 * gives number of activities in the schedule.
	 * @return number of activities.
	 */
	public int size();

	public void writeValues(CsvWriter writer) throws IOException;
	/**
	 * redefinition of first home and last home activity of the day.
	 */
	public void redefFirstAndLastHomeActivity();

	public FeasibleType analyseBatChargConsum(int p_personCounter);

	/**
	 * creates the deep clone of ISchedule object.
	 * @return ISchedule
	 */
	public ISchedule clone();

	public IActivity getActivityAtPeriod(int ind);

	public double getTotalDistance();





}
