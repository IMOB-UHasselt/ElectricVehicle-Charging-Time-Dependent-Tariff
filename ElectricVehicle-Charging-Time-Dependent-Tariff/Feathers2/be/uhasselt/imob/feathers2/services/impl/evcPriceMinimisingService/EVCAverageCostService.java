/**
 * 
 */
package be.uhasselt.imob.feathers2.services.impl.evcPriceMinimisingService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar;
import be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPerson;
import be.uhasselt.imob.feathers2.services.entityManagerService.ISchedule;
import be.uhasselt.imob.feathers2.services.entityManagerService.IBEV.FeasibleType;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar.CarType;
import be.uhasselt.imob.library.config.Config;

/**
 * @author MuhammadUsman
 *
 */
public class EVCAverageCostService extends EVCSimulationService
{
	// TODO Future implementation
	
	public EVCAverageCostService(Config cfg, IEntityManagerService entService, ICommonsService cmnSrv ) throws F2Exception 
	{
		super(cfg, entService, cmnSrv);
	}

	public String startEVCAvgCost()
	{
		return "Will be released soon";
	}
	
	//================================================================================'

	public String AnalyzeCostArray()
	{
		return "Will be implemted soon";
	}
}
