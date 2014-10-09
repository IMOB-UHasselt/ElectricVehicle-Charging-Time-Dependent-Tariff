package be.uhasselt.imob.feathers2.services.impl.evcPriceMinimisingService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.csvreader.CsvWriter;

import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IActivity;
import be.uhasselt.imob.feathers2.services.entityManagerService.IBEV.FeasibleType;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar;
import be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPerson;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerSupplyManager;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar.CarType;
import be.uhasselt.imob.feathers2.services.entityManagerService.ISchedule;
import be.uhasselt.imob.library.config.Config;

/**
 * This class serves as the integration of EVC optimization model with Vito's
 * model. It produces all the required data what Price predictor (Matlab, VITO)
 * requires in right format.
 * 
 * @author MuhammadUsman
 * 
 */
public class EVC_VitoIntegrationService extends EVCSimulationService
{
	// TODO Future implementation

	public EVC_VitoIntegrationService(Config cfg, IEntityManagerService entService, ICommonsService cmnSrv) throws F2Exception
	{
		super(cfg, entService, cmnSrv);
	}

	public String startEVIntegVito()
	{
		return "Will be released soon";
	}
}
