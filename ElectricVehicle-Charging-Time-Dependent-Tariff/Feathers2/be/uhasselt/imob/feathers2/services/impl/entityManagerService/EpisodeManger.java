/**
 * 
 */
package be.uhasselt.imob.feathers2.services.impl.entityManagerService;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IActivity;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPerson;
import be.uhasselt.imob.feathers2.services.environmentService.IEnvironmentService;
import be.uhasselt.imob.library.config.Config;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * @author MuhammadUsman
 * 
 */
public class EpisodeManger
{
	/** Data directory path. */
	private String dataDir = null;

	/** input file path. */
	private String originalEpisodeFile = null;

	/** output file name */
	private String outFile = null;

	/** Logger */
	private Logger logger = null;
	private String currentEpisodeFileAsInput = null;
	private Config config;

	/**
	 * file name of the episode which was created during previous run after
	 * making the original file compatible with reference matrices
	 */
	private String adjustedEpisodeFileName = null;

	private CsvReader epiReader = null;

	private CsvWriter writer;

	private Episode predecessor = null;

	/** file containing the checksum (CRC-32) of the original Episode File */
	private String checkSumOriginalEpisodeFileName = null;

	/** Keep info about last person for whom an Episode has been created. */
	// private Person currentPerson = null;

	private ICommonsService commonsService;
	private IEnvironmentService environmentService;

	/**
	 * latest Episode fetched from File, first episode is fetched while creating
	 * the reader to the file
	 */
	private static Episode epiFetched = null;

	/**
	 * this is a reference to the household object which serves as a reference
	 * to the house which is fetched from the episode, all <code>person</code>
	 * living in same house contains the same object of house.
	 */
	private static HouseHold houseFetched = null;

	// =============================================================================================================
	/**
	 * Number of subzones : this field is updatd after calculating the number of
	 * zones updated from schedule file
	 */
	private int numberOfSubZones = Integer.MIN_VALUE;

	private boolean configError = false;

	// =============================================================================================================

	public EpisodeManger(Config cfg, ICommonsService commonsSrv, IEnvironmentService envirSrv) throws F2Exception
	{
		assert (cfg != null) : Constants.PRECOND_VIOLATION + "cfg == null";
		assert (commonsSrv != null) : Constants.PRECOND_VIOLATION + "commonsSrv == null";
		assert (envirSrv != null) : Constants.PRECOND_VIOLATION + "envirSrv == null";
		this.config = cfg;
      this.environmentService = envirSrv;
		this.commonsService = commonsSrv;

		if (logger == null)
			logger = Logger.getLogger(getClass().getName());

	
		dataDir = fetchStringConfigValue("rescheduler:dataDir");
		originalEpisodeFile = fetchStringConfigValue("fetchEpisode:dataFile");
		outFile = fetchStringConfigValue("fetchEpisode:outPutFile");
		adjustedEpisodeFileName = fetchStringConfigValue("fetchEpisode:episodeFileBeforeRescheduling");
		checkSumOriginalEpisodeFileName = fetchStringConfigValue("fetchEpisode:epiCheckSumFileName");

		if (configError)throw new F2Exception("Consult log: Fetch Epsiode service instantiation failed");
	}

	// =============================================================================================================
	
	public EpisodeManger(String epiFile, ICommonsService commonsSrv, IEnvironmentService envirSrv) throws IOException
   {
		assert (epiFile != null) : Constants.PRECOND_VIOLATION + "epiFile == null";
		assert (commonsSrv != null) : Constants.PRECOND_VIOLATION + "commonsSrv == null";
		assert (envirSrv != null) : Constants.PRECOND_VIOLATION + "envirSrv == null";
      this.environmentService = envirSrv;
		this.commonsService = commonsSrv;

		logger = Logger.getLogger(getClass().getName());
	   epiReader = new CsvReader(epiFile, ';');
		epiReader.readHeaders();
		epiFetched = fetchNext();
		houseFetched = new HouseHold();
		houseFetched.setH_householdCounter(-1);
    }
	// =============================================================================================================

