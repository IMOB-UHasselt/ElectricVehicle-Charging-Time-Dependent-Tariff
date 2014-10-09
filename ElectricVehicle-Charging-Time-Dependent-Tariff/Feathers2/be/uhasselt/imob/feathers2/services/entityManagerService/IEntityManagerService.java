package be.uhasselt.imob.feathers2.services.entityManagerService;



import java.io.IOException;
import java.util.ArrayList;

import be.uhasselt.imob.feathers2.services.impl.entityManagerService.Activity;
import be.uhasselt.imob.library.config.Config;

import com.csvreader.CsvWriter;

/**
   this interface provides the services to communicate to the activity schedule perdicted file CSV.

 * @author MuhammadUsman
 * @version $Id: IEntityManagerService.java 866 2013-06-24 11:21:47Z MuhammadUsman $
 *
 */
public interface IEntityManagerService
{
	/**
	 * Creates and returns the BEV type car object.
	 * @param randCat is a boolean parameter about car category. False when we want BEV of same category.
	 * @param fixedPar Fixed parameters: boolean, when false all parameters about EV (distance specific consumption, charger power) are set randomly.
	 * @return the BEV entity
	 */
	public IBEV createAndGetBEV(boolean randCat, boolean fixedPar);

	/**Create a Charging Slot*/
	public  IChargingSlot createChargingSlot();

	/**
	 * calculate and return the checksum of the original schedule file
	 * @return checksum of original schedule file from FEATHER
	 */
	public String getCheckSumValueOfEpisodeScheduleFile();
	/**create the  ChargingSlot array equal to length of number of periods in a day {@code WidrsConstants#NUMBER_OF_PERIODS_IN_ONE_DAY}}*/
	public IChargingSlot[] getDuplicateChargingSlotsArray();
	/**
	 * @return the houseLvlList
	 */
	public ArrayList<IHouseHold> getHouseLvlList();

	/**
	 * This function calculates the number of subzones from the input episode file. Calculation is based on the fact that number of subzones in the episodes file are euqal to the highest subzone number used by any of the activity(destination or departure).
	 * @return number of subzones
	 */
	public int getNumberOfSubZones();//	

	/**
	 * @return the personLvlList
	 */
	public ArrayList<IPerson> getPersonLvlList();

	/**
	 * It reads the specified Episode file to create the list of <code>person</code> which contain the information of person and a schedule.
	 * @param epiFile
	 * @throws IOException 
	 */
	public ArrayList<IPerson> getPersonLvlList(String epiFile) throws IOException;

	/**
	 * returns the singlton entity of IPowerCapManager
	 * @return IPowerCapManager
	 */
	public IPowerSupplyManager getpowerSupplyManager();
	/**
	 * @return ArrayList<ISchedule> complete list of schedules where one schedule contains a list of activities. 
	 */
	public ArrayList<ISchedule> getSchedulelvlList();

	/**
	 * Checks whether episode input file exists in the given directory.
	 * @return a boolean whether input episode file exists
	 */
	public boolean inputEpisodeFileExists();

	public String readCheckSumValueFromFile(String checkSumValueFile);

	public void reset();

	public  void setCarConfig(Config cfg);

	/**
	 * first checks if parent directory exists, if it does not exist, it is created first. Then schedules of complete list of persons are saved in the specified file with same format as input episode file from FEATHERS.
	 * @param epiFileName target episode file name
	 * @param personsList List of Persons
	 * @throws IOException
	 */
	public void writeEpisodeFile(String epiFileName, ArrayList<IPerson> personsList) throws IOException;

	public void writeNewCheckSumValueToFile(String fileName, String value);

	/**
	 * gives the list of perons who have all of the activities inside the list of given zones.
	 * @param zones
	 * @return list of persons
	 */
	public ArrayList<IPerson> getFilteredPerListWithZones(ArrayList<Short> zones);
	/**
	 * This functions redefine the first and last activity of the schedule which is actually the same activity (home activity)and splitted into two home activities. 
	 * number of activities will be same. as last activity (which is home activity) will be there but having 0 minute long duration. this reduction in activity duration is compensated in first activity in the schedule (which is a home activity).
	 * This new implementation will create a new phenomenon of a separate/private definition of the day for each person. 
	 * day is assumed to start with the start of the first activity of the schedule. Hence every individual will have different start time of the day.
	 * @return list of Persons
	 */
	public ArrayList<IPerson> getPersonsListHomeActRedef();






}
