/**
 * 
 */
package be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.production;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IActivity;
import be.uhasselt.imob.feathers2.services.entityManagerService.IChargingSlot;
import be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerConsumTracker;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerSupplyManager;
import be.uhasselt.imob.library.config.Config;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * @author MuhammadUsman
 * 
 */
public class PowerSupplyManager implements IPowerSupplyManager
{

	private String electricStatDir = null;
	private String pwrStatGnuFile = null;
	private String priceStatGnuFile = null;
	private String gnuExt = null;
	// =============================================================================================================
	/** Logger */
	private static Logger logger = null;
	/**commonServices*/
	private ICommonsService commonService;
	/** Configurator to use. */
	private Config config = null;
	/** Entity Manager Service. */
	private IEntityManagerService entityManagerService;
	// =============================================================================================================
	private PowerConsumTracker powerConsumTracker = null;
	private PowerGridDataReader dataReader = null;
	private int[] sortedInd = null;
	private float[] priceArray = null;
	// =============================================================================================================
	private boolean configError;
	/**number of periods in a day*/
	private int nPrd;

	// =========================================================================
	public PowerSupplyManager(Config config, ICommonsService commonsSrv) throws F2Exception
	{
		assert (config != null) : Constants.PRECOND_VIOLATION + "config == null";

		if (logger == null)
			logger = Logger.getLogger(getClass().getName());
		dataReader = new PowerGridDataReader(config, commonsSrv);
		powerConsumTracker = new PowerConsumTracker();
		this.config = config;
		this.commonService = commonsSrv;

		electricStatDir = fetchStringConfigValue("EVCPriceMinimiser:electricStatDir");
		pwrStatGnuFile = fetchStringConfigValue("EVCPriceMinimiser:pwrStatGnuFile");
		gnuExt = fetchStringConfigValue("EVCPriceMinimiser:gnuExt");

		this.nPrd = WidrsConstants.NUMBER_OF_PERIODS_IN_ONE_DAY;

		priceArray = new float[nPrd];

		if (configError)
			throw new F2Exception("Consult log: PowerSupplyManager service instantiation failed");


		//		readFromFile();
	}

	// ===================================================================

	private float fetchFloatConfigValue(String name)
	{
		float value = Float.NEGATIVE_INFINITY ;
		value = (float) config.doubleValueForName(name);
		if (value == Float.NEGATIVE_INFINITY )
		{
			logger.error("Config error : no value for name=["+name+"]");
			configError = true;
		}
		return value;
	}


	// ===================================================================
	private String fetchStringConfigValue(String name)
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


	// =============================================================================================================
	private void print(String msg)
	{
		System.out.println(msg);
		logger.info(msg);
	}
	// =============================================================================================================

