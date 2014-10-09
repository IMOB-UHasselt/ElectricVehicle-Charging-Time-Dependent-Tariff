package be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.production;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerConsumTracker;
import be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility.production.PowerConsumTracker;
import be.uhasselt.imob.library.config.Config;

import com.csvreader.CsvReader;

/**
 * This class provides the function to read the data and prepare the local grids data to be used and feed into the power grid station objects.
 * @author MuhammadUsman
 *
 */
public class PowerGridDataReader
{
	//======================================================================
	private String gridDataFileName;
	private String zonesLoadFileName;
	private String priceFileName = null;
	private String powerFileName = null;
	//======================================================================
	/** Logger */
	private static Logger logger = null;
	/**commonServices*/
	private ICommonsService commonService;
	/** Configurator to use. */
	private Config config = null;
	/**config load error indicator*/
	private boolean configError;
	private int nPrd;
	//======================================================================
	/**
	 * private construction.
	 * @param commonsSrv 
	 * @param config 
	 * @throws F2Exception 
	 */
	public PowerGridDataReader(Config config, ICommonsService commonsSrv) throws F2Exception
	{
		this.config = config;
		this.commonService = commonsSrv;

		if (logger == null)
			logger = Logger.getLogger(getClass().getName());

		priceFileName = fetchStringConfigValue("EVCPriceMinimiser:basicPriceFileName");
		powerFileName = fetchStringConfigValue("EVCPriceMinimiser:powerFileName");
		gridDataFileName = fetchStringConfigValue("EVCPriceMinimiser:GridsFile");
		zonesLoadFileName = fetchStringConfigValue("EVCPriceMinimiser:ZoneLoadFile");
		if (configError)
			throw new F2Exception("Consult log: PowerSupplyManager service instantiation failed");
		nPrd = WidrsConstants.NUMBER_OF_PERIODS_IN_ONE_DAY;
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

	//======================================================================
	public void readSubStationGridData()
	{


	}

	//======================================================================
	public void readPriceFromFile(float[] priceArray) throws NumberFormatException, IOException
	{
		CsvReader reader = new CsvReader(priceFileName);
		short i = 0;
		while(reader.readRecord())
		{
			priceArray[i] = (new Float(Float.parseFloat(reader.get(0))));  // Euro per KWh
			//				System.out.println("price["+i+"]=["+priceArray[i]+"]");
			i++;
		}
		reader.close();
	}


	//======================================================================
	public void readGlobalAvailablePowerFromFile(IPowerConsumTracker powerConsumTracker) throws IOException
	{
		CsvReader reader = new CsvReader(powerFileName);
		reader.readHeaders();
		float[] maxGlobalPower = new float[nPrd];
		int i = 0;
		while(reader.readRecord())
		{
			maxGlobalPower[i] = (1000 * (Float.parseFloat(reader.get(3))));
			i++;
		}
		powerConsumTracker.setMaxGlobalAvailPower(maxGlobalPower);
		reader.close();
		readLocalGridPowerLimitFromFile(powerConsumTracker);
	}

	//======================================================================
	public void readLocalGridPowerLimitFromFile(IPowerConsumTracker powerConsumTracker) throws IOException
	{
		CsvReader reader = new CsvReader(new FileReader(gridDataFileName), ';');
//		reader.readHeaders();
		while(reader.readRecord())
		{
			powerConsumTracker.addStation(new PowerGridStation(reader.get(0),Float.parseFloat(reader.get(1))));
		}
		reader.close();
		popolateListOfFeedZones(powerConsumTracker);
	}

	//======================================================================
	/**
	 * zonesLoadFileName: File containing the relation between zone and its feeds.This files have following headers
	 * 1: it is the TAZ number of the zone. i.e [1:2386]
	 * 2: is the feeder number from where it is feed. Note: feeder number is actually the line number of the feeder when it is read from the file.
	 * @param stations
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void popolateListOfFeedZones(IPowerConsumTracker powerConsumTracker) throws NumberFormatException, IOException
	{
		CsvReader reader = new CsvReader(new FileReader(zonesLoadFileName), ';');
//		reader.readHeaders();
		int gsNumber , zoneID;
		while(reader.readRecord())
		{
			gsNumber = Integer.parseInt(reader.get(1)) - 1; // numbering start from 1, so to make the indexing from 0 it is subtracted from 1
			zoneID = Integer.parseInt(reader.get(0));
			powerConsumTracker.addZoneToGrid(gsNumber,zoneID);
		}
		reader.close();
	}

	//======================================================================
	/**
	 * creates the price for each grid for each time unit
	 * @param stations
	 * @param priceArray
	 */
	public void populatePriceForStations(IPowerConsumTracker powerConsumTracker, float[] priceArray)
	{
		float[] price;
		ArrayList<PowerGridStation> stations = powerConsumTracker.getGridStation();
		for(PowerGridStation pg : stations)
		{
			price = new float[priceArray.length];
			
			for(int i = 0; i<price.length; i++)
			{
				price[i] = (float) priceArray[i];// * pg.getMaxPowerCap()/tot);
			}
			pg.setPrice(price);
		}

	}

	public String getPriceFileName()
    {
	    return this.priceFileName;
    }




}
