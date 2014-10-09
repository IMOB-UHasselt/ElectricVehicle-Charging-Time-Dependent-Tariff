/**
 * 
 */
package be.uhasselt.imob.feathers2.services.impl.evcPriceMinimisingService;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import org.apache.log4j.Logger;

import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar;
import be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPerson;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerSupplyManager;
import be.uhasselt.imob.feathers2.services.entityManagerService.ISchedule;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar.CarType;
import be.uhasselt.imob.feathers2.services.impl.abstractService.Service;
import be.uhasselt.imob.library.base.Constants;
import be.uhasselt.imob.library.config.Config;

/**
 * @author MuhammadUsman
 *
 */
public class EVCSimulationService  extends Service
{

	// ===================================================================
	protected IEntityManagerService entityManagerService = null;
	protected  ICommonsService commonsService = null;
	protected IPowerSupplyManager powerSupplyManager = null;
	// ===================================================================
	protected boolean configError = false;

	// ===================================================================
	protected static Random random = new Random();
	
	protected boolean consoleOut= true;
	// ===================================================================

	/**Probability of Owners related to Car Types , prob(carType='PHEV'), prob(carType = 'BEV') = 1-prob(carType='PHEV')*/
	private double probPHEVCarTypeOwnership = Double.NEGATIVE_INFINITY ;
	// ===================================================================
	protected double priceOptimized = 0;
	protected double total = 0;
	protected int feasCnt = 0;

	protected boolean randomCategory;
	protected boolean fixedParam;
	// ===================================================================
	protected ArrayList<IPerson> prsnList = null;
	// ===================================================================
	public EVCSimulationService(Config cfg, IEntityManagerService entityManagerService, ICommonsService cmnSrv)
	{
		super(cfg);

		if (logger == null) logger = Logger.getLogger(getClass().getName());
		this.entityManagerService = entityManagerService;
		commonsService = cmnSrv;
		assert(this.entityManagerService != null && commonsService != null) : Constants.PRECOND_VIOLATION+" invalid state.";

		this.entityManagerService.setCarConfig(cfg);
		powerSupplyManager = entityManagerService.getpowerSupplyManager();
	}
	// ===================================================================

	protected void loadConfig()
	{
		probPHEVCarTypeOwnership = fetchFloatConfigValue("fleet:prob_PHEV_carType_ownership");
		randomCategory = fetchBooleanConfigValue("fleet:randomeCategory");
		fixedParam = fetchBooleanConfigValue("fleet:fixedParam");
		consoleOut = fetchBooleanConfigValue("EVCPriceMinimiser:consoleOut");

	}
	// ===================================================================
	protected void print(String msg) 
	{
		if(consoleOut )System.out.println(msg);
		logger.info(msg);
	}

	// =========================================================================
	protected boolean fetchBooleanConfigValue(String name)
	{
		boolean value;
		value = config.booleanValueForName(name);
		return value;
	}
	// =========================================================================
	protected float fetchFloatConfigValue(String name)
	{
		float value = -1;
		value = Float.parseFloat(config.stringValueForName(name));
		if (value == -1)
		{
			logger.error("Config error : no value for name=["+name+"]");
			configError = true;
		}
		return value;
	}

	// =========================================================================
	protected short fetchShortConfigValue(String name)
	{
		short value = -1;
		value = Short.parseShort(config.stringValueForName(name));
		if (value == -1)
		{
			logger.error("Config error : no value for name=["+name+"]");
			configError = true;
		}
		return value;
	}


	// =========================================================================
	protected String fetchStringConfigValue(String name)
	{
		String value = config.stringValueForName(name);
		if (value == null)
		{
			logger.error("Config error : no value for name=["+name+"]");
			configError = true;
		}
		return value;
	}
	//================================================================================'
	protected int fetchIntConfigValue(String name)
	{
		int value = 0;
		try
		{
			value = config.intValueForName(name);
		}
		catch (NumberFormatException nfe)
		{
			logger.error("Config error : no value for name=["+name+"] : Exception=["+nfe.getMessage()+"]");
			configError = true;
		}
		return value;
	}
	//================================================================================'
	protected void printStat(double time)
	{
		print("total = ["+total+"], needOptimized = ["+priceOptimized+"], ratio = ["+String.valueOf((double)100*priceOptimized/total)+"]");
		print("total time for ["+total+"] BEV = ["+time+"]");
		print("time average for one car = ["+ (time)/total+"]");

	}
	//================================================================================'
	protected void cleanup() 
	{
	}	
	//================================================================================'
	protected void reset()
	{
		total = 0;
		priceOptimized = 0;
		//		writeCostDirReset = true;
		powerSupplyManager.resetPowerConsuption();
		for(IPerson prs : prsnList)
		{
			ICar car = prs.getSchedule().getCar();
			if(car != null && car.carType() == CarType.BEV) car.reset();
		}
	}
	//================================================================================'