	public void preparePriceAndCapacityData()
	{
		try
		{
			dataReader.readPriceFromFile(priceArray);
			dataReader.readGlobalAvailablePowerFromFile(powerConsumTracker);
			dataReader.populatePriceForStations(powerConsumTracker, priceArray);
		} catch (NumberFormatException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}



	// =========================================================================

	@Override
	public IPowerConsumTracker getDuplicateTracker()
	{

		IPowerConsumTracker dupTracker = new PowerConsumTracker(powerConsumTracker);
		return dupTracker;
	}


	// =========================================================================

	@Override
	public void updatePowerTracker(IPowerConsumTracker pcTracker)
	{
		powerConsumTracker.updatePower(pcTracker);
	}

	// =========================================================================
	@Override
	public void writeAvailablePowerGraphData()
	{
		print("writing plt graph data for consumed power");
		writeAPdataInotherFormat();

		String fName = pwrStatGnuFile+gnuExt;
		commonService.deleteDirRecusively(fName);

		try
		{
			File dir = new File(electricStatDir);
			if(!dir.exists())
				dir.mkdir();
			File file = new File(fName );
			file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("set terminal pngcairo size 800,600 enhanced font 'Verdana,10'  \n");
			bw.write("set output \""+pwrStatGnuFile+".png\"\n");
			bw.write("set title \"Power Consumption [KW]\"\n");
			bw.write("set ylabel \"Power[KW]\"\n");
			bw.write("set xlabel \"period of day 15 [min] each\"\n");

			int obj = 0;
			double yRange = Float.NEGATIVE_INFINITY ;
			int i = 0;
			for(; i < nPrd; i++)
			{
				obj++;
				bw.write("set object "+obj+" rect from "+i+",0 to "+(i+1)+","+powerConsumTracker.getMaxGlobalPower(i)+" fc rgb \"sea-green \"\n");
				obj++;
				bw.write("set object "+obj+" rect from "+i+",0 to "+(i+1)+","+powerConsumTracker.getGlobalConsumedPower(i)+" fc rgb \"coral\"\n");
				if(powerConsumTracker.getMaxGlobalPower(i) > yRange) yRange = powerConsumTracker.getMaxGlobalPower(i);
			}   
			yRange += yRange * 0.05;
			bw.write("plot [0:"+i+"] [0:"+yRange+"] NaN notitle");
			bw.close();

		} 
		catch (IOException e)
		{
			logger.error("failed to create the file buffered writer.");
			logger.error(e.getMessage());
		} 
	}

	// =========================================================================

	private void writeAPdataInotherFormat()
	{
		String dif = "_0";
		String fName = pwrStatGnuFile+dif +gnuExt;
		commonService.deleteDirRecusively(fName);

		try
		{
			File dir = new File(electricStatDir);
			if(!dir.exists())
				dir.mkdir();
			File file = new File(fName );
			file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("set terminal pngcairo size 800,600 enhanced font 'Verdana,10'  \n");
			bw.write("set output \""+pwrStatGnuFile+dif+".png\"\n");
			bw.write("set title \"Power Consumption [KW]\"\n");
			bw.write("set ylabel \"Power[KW]\"\n");
			bw.write("set xlabel \"period of day 15 [min] each\"\n");



			bw.write("plot \"-\"  title 'Available Power' with lines, \"-\"  title 'consumed Power' with lines"+"\n");

			int obj = 0;
			double yRange = Float.NEGATIVE_INFINITY ;
			int i = 0;
			long tot = 0;
			for(; i < nPrd; i++)
			{
				bw.write((i+1)+" "+ powerConsumTracker.getMaxGlobalPower(i)+"\n");
				if(powerConsumTracker.getMaxGlobalPower(i) > yRange) yRange = powerConsumTracker.getMaxGlobalPower(i);
				tot += powerConsumTracker.getMaxGlobalPower(i);
			}  
			print("totalAvailablePower=["+tot+"]");
			bw.write("e"+"\n");
			i = 0;
			tot = 0;
			for(; i < nPrd; i++)
			{
				bw.write((i+1)+" "+ powerConsumTracker.getGlobalConsumedPower(i)+"\n");
				if(powerConsumTracker.getGlobalConsumedPower(i) > yRange) yRange = powerConsumTracker.getMaxGlobalPower(i);
				tot += powerConsumTracker.getMaxGlobalPower(i);
			}  
			print("totalConsumedPower=["+tot+"]");

			bw.close();

		} 
		catch (IOException e)
		{
			logger.error("failed to create the file buffered writer.");
			logger.error(e.getMessage());
		} 

	}

	public float[] getPriceArray() 
	{
		return priceArray ;
	}



	// =========================================================================
	public float getPriceAtPrd(int prd)
	{
		return priceArray[prd];
	}

	// =========================================================================
	@Override
	public int getDOP(float maxSwPower)
	{
		double minAvailPower = powerConsumTracker.getMinAvailablePower();
		int dop = (int) Math.floor(minAvailPower/maxSwPower);
		if(dop == 0) dop = 1;
		//	    System.out.println("dop=["+dop+"], minAvailPower=["+minAvailPower+"], maxSwPower=["+maxSwPower+"]");
		return dop;
	}
	// =========================================================================
	@Override
	public void resetPowerConsuption()
	{
		float[] consumedPower = new float[nPrd];
		for(int  i = 0; i < nPrd; i++)
		{
			consumedPower[i] = 0f;
		}
		powerConsumTracker.setGlobalConsumedPower(consumedPower);
	}

	@Override
	public String priceFileName()
	{
		File f = new File(dataReader.getPriceFileName());
		if(!f.exists())
			try
		{
				f.createNewFile();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f.getName();
	}

	@Override
	public String priceDir()
	{
		return  new File(dataReader.getPriceFileName()).getParentFile().getAbsolutePath();
	}

	public int[] getSortedIndices()
	{
		if(sortedInd == null)
		{
			Map<Float, Integer> indices = new HashMap<Float, Integer>();
			for (int index = 0; index < priceArray.length; index++) 
			{
				indices.put(priceArray[index], index);
			}

			float[] copy = Arrays.copyOf(priceArray, priceArray.length);
			sortedInd = new int[copy.length];
			Arrays.sort(copy);
			for (int index = 0; index < copy.length; index++)
			{
				copy[index] = indices.get(copy[index]);
				sortedInd[index] = (int) copy[index];
			}
		}
		return sortedInd;

	}
	// =========================================================================
	// =========================================================================

	@Override
    public boolean isBlocked(int prd)
    {
	   return powerConsumTracker.isBlocked(prd);
    }


}