	private String fetchStringConfigValue(String name)
	{
		String value = config.stringValueForName(name);
		if (value == null)
		{
			logger.error("Config error : no value for name=["+name+"]");
			configError  = true;
		}
		return value;
	}
	// =============================================================================================================
	/**
	 * returns the name and path of original episode file
	 */
	public String originalEpisodeFileNameWithPath()
	{
		return originalEpisodeFile;
	}

	// =============================================================================================================
	/**
	 * This function calculate the number of subzones used in the episode file
	 * It finds the highest number of the subzone which is used in any of the
	 * activity in the episode file.
	 * 
	 * @return number of subzones in the episode file
	 */
	public int getNumberOfSubZones()
	{
		if (numberOfSubZones != Integer.MIN_VALUE)
			return numberOfSubZones;
		try
		{

			String msg = "Number of subzones from the input file calculation started. ";
			logger.info(msg);
			System.out.println(msg);

			int numberOfSubzones = Integer.MIN_VALUE;
			int hLocation;
			int aLocation;

			if (currentEpisodeFileAsInput == null)
			{
				System.out.println("currentEpisodeFileAsInput = [" + currentEpisodeFileAsInput + "]");
				reset();
			}

			CsvReader reader = new CsvReader(currentEpisodeFileAsInput, ';');

			if (!(new File(currentEpisodeFileAsInput).exists()))
				System.out.println("File=[" + currentEpisodeFileAsInput + "] does not exist.");

			if (reader == null)
				System.out.println("reader is null");

			reader.readHeaders();
			while (reader.readRecord())
			{
				hLocation = Integer.parseInt(reader.get("H_hhlocid"));
				aLocation = Integer.parseInt(reader.get("A_location"));

				if (hLocation > numberOfSubzones)
					numberOfSubzones = hLocation;

				if (aLocation > numberOfSubzones)
					numberOfSubzones = aLocation;

			}
			reader.close();

			msg = numberOfSubzones + " subzones found in the input episodes file";
			logger.info(msg);
			System.out.println(msg);

			return numberOfSubzones;

		} catch (FileNotFoundException e)
		{
			logger.error(currentEpisodeFileAsInput + " FileNotFoundException at getNumberOfSubZones. " + e.getMessage());
		} catch (IOException e)
		{
			logger.error("IOException at getNumberOfSubZones. " + e.getMessage());
		}
		return Integer.MIN_VALUE;
	}

	// =============================================================================================================
	/**
	 * This function prepares the <em>episode file reader</em> to be used.
	 * This function must be
	 * executed before start reading the Episode file
	 * 
	 */
	public void loadEpisodeFileReader()
	{

		try
		{
			this.currentEpisodeFileAsInput = selectInputScheduleFile();
			epiReader = new CsvReader(currentEpisodeFileAsInput, ';');
			epiReader.readHeaders();

		} catch (FileNotFoundException e)
		{
			logger.error(e.toString());
			logger.error("Consult log: FileNotFoundException occured");
		} catch (IOException e)
		{
			logger.error(e.toString());
			logger.error("Consult log: IOException occured");
		}
	}

	// =============================================================================================================

