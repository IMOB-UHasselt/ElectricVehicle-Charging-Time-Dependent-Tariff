package be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility;

import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.commonsService.IEVC_Conventions;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.Person;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.Schedule;
import be.uhasselt.imob.library.config.Config;
import java.util.Random;
import org.apache.log4j.Logger;

/**
 * Abstract class for cars.
 * @author IMOB/Luk Knapen
 * @version $Id: Car.java 877 2013-07-04 07:09:17Z LukKnapen $
 Car data are collected in a separate class for following reasons:
   <ol>
      <li>Newer applications (like EVC-WIDRS) need detailed car attributes.</li>
      <li>A household can own zero or more cars.</li>
      <li>Car objects are created only if required (an EV is created only for a small fraction of the population). By using a separate class (instead of keeping car attributes in the {@link Schedule} or {@link Person} objects) memory can be saved since millions of {@link Schedule} and {@link Person} are instantiated.</li>
   </ol>
 */
public abstract class Car implements ICar 
{
	protected static String battSocVisualDir = null;
	protected static String battSocRadix = null;
	protected static String battSocExt = null;
	// ===================================================================
	protected static Random random = new Random();
	// ===================================================================
	/** Configurator to use. */
	protected static Config config = null;
	protected static boolean configError = false;
	protected static Logger logger = null;
	protected static ICommonsService commonsService = null;
	protected static IEVC_Conventions evcConventions = null;
	/** Cumulative probability function for car category according to government statistics. This probability weight function is used for all types.*/
	protected static double[] carCatProb = new double[3];
	// ===================================================================
	/** Car type. */
	protected CarType carType = CarType.ICEV;
	/** Car category. */
	protected short carCategory;

	// ===================================================================
	protected static double fetchDoubleConfigValue(String name)
	{
		double value = -1;
		String pValue = config.stringValueForName(name);
		value = Double.parseDouble(pValue);
		if (value == -1)
		{
			logger.error("Config error : no value for name=["+name+"]");
			configError = true;
		}
		return value;
	}
	
	
	// ===================================================================
	protected static float fetchFloatConfigValue(String name)
	{
		float value = -1;
		String pValue = config.stringValueForName(name);
		value = Float.parseFloat(pValue);
		System.out.println(name+":"+value);
		if (value == -1)
		{
			logger.error("Config error : no value for name=["+name+"]");
			configError = true;
		}
		return value;
	}	
	// ===================================================================
	protected static boolean fetchBooleanConfigValue(String name)
	{
		Boolean value = null;
		value = config.booleanValueForName(name);
		if (value == null)
		{
			logger.error("Config error : no value for name=["+name+"]");
			configError = true;
		}
		return value;
	}


	// ===================================================================
	protected static String fetchStringConfigValue(String name)
	{
		String value = null;
		value = config.stringValueForName(name);
		if (value == null)
		{
			logger.error("Config error : no value for name=["+name+"]");
			configError = true;
		}
		return value;
	}


	// ===================================================================
	protected static short fetchShortConfigValue(String name)
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

	// ===================================================================
	public static void registerConfig(Config cfg, ICommonsService commonsService2)
	{
		if (logger == null)
		{
			logger = Logger.getLogger("be.uhasselt.imob.feathers2.services.entityManagerService.Car");
		}
		config = cfg;
		Car.commonsService = commonsService2;
		evcConventions = commonsService.evcConventions();
		assert (config != null) : "Config not assigned yet";
		carCatProb[0] = fetchDoubleConfigValue("fleet:probCarCatSmall");
		carCatProb[1] = carCatProb[0] + fetchDoubleConfigValue("fleet:probCarCatMedium");
		carCatProb[2] = carCatProb[1] + fetchDoubleConfigValue("fleet:probCarCatLarge");
	}

	// ===================================================================
	/** Constructors defined in subclasses shall be used. 
	 * @param randCat Random Category: boolean, False if all Cars are of same category.*/
	protected Car(boolean randCat)
	{
		carType = CarType.ICEV;
		if(randCat)
			sampleCarCategory();
		else
			chooseCarCategory();
	}
	protected Car(){}
	// ===================================================================
	/**
	 * choose car category. fix at the moment. all cars belong to small category.
	 */
	private void chooseCarCategory()
    {
		carCategory = 0;
    }


	// ===================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ICar#carType()
	 */
	public CarType carType()
	{
		return carType;
	}

	// ===================================================================
	/** Set car type. */
	protected void setCarType(CarType carType)
	{
		this.carType = carType;
	}

	// ===================================================================
	/** sample car category. */
	private void sampleCarCategory()
	{
		double prob = random.nextDouble();
		carCategory = 0;
		while(prob >= carCatProb[carCategory]) carCategory++;
	}

	// ===================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ICar#pubRange()
	 */
	public abstract float pubRange();

	// ===================================================================
	/* (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.ICar#effRange()
	 */
	public abstract float effRange();
	
	// ===================================================================
	/*
	 * (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.entityManagerService.ICar#reset()
	 */
	public abstract void reset();
	public abstract ICar clone();



}
