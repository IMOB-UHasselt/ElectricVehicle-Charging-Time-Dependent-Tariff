package be.uhasselt.imob.feathers2.services.impl.entityManagerService;

import java.io.File;
import java.io.IOException;
import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.library.config.Config;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IBEV;
import be.uhasselt.imob.feathers2.services.entityManagerService.IChargingSlot;
import be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService;
import be.uhasselt.imob.feathers2.services.entityManagerService.IHouseHold;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPerson;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerSupplyManager;
import be.uhasselt.imob.feathers2.services.entityManagerService.ISchedule;
import be.uhasselt.imob.feathers2.services.environmentService.IEnvironmentService;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.BEV;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.Car;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.ChargingSlot;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.production.PowerSupplyManager;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.csvreader.CsvWriter;

/**
 * This class provides access to the episode from the predicted schedule file.
 * and return the episodes.
 * @author MuhammadUsman
 *
 */
public class EntityManagerService implements IEntityManagerService
{

	/**EnvironmentService*/
	private IEnvironmentService environmentService;
	/**CommonService*/
	private ICommonsService commonsService;
	/**Config*/
	private Config config;
	/** Logger */
	private Logger logger = null ;
	// ============================================================================
	private EpisodeManger epiManger;
	private IPowerSupplyManager powerSupplyManager = null;

	// ============================================================================
	/**This is a data container class, it contains entities in three different format.*/
	/**HouseList contains all houses containing all attributes related to the house, this House does not know about the <code>person</code> and <code>car</code> belonging to it.*/
	ArrayList<IHouseHold> housesList = null;

	/**personlvlList contains complete list of persons. one <code>person</code> contains all attribute related to the person and one <code>schedule</code>*/
	ArrayList<IPerson> personLvlList = null;
	ArrayList<ISchedule> schedulelvlList = null;
	// ============================================================================

	/**
	 * @param cfg
	 * @param commonsSrv 
	 * @throws F2Exception
	 */
	public EntityManagerService(Config cfg, ICommonsService commonsSrv, IEnvironmentService envirSrv) throws F2Exception
	{
		assert (cfg != null) : Constants.PRECOND_VIOLATION + "config == null";
		assert (commonsSrv!= null) : Constants.PRECOND_VIOLATION + "commonsSrv== null";
		assert (envirSrv!= null) : Constants.PRECOND_VIOLATION + "envirSrv== null";

		if (logger == null) logger = Logger.getLogger(getClass().getName());

		this.commonsService = commonsSrv;
		this.environmentService = envirSrv;
		this.config = cfg;

		epiManger = new EpisodeManger(cfg, commonsService, environmentService);
		powerSupplyManager = new PowerSupplyManager(cfg, commonsService);
	}


	// ============================================================================

	public void cleanup()
	{

	}

	// ============================================================================

	@Override
	public String getCheckSumValueOfEpisodeScheduleFile()
	{
		return epiManger.getCheckSumValueOfEpisodeScheduleFile();
	}

	// ============================================================================

	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.qq#getHouseLvlList()
	 */
	@Override
	public ArrayList<IHouseHold> getHouseLvlList() 
	{
		if(housesList == null) populateHouseLvlList();
		return housesList;
	}


	private void populateHouseLvlList()
	{
		getPersonLvlList();

		housesList = new ArrayList<IHouseHold>();
		for(IPerson prs : personLvlList)
		{
			housesList.add(prs.getHouse());
		}

	}


	// ============================================================================
	@Override
	public int getNumberOfSubZones()
	{
		return epiManger.getNumberOfSubZones();
	}


	// ============================================================================

	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.qq#getPersonLvlList()
	 */
	@Override
	public ArrayList<IPerson> getPersonLvlList() 
	{
		if(personLvlList == null) populatePersonsList();
		return personLvlList;
	}

	// ============================================================================

	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.qq#getSchedulelvlList()
	 */
	@Override
	public ArrayList<ISchedule> getSchedulelvlList() 
	{
		if(schedulelvlList == null) populateSchedulesList();
		return schedulelvlList;
	}




	// ============================================================================

	@Override
	public boolean inputEpisodeFileExists()
	{
		return epiManger.inputEpisodeFileExists();
	}


	// ============================================================================


	private void populatePersonsList()
	{
		print("populatePersonsList started.");
		reset();
		personLvlList = new ArrayList<IPerson>();
		Person prs = epiManger.fetchNextPerson();
		while(prs != null)
		{
			personLvlList.add(prs);
			prs = epiManger.fetchNextPerson();
		}

	}