	/**
	 * This method selects the schedule file from previous run where it was made
	 * consistent with reference matrices before actual rescheduling. Before
	 * Selecting this file, concurrency of the original schedule file from
	 * FEATHERS is checked by comparing the checksum with already calculated
	 * checksum. if current original schedule file is not concurrent to the
	 * checksum, then original schedule file is selected for input. if schedule
	 * file which was made consistent during previous run is not found in
	 * directory, again original schedule file from FEATHERS is used as input.
	 * 
	 * @return inputScheduleFile
	 * @throws IOException
	 */
	private String selectInputScheduleFile() throws IOException
	{
		print("Episode input Selection method started.");

		String selectedInputFile = originalEpisodeFile;

		String oldCheckSumValueFromFile = null;
		String newCalculatedChecksum = null;

		File adjustedEpisodeFile = new File(adjustedEpisodeFileName);
		File checkSumFile = new File(checkSumOriginalEpisodeFileName);

		newCalculatedChecksum = this.getCheckSumValueOfEpisodeScheduleFile();

		if (!checkSumFile.exists())
		{
			print("checksum containing file does not exist, Validation Failed.");
			checkSumFile.createNewFile();
			writeNewCheckSumValueToFile(checkSumOriginalEpisodeFileName, newCalculatedChecksum);
		} else
		{
			oldCheckSumValueFromFile = readCheckSumValueFromFile(checkSumOriginalEpisodeFileName);
			print("checkSumFromFile=[" + oldCheckSumValueFromFile + "] calculatedCheckSum[" + newCalculatedChecksum + "]");
			if (adjustedEpisodeFile.exists()) // if file
			{
				if (oldCheckSumValueFromFile == null)
				{
					print("Episode File Validation: oldCheckSumValueFromFile is null, Validation Failed");
					writeNewCheckSumValueToFile(checkSumOriginalEpisodeFileName, newCalculatedChecksum);
				} else if (!oldCheckSumValueFromFile.matches(newCalculatedChecksum))
				{
					print("Episode File Validation: old and new checksum do not match, Validation Failed.");
					writeNewCheckSumValueToFile(checkSumOriginalEpisodeFileName, newCalculatedChecksum);
				} else
				{
					print("Episode File Validation: old and new checksum have matched, Validation succeeded.");
					selectedInputFile = adjustedEpisodeFileName;
				}
			} else
			{
				print("adjustedEpisodeFile = [" + adjustedEpisodeFile + "] does not exist.");
			}
		}
		print("selectedInputEpisodeFile=[" + selectedInputFile + "]");

		return selectedInputFile;
	}

	// =============================================================================================================
	/**
	 * overwrite the file with content of the parameter value
	 * 
	 * @param fileName
	 *            FileName of the file where to write(it contains both name and
	 *            path)
	 * @param value
	 *            String which contains the value to be written
	 */
	public void writeNewCheckSumValueToFile(String fileName, String value)
	{
		logger.info("Writing new checksum=[" + value + "] in the file=[" + fileName + "]");
		System.out.println("Writing new checksum=[" + value + "] in the file=[" + fileName + "]");
		FileOutputStream fos;
		try
		{
			fos = new FileOutputStream(fileName, false);

			fos.write(value.getBytes());
			fos.close();

		} catch (FileNotFoundException e)
		{
			logger.error("FileNotFoundException Occurred! Failed to write new checksum in " + fileName + ".");
		} catch (IOException e)
		{
			logger.error("IOException Occurred! Failed to write new checksum in " + fileName + ".");
		}

	}

	// =============================================================================================================

	/**
	 * This function returns the checksum crc-32 value for the original episode
	 * file
	 * 
	 * @return checksum of the original episode file
	 */
	public String getCheckSumValueOfEpisodeScheduleFile()
	{

		return commonsService.checksumValue(this.originalEpisodeFileNameWithPath());
	}

