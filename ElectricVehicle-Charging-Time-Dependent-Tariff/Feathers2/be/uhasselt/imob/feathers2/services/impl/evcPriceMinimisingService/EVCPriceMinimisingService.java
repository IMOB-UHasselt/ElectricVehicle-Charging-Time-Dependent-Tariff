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
import java.util.Random;

import org.apache.log4j.Logger;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IActivity;
import be.uhasselt.imob.feathers2.services.entityManagerService.IBEV.FeasibleType;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar.CarType;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar;
import be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPerson;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerSupplyManager;
import be.uhasselt.imob.feathers2.services.entityManagerService.ISchedule;
import be.uhasselt.imob.feathers2.services.evcPriceMinimisingService.IEVCPriceMinimisingService;
import be.uhasselt.imob.feathers2.services.impl.abstractService.Service;
import be.uhasselt.imob.library.config.Config;


/**
 * 
 * 
 * @author MuhammadUsman
 *
 */
public class EVCPriceMinimisingService extends EVCSimulationService implements IEVCPriceMinimisingService
{

	// ===================================================================





	public EVCPriceMinimisingService(Config cfg, IEntityManagerService entService, ICommonsService cmnSrv ) throws F2Exception 
	{
		super(cfg, entService, cmnSrv);
		loadConfig();

		if (configError) throw new F2Exception("EVCPriceMinimisingService instantiation failed : consult log");
	}


	protected void loadConfig()
    {
		super.loadConfig();
    }


	



	//================================================================================'
	@Override
	public String startEVC()
	{
		print("EVC Cost Minimser Simulation started");
		prsnList = entityManagerService.getPersonsListHomeActRedef();
		sortSchedulesWithStTime(prsnList);
		powerSupplyManager.preparePriceAndCapacityData();
		long tm = 0;
		Calendar startTime ;
		registerCars(prsnList);

		int feasible = 0, softInfeasible= 0, hardInfeasible = 0;
		startTime = Calendar.getInstance(); 

		reset();
		for(IPerson prs : prsnList)
		{
			ICar car = prs.getSchedule().getCar();
			ISchedule schd = prs.getSchedule();
			if(car != null && car.carType() == CarType.BEV)
			{
				FeasibleType fType = schd.analyseBatChargConsum(prs.getP_personCounter());
				if( fType == FeasibleType.Feasible)
				{
					feasible++;
				}
				else 
				{
					priceOptimized++;
					if( fType == FeasibleType.SoftInfeasible)
					{
						softInfeasible++;
						System.out.println("Soft Infeasible => pCount=["+prs.getP_personCounter()+"]");
					}
					else
					{
						hardInfeasible++;
						System.out.println("Hard Infeasible => pCount=["+prs.getP_personCounter()+"]");
					}
				}
				total++;
			}
		}
		
		
		print("total=["+total+"], feasible=["+feasible+"], softInfeasible=["+softInfeasible+"], hardInfeasible=["+hardInfeasible+"]");

		print("total=["+total+"], needOptimization=["+priceOptimized+"], ratio=["+((double)(priceOptimized/total))+"]");

		print("total Time [ms]=["+tm+"], avg=["+(tm/total)+"]");
		
		printStat(Calendar.getInstance().getTimeInMillis() -  startTime.getTimeInMillis());
		powerSupplyManager.writeAvailablePowerGraphData();


		print("EVC Cost Minimser Simulation  finished");
		return "Completed";
	}

	//================================================================================'
	/**
	 * sorts the list of person w.r.t to the starting time of the perosnal agendas
	 * @param prsnList 
	 */

	private void sortSchedulesWithStTime(ArrayList<IPerson> prsnList)
    {
		Collections.sort(prsnList);
//		String str;
//		int i;
//		for(IPerson p : prsnList)
//		{
//			p.toString();
//			str = "";
//			i = 0;
//			ISchedule s = p.getSchedule();
//			for(IActivity a : s.getActivitiesList())
//			{
//				str = str.concat("{j"+i+"="+a.getJ_beginningTime()+" s"+i+"="+a.getA_beginningTime_minOfDay()+" e"+i+"="+(a.getA_beginningTime_minOfDay()+a.getA_duration())+"}, ");
//				i++;
//			}
//			System.out.println("pCount=["+p.getP_personCounter()+"], S=["+str+"]");
//		}
    }

	//================================================================================'







}