	// ============================================================================
	private void populateSchedulesList() 
	{
		getPersonLvlList();

		schedulelvlList = new ArrayList<ISchedule>();

		for(IPerson prs : personLvlList)
		{
			schedulelvlList.add(prs.getSchedule());
		}
	}

	// ============================================================================

	private void print(String msg) 
	{
		System.out.println(msg);
		logger.info(msg);
	}

	// ============================================================================

	@Override
	public String readCheckSumValueFromFile(String checkSumValueFile)
	{
		return epiManger.readCheckSumValueFromFile(checkSumValueFile);
	}

	// ============================================================================

	@Override
	public void reset()
	{
		epiManger.reset();
	}


	// ============================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService#setFileName(java.lang.String)
	 */
	public void setFileName(String fileName) 
	{

	}


	// ============================================================================
	/**
	 * @param houseLvlList the houseLvlList to set
	 */
	public void setHouseLvlList(ArrayList<IHouseHold> houseLvlList) 
	{
		this.housesList = houseLvlList;
	}


	// ============================================================================
	/**
	 * @param schedulelvlList the schedulelvlList to set
	 */
	public void setSchedulelvlList(ArrayList<ISchedule> schedulelvlList) 
	{
		this.schedulelvlList = schedulelvlList;
	}




	// ============================================================================

	@Override
	public void writeNewCheckSumValueToFile(String fileName, String value)
	{
		epiManger.writeNewCheckSumValueToFile(fileName, value);
	}

	// ============================================================================



	// ============================================================================
	@Override
	public IBEV createAndGetBEV(boolean randCat, boolean fixedPar)
	{
		if (fixedPar)
			return new BEV(randCat, fixedPar);
		else
			return new BEV(randCat);
	}


	// ============================================================================
	@Override
	public void setCarConfig(Config cfg)
	{
		Car.registerConfig(cfg,commonsService);
	}


	// ============================================================================
	@Override
	public IChargingSlot createChargingSlot()
	{
		return new ChargingSlot();
	}


	// ============================================================================
	@Override
	public IChargingSlot[] getDuplicateChargingSlotsArray()
	{
		IChargingSlot[] slots = new ChargingSlot[WidrsConstants.NUMBER_OF_PERIODS_IN_ONE_DAY];
		int ind = 0;
		for(IChargingSlot chSlot : slots)
		{
			slots[ind] = new ChargingSlot();
			ind++;
		}
		return slots;
	}

	// ============================================================================

	@Override
	public IPowerSupplyManager getpowerSupplyManager()
	{
		return powerSupplyManager;
	}

	// ============================================================================
	@Override
	public ArrayList<IPerson> getFilteredPerListWithZones(ArrayList<Short> zones)
	{
		ArrayList<IPerson> prsList = getPersonLvlList();
		ArrayList<IPerson> fltrList = new ArrayList<IPerson>();
		for(IPerson prs : prsList)
		{
			if(prs.hasAllActInZoneList(zones))
				fltrList.add(prs);
		}
		
		return fltrList;	
	}

	// ============================================================================
	@Override
	public ArrayList<IPerson> getPersonsListHomeActRedef()
	{
		ArrayList<IPerson> prsList = getPersonLvlList();
		for(IPerson prs : prsList)
		{
//			print(prs.toString());
			prs.getSchedule().redefFirstAndLastHomeActivity();
//			print("================================================================================================================");
//			print(prs.toString());
//			print("################################################################################################################");
		}
		return prsList;	
	}
	// ============================================================================

	@Override
	/*
	 * (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService#getPersonLvlList(java.lang.String)
	 */
	public ArrayList<IPerson> getPersonLvlList(String epiFile) throws IOException
	{
		print("Starts reading Episode file : "+epiFile);

	    EpisodeManger epiMgr = new EpisodeManger(epiFile, commonsService, environmentService);
	    ArrayList<IPerson> prsList = new ArrayList<IPerson>();
	    Person prs = epiMgr.fetchNextPerson();
		
		while(prs != null)
		{
			prsList.add(prs);
			prs = epiMgr.fetchNextPerson();
		}
		return prsList;
	}

	// ============================================================================

	@Override
	public void writeEpisodeFile(String episodeFileBeforeRescheduling, ArrayList<IPerson> personsList) throws IOException
	{
		commonsService.createParentDir(new File(episodeFileBeforeRescheduling));
		epiManger.WritePersonsToFile(episodeFileBeforeRescheduling, personsList);
	}
}