	// =============================================================================================================
	/**
	 * read checksum value from file and return the value
	 * 
	 * @param checkSumValueFile
	 * @return value for check from file
	 */
	public String readCheckSumValueFromFile(String checkSumValueFile)
	{
		logger.info("read new checksum from the file=[" + checkSumValueFile + "]");
		System.out.println("read new checksum from the file=[" + checkSumValueFile + "]");
		String value = null;
		try
		{
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(checkSumValueFile);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			// Read File By Line
			value = br.readLine();
			// Close the input stream
			in.close();
		} catch (Exception e)
		{// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		if (value == null)
			logger.error("Error occurred! Nothing to read from check sum value file");

		return value;
	}

	// =============================================================================================================
	public void WriteEpisodesToFile(ArrayList<Episode> list)
	{
		try
		{
			writer = new CsvWriter(new FileWriter(outFile, false), ';');
			writeHeader(writer);
			for (Episode epi : list)
			{
				epi.writeValues(writer);
				writer.endRecord();
			}
			writer.close();
		} catch (IOException e)
		{
			logger.error(e.toString());
		}
	}

	// =============================================================================================================
	/**
	 * Writes the person specific data to the specified file writer. It write the data in same original format as input. Information about house and person is repeated in each tupple with activity. 
	 * @param prs Person entity
	 * @param writer CSV writer
	 * @throws IOException
	 */
	public void WritePersonToFile(IPerson prs, CsvWriter writer) throws IOException
	{
		ArrayList<IActivity> sched = prs.getSchedule().getActivitiesList();
		for (int i = 0; i < sched.size(); i++)
		{
			prs.getHouse().writeValues(writer);
			prs.writeValues(writer);
			sched.get(i).writeValues(writer);
			writer.endRecord();
		}
	}

	// =============================================================================================================

	// =============================================================================================================


	// =============================================================================================================
	public void writeHeader(CsvWriter writer2) throws IOException
	{
		writer2.write("H_householdCounter");
		writer2.write("H_hhlocid");
		writer2.write("H_comp");
		writer2.write("H_SEC");
		writer2.write("H_age");
		writer2.write("H_child");
		writer2.write("H_ncar");
		writer2.write("P_personCounter");
		writer2.write("P_age");
		writer2.write("P_twork");
		writer2.write("P_gend");
		writer2.write("P_isDriv");
		writer2.write("A_activityCounter");
		writer2.write("A_day");
		writer2.write("A_activityType");
		writer2.write("A_beginningTime");
		writer2.write("A_duration");
		writer2.write("A_location");
		writer2.write("J_duration");
		writer2.write("J_transportMode");
		writer2.write("J_distance");
		writer2.endRecord();
	}

	// =============================================================================================================
	public Episode fetchNext()
	{
		try
		{
			epiFetched = null;

			int p_personCounter, h_householdCounter, a_activityCounter;
			short p_gend, p_age, p_twork, p_isDriv, h_hhlocid, h_SEC, h_child, h_ncar, h_age, h_comp, a_day, a_activityType, a_location, j_transportMode, a_beginningTime;
			double j_duration = -1;
			double a_duration;
			double j_distance;

			if (epiReader.readRecord())
			{
				h_householdCounter = Integer.parseInt(epiReader.get("H_householdCounter"));
				h_hhlocid = Short.parseShort(epiReader.get("H_hhlocid"));
				h_comp = Short.parseShort(epiReader.get("H_comp"));
				h_SEC = Short.parseShort(epiReader.get("H_SEC"));
				h_age = Short.parseShort(epiReader.get("H_age"));
				h_child = Short.parseShort(epiReader.get("H_child"));
				h_ncar = Short.parseShort(epiReader.get("H_ncar"));
				p_personCounter = Integer.parseInt(epiReader.get("P_personCounter"));
				p_age = Short.parseShort(epiReader.get("P_age"));
				p_twork = Short.parseShort(epiReader.get("P_twork"));
				p_gend = Short.parseShort(epiReader.get("P_gend"));
				p_isDriv = Short.parseShort(epiReader.get("P_isDriv"));
				a_activityCounter = Integer.parseInt(epiReader.get("A_activityCounter"));
				a_day = Short.parseShort(epiReader.get("A_day"));
				a_activityType = Short.parseShort(epiReader.get("A_activityType"));
				a_beginningTime = Short.parseShort(epiReader.get("A_beginningTime"));
				j_transportMode = Short.parseShort(epiReader.get("J_transportMode"));
				j_distance = Double.parseDouble(epiReader.get("J_distance"));
//				short value = (short) Double.parseDouble(epiReader.get("A_duration"));
//				a_duration = (value != 0 ) ? value : 1;
				a_duration = Double.parseDouble(epiReader.get("A_duration"));
				
				
				a_location = Short.parseShort(epiReader.get("A_location"));
				if (a_location == -1)
					a_location = h_hhlocid;
//				value = (short) Double.parseDouble(epiReader.get("J_duration"));
//				j_duration = (value != 0 ) ? value : 1;
				j_duration = Double.parseDouble(epiReader.get("J_duration"));
				
				epiFetched = new Episode(h_householdCounter, h_hhlocid, h_SEC, h_child, h_ncar, h_age, h_comp, p_personCounter, p_gend, p_age,
				        p_twork, p_isDriv, a_day, a_activityType, a_activityCounter, a_location, a_beginningTime, a_duration, j_transportMode,
				        j_distance, j_duration);

			}

		} catch (IOException e)
		{
			logger.error("IOException occured during reading episode from input file");
			logger.error(e.toString());
		}
		return epiFetched;
	}

	// =============================================================================================================
	public Person fetchNextPerson()
	{
		if (epiFetched == null)
			return null;
		Person prs = new Person(epiFetched.getP_personCounter(), epiFetched.getP_age(), epiFetched.getP_twork(), epiFetched.getP_gend(),
		        epiFetched.getP_isDriv());

		if (houseFetched.getH_householdCounter() != epiFetched.getH_householdCounter())
			houseFetched = new HouseHold(epiFetched.getH_householdCounter(), epiFetched.getH_hhlocid(), epiFetched.getH_SEC(),
			        epiFetched.getH_child(), epiFetched.getH_ncar(), epiFetched.getH_age(), epiFetched.getH_comp());

		ArrayList<IActivity> actList = new ArrayList<IActivity>();
		while (epiFetched.getP_personCounter() == prs.getP_personCounter())
		{
			IActivity act = new Activity(environmentService,epiFetched.getA_activityCounter(), epiFetched.getA_activityType(), epiFetched.getA_location(),
			        epiFetched.getA_beginningTime(), epiFetched.getA_duration(), epiFetched.getA_day(), epiFetched.getJ_transportMode(),
			        epiFetched.getJ_distance(), epiFetched.getJ_duration());
			actList.add(act);
			assert (act.getA_beginningTime_minOfDay() - F0Constants.FEATHERS0_TIME_OFFSET >= 0) && (act.getA_beginningTime_minOfDay()  - F0Constants.FEATHERS0_TIME_OFFSET < WidrsConstants.MINUTES_IN_DAY) : Constants.PRECOND_VIOLATION + "shiftedTime=["+act.getA_beginningTime_minOfDay()+"] out of range";
			if (fetchNext() == null)
				break;
		}

		prs.setHouse(houseFetched);
		prs.setSchedule(new Schedule(actList));

		return prs;
	}

	// =============================================================================================================
	public boolean inputEpisodeFileExists()
	{
		File f = new File(originalEpisodeFile);
		if (f.exists())
			return true;
		else
			return false;
	}

	// =============================================================================================================
	/**
	 * @return the name of the input file.
	 */
	public String getoutPut()
	{

		System.out.println(originalEpisodeFile);
		return originalEpisodeFile;
	}

	// =============================================================================================================
	public void cleanup()
	{
		if (epiReader != null)
			epiReader.close();
		logger.info("Cleaned up.");
	}

	// =============================================================================================================
	/**
	 * Deletes old episode reference reader. selects and loads input episode
	 * See {@link #loadEpisodeFileReader()}
	 */
	public void reset()
	{
		cleanup();
		loadEpisodeFileReader();
		epiFetched = fetchNext();
		houseFetched = new HouseHold();
		houseFetched.setH_householdCounter(-1);
	}

	// =============================================================================================================
	private void print(String msg)
	{
		System.out.println(msg);
		logger.info(msg);
	}
	// =============================================================================================================

	class EpiManagerHelper
	{
		
	}

	// =============================================================================================================
	/**
	 * creates the CSV file writer with specified file name. writes the complete list of persons to the file with same format as input episode file from FEATHERS. 
	 * @param epiFileName 
	 * @param personsList 
	 * @throws IOException 
	 */
	public void WritePersonsToFile(String epiFileName, ArrayList<IPerson> personsList) throws IOException
    {
		CsvWriter pWriter = new CsvWriter(new FileWriter(epiFileName, false), ';');
		writeHeader(pWriter);
		for (IPerson prs : personsList)
		{
			WritePersonToFile(prs, pWriter);
		}
		pWriter.close();
	    
    }
}