	protected float registerFixedBEV(ArrayList<IPerson> prsnList) 
	{
		print("populateSchedulesWithBasicChargingCosts started");
		int BEVcnt = 0;
		feasCnt = 0;
		float maxSwPower = 0f;
		for(IPerson prs : prsnList)
		{
			ISchedule sched = prs.getSchedule();

			if (sched .containsCarTrips())
			{
				//All the cars are BEV. This is assumed to get the charging cost for each vehicle during each run. Charging cost for each vehicle is required to calculate the avg. cost for all runs.

				ICar bev = entityManagerService.createAndGetBEV(randomCategory,fixedParam);
				if(bev.is_BEV_feasible(new BitSet(), sched.getActivitiesList()) == true) //TODO:{MU}:define charging opportunity bitset 
				{
					bev.registerPowerCapManager(powerSupplyManager);
					bev.registerChargingSlotsArray(entityManagerService.getDuplicateChargingSlotsArray());
					if(maxSwPower < bev.getMaxSwitchPower()) maxSwPower = bev.getMaxSwitchPower();
					sched.setCar(bev);
					feasCnt++;
				}
			}
		}

		print("BEVcnt=["+BEVcnt+"], all=["+prsnList.size()+"], %OfBev=["+String.valueOf((double)BEVcnt/prsnList.size())+"]");
		print("feasCnt=["+feasCnt+"], BEVcnt=["+BEVcnt+"], %feasible(feasible/allBEV)=["+String.valueOf((double)feasCnt/BEVcnt)+"]");
		print("populateSchedulesWithBasicChargingCosts finished");
		return maxSwPower;
	}

	//====================================================================================

	protected void registerCars(ArrayList<IPerson> prsnList) 
	{
		print("populateSchedulesWithBasicChargingCosts started");
		int BEVcnt = 0;
		feasCnt = 0;
		int SchedcontainingarTrips = 0;
		for(IPerson prs : prsnList)
		{
			ISchedule sched = prs.getSchedule();
			// TODO:{LK,MU}:02 We shall find out if the episode contains car trips.
			//                 Then we shall use a "carType selector" here.
			//                 That shall determine the car type for the schedule probabilistically.
			//                 If applicable, a car is to be created.
			if (sched .containsCarTrips())
			{
				SchedcontainingarTrips++;
				CarType ct = carTypeFromMarketShares();
				switch (ct)
				{
					case BEV :

						BEVcnt++;
						ICar bev = entityManagerService.createAndGetBEV(randomCategory,fixedParam);
						if(bev.is_BEV_feasible(new BitSet(), sched.getActivitiesList()) == true) //TODO:{MU}:define charging opportunity bitset 
						{
							bev.registerPowerCapManager(powerSupplyManager);
							bev.registerChargingSlotsArray(entityManagerService.getDuplicateChargingSlotsArray());
							sched.setCar(bev);
							feasCnt++;
						}
						break;
					case PHEV :
						break;

					case ICEV :
						break;
					default : assert (false) : Constants.SOFTWARE_ERROR;
					break;
				}
			}
		}

		print("all=["+prsnList.size()+"], carTrips=["+SchedcontainingarTrips+"], BEVcnt=["+BEVcnt+"],  %OfBev=["+String.valueOf((double)BEVcnt/SchedcontainingarTrips)+"]");
		print("feasCnt=["+feasCnt+"], BEVcnt=["+BEVcnt+"], %feasible(feasible/allBEV)=["+String.valueOf((double)feasCnt/BEVcnt)+"]");
		print("populateSchedulesWithBasicChargingCosts finished");
	}

	//================================================================================
	/** Determine car type based on market shares read from config file. */
	private CarType carTypeFromMarketShares()
	{
		CarType ct;
		double prob = random.nextDouble();
		//		System.out.println("prob=["+prob+"], probPHEVCarTypeOwnership["+probPHEVCarTypeOwnership+"]");
		ct = (prob <= probPHEVCarTypeOwnership) ? CarType.PHEV : CarType.BEV;
		return ct;
	}



}

