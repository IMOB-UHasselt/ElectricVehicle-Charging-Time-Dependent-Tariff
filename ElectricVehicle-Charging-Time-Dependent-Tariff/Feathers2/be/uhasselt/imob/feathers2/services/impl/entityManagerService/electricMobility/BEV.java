package be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;


import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.entityManagerService.IActivity;
import be.uhasselt.imob.feathers2.services.entityManagerService.IBEV;
import be.uhasselt.imob.feathers2.services.entityManagerService.ICar;
import be.uhasselt.imob.feathers2.services.entityManagerService.IChargingSlot;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerConsumTracker;
import be.uhasselt.imob.feathers2.services.entityManagerService.IPowerSupplyManager;
import be.uhasselt.imob.feathers2.services.entityManagerService.IBEV.FeasibleType;
import be.uhasselt.imob.feathers2.services.entityManagerService.ISchedule;


/**
 * Concrete class for BEV.
 * @author IMOB/Luk Knapen
 * @version $Id: BEV.java 1471 2014-08-25 11:03:00Z MuhammadUsman $
   Notes on attributes
   <ol>
      <li>The distance specific consumption is sampled from distributions for which the parameters are read from the config file. At software creation time (2013-jul-02) the mechanisms used in the <code>smartGrid.py</code> code and in this class, were identical.
      <li><code>float</code> is used for the specific consumption since it is sufficiently accurate and consumes 4 bytes of memory (instead of 8 for a <code>double</code>). Remember that millions of schedules are kept in meomory.
      <li>Note that (like in <code>smartGrid.py</code> code, there is not yet any provision to take seasonal effects into account. EV are known to have a lower range during winter because of electric energy required for <em>cabin heating</em>.</li>
   </ol>
 */
public class BEV extends Car implements IBEV
{

	private static IPowerSupplyManager powerSupplyManager = null;
	// ===================================================================
	/** Array specifying for each of 3 car categories (small, medium, large) the lower and upper bounds for the distance specific consumption.
	       The actual consumption is sampled from a uniform distribution for which the range is limited by the given bounds. */
	protected static double[][] carCatConsumLimits = null;

	/** Array specifying for each of 3 car categories the published range[km]. */
	protected static double[] carRange = null;

	/** Array specifying for each of 3 car categories the probability to get small (3300W) at home.  and [probability for large (10000W) charger at home = 1 - (probability of small (3300W) charger at home)]. */
	protected static double[] chargerTypesAtHome = null;

	/** Array specifying for each of 3 car categories the probability to get small (3300W) at work.  and [probability for large (10000W) charger at work = 1 - (probability of small (3300W) charger at work)]. */
	protected static double[] chargerTypesAtWork = null;

	/** Power Capacity of the smaller charger [KW]. initialized with Short.min [-(2^15)]*/
	protected static float smallChargerCap = Short.MIN_VALUE;

	/** Power Capacity of the larger charger [KW]  initialized with Short.min [-(2^15)]*/
	protected static float largeChargerCap = Short.MIN_VALUE;

	/**if charging evaluation to write in directory for each car.*/
	protected  static  boolean writeToFile = true;

	/**  flag which is used to control available power limit for charging at any moment in time. if this flag is false, cars can charge energy as much as possible at any time.*/
	protected static Boolean energyLimitCap = null;
	// ===================================================================
	/** Distance specific consumption [kWh/km] for electric vehicle. If the vehicle is not an EV, <code>evDistSpecifConsum</code> equals {@link java.lang.Float#MAX_VALUE} . */
	private float evDistSpecifConsum = Float.MAX_VALUE;
	/** Officially published car range [km]. This is larger than the effectively usable range. */
	protected float pubRange = 0;
	/** Battery capacity [kWh]  */
	protected float battCap;
	/** Deepest Capacity Depletion: minimal relative battery capacity allowed. */
	protected static float dcd;
	/** <em>Official</em> to <em>real></em> range reduction factor: apply this one to the range determined using standardized tests because those standard tests are known to over-estimate the range. */
	private float off2realCorrection;
	/**charger type available at home, capacity in [KW] :  small (3300W) or large (10000W)*/
	private float chargerPwrAtHome;
	/**charger type available at work, capacity in [KW] :  small (3300W) or large (10000W)*/
	private float chargerPwrAtWork;
	/**battery charging level [KWh]*/
	private float battSOC;
	private IChargingSlot[] chargingSlot = null;
	/**total charging cost*/
	private double basicCost;
	/**indicator to reset the battery SOC visual directory.*/
	private static boolean battSocDirReset = false;
	/**epsilon value to compare the float values*/
	private float epsilon = 0.005f;
	/**Total charging drawn from grid*/
	private float totCharging;
	private String s = "";

	// ===================================================================
	/** Constructor. 
	 * @param randCat Random category: boolean, False if all BEV of same category.*/
	public BEV(boolean randCat)
	{
		super(randCat);
		setCarType(CarType.BEV);
		loadConfigValues();

		double lowConsum = carCatConsumLimits[carCategory][0];
		double highConsum = carCatConsumLimits[carCategory][1];
		double probForSmallChargerHome = chargerTypesAtHome[carCategory];
		double probForSmallChargerWork = chargerTypesAtWork[carCategory];

		evDistSpecifConsum = (float)(lowConsum + random.nextDouble()*(highConsum - lowConsum));
		chargerPwrAtHome = (random.nextDouble() <= probForSmallChargerHome) ? smallChargerCap : largeChargerCap;
		chargerPwrAtWork = (random.nextDouble() <= probForSmallChargerWork) ? smallChargerCap : largeChargerCap;

		battCap = pubRange()*evDistSpecifConsum;
		battSOC = intitialSOC(); // TODO:{MU}: Initial battery state can vary from empty to full depending upon the case, should move to Config or read from file;
		basicCost = 0.0f;

	}
	public BEV(){}

	public BEV(boolean randCat, boolean fixedPar)
	{
		super(randCat);
		setCarType(CarType.BEV);
		loadConfigValues();

		double lowConsum = carCatConsumLimits[carCategory][0];
		double highConsum = carCatConsumLimits[carCategory][1];

		evDistSpecifConsum = (float)(lowConsum + 0.1 * (highConsum - lowConsum));
		battCap = pubRange()*evDistSpecifConsum;

		chargerPwrAtHome =  smallChargerCap;
		chargerPwrAtWork = largeChargerCap;

		battCap = pubRange()*evDistSpecifConsum;
		battSOC = intitialSOC(); // TODO:{MU}: Initial battery state can very from empty to full depending upon the case, should move to Config or read from file;
		basicCost = 0.0f;
	}

	private void loadConfigValues()
	{
		if (carCatConsumLimits == null)
		{
			carCatConsumLimits = new double[3][2];

			carCatConsumLimits[0][0] = fetchDoubleConfigValue("fleet:bev_SpecifConsum_small_lowLimit");
			carCatConsumLimits[0][1] = fetchDoubleConfigValue("fleet:bev_SpecifConsum_small_highLimit");
			carCatConsumLimits[1][0] = fetchDoubleConfigValue("fleet:bev_SpecifConsum_medium_lowLimit");
			carCatConsumLimits[1][1] = fetchDoubleConfigValue("fleet:bev_SpecifConsum_medium_highLimit");
			carCatConsumLimits[2][0] = fetchDoubleConfigValue("fleet:bev_SpecifConsum_large_lowLimit");
			carCatConsumLimits[2][1] = fetchDoubleConfigValue("fleet:bev_SpecifConsum_large_highLimit");

			carRange = new double[3];
			carRange[0] = fetchDoubleConfigValue("fleet:bev_RangeSmall");
			carRange[1] = fetchDoubleConfigValue("fleet:bev_RangeMedium");
			carRange[2] = fetchDoubleConfigValue("fleet:bev_RangeLarge");

			off2realCorrection = (float)fetchDoubleConfigValue("fleet:bev_Off2realCorrection");
			dcd = fetchFloatConfigValue("fleet:bev_DCD_FRAC");
		}

		if(chargerTypesAtHome == null)
		{
			chargerTypesAtHome= new double[3];
			chargerTypesAtHome[0] = fetchDoubleConfigValue("fleet:chargePwr_small_veh_small_home");
			chargerTypesAtHome[1] = fetchDoubleConfigValue("fleet:chargePwr_small_veh_medium_home");
			chargerTypesAtHome[2] = fetchDoubleConfigValue("fleet:chargePwr_small_veh_large_home");

			chargerTypesAtWork= new double[3];
			chargerTypesAtWork[0] = fetchDoubleConfigValue("fleet:chargePwr_small_veh_small_work");
			chargerTypesAtWork[1] = fetchDoubleConfigValue("fleet:chargePwr_small_veh_medium_work");
			chargerTypesAtWork[2] = fetchDoubleConfigValue("fleet:chargePwr_small_veh_large_work");

			smallChargerCap = fetchFloatConfigValue("fleet:chargePwr_small");
			largeChargerCap = fetchFloatConfigValue("fleet:chargePwr_large");
		}
		if(battSocVisualDir == null)
		{
			battSocVisualDir = fetchStringConfigValue("EVCPriceMinimiser:battSocVisualDir");
			battSocRadix = fetchStringConfigValue("EVCPriceMinimiser:battSocRadix");
			battSocExt = fetchStringConfigValue("EVCPriceMinimiser:battSocExt");
			writeToFile = fetchBooleanConfigValue("EVCPriceMinimiser:writeChEvolutionToFile");
		}

		if(energyLimitCap == null) 
		{
			energyLimitCap = fetchBooleanConfigValue("EVCPriceMinimiser:energyLimitCap");
		}
	}

	private float intitialSOC()
	{
		//		float initBat = 0.30f * battCap;

		float initBat = dcd * battCap;
		return initBat;
	}



	// ===================================================================
	/** Return car type. */
	public CarType carType()
	{
		return carType;
	}

	// ===================================================================
	/**
	 * activity type defines the difference between being at home, work, shopping or leisure, which is used to identify that which charger is available (how much power of the charger)
	 * @param actType short
	 * @return power of the charger [KW] for type of the activity
	 */

	public float chargerPwratActType(short actType)
	{
		float chrgrPwr = 0;
		switch(actType)
		{
			case F0Constants.at_Home : 
				chrgrPwr = chargerPwrAtHome;
				break;
			case F0Constants.at_Work : 
				chrgrPwr = chargerPwrAtWork;
				break;
			default : 
				chrgrPwr = 0;
				break;

		}
		return chrgrPwr;
	}

	// ===================================================================
	@Override
	/*
	 * (non-Javadoc)
	 * @see be.uhasselt.imob.feathers2.services.impl.entityManagerService.Car#effRange()
	 */
	public float effRange()
	{
		return pubRange()*dcd*off2realCorrection;
	}

	// ===================================================================
	public float getBattCap()
	{
		return battCap;
	}

	// ===================================================================
	public float getBattSOC()
	{
		return battSOC;
	}

	// ===================================================================
	public float getDcd()
	{
		return dcd;
	}


	// ===================================================================

	// ===================================================================
	/** Get EV distance specific cost [kWh/km] */
	public float getEvDistSpecifConsum() 
	{
		return evDistSpecifConsum;
	}

	// ===================================================================
	public boolean isBattFull()
	{
		return ((battCap - battSOC) <  epsilon);
	}

	// ===================================================================
	public boolean isBatBelowLevel(double initSoc)
	{
		return battSOC < (initSoc - epsilon);
	}

	// ===================================================================
	public float pubRange()
	{
		return (float)carRange[carCategory];
	}


	// ===================================================================

	public void updateSOC(double energy)
	{
		battSOC += energy;
	}

	// ===================================================================
	/**
	 * returns the minimum allowed charging level of the battery (dcd * battCap)
	 * @return [KWh]
	 */
	public float getMinPossChrgLvl()
	{
		return battCap*dcd;
	}

	// ===================================================================

	/**
	 * 
	 * @param energy [KWh]
	 * @param power [KW]
	 * @return Time [minutes]
	 */
	public double reqTimeToChargeBat(double energy, double power)
	{
		return  60*energy/power;
	}
	// ===================================================================

	public double reqChrgToFull(ArrayList<IActivity> sched, int actInd, CheapestChargableSlot cheapestSlot, ArrayList<BatChargingDischargingStamp> chStamps, double initSoc)
	{
		double reqCharg = 0f;
		double maxSOC = Double.NEGATIVE_INFINITY;
		double soc = initSoc;
		int lPrd = 0;

		//		print("stamps=["+chStamps.size()+"]");


		if(chStamps == null || actInd == 0)
		{
			maxSOC = battCap;
		}
		else 
		{
			Collections.sort(chStamps);
			for(BatChargingDischargingStamp cs : chStamps)
			{
				soc += cs.chInCharg;
				//				print("cheapestSlot.prd=["+cheapestSlot.prd+"], cs.period=["+cs.period +"], cs.batSoc=["+soc+"]");
				if(cs.period > cheapestSlot.prd || cs.period == cheapestSlot.prd && cs.stMinInTime > cheapestSlot.affChargingST )
				{
					maxSOC = (soc > maxSOC)? soc : maxSOC;
					//					print("cheapestSlot.prd=["+cheapestSlot.prd+"], cs.period=["+cs.period +"], cs.batSoc=["+soc+"], maxSOC=["+maxSOC+"]");
				}

			}
		}

		return ((battCap - maxSOC) > epsilon) ?  (battCap - maxSOC) : 0;
	}


	// ===================================================================
	public void updateSOCAfterTravel(double j_distance)
	{
		double consum = evDistSpecifConsum * j_distance;
		battSOC -= consum;
	}
	// ===================================================================

	@Override
	public void registerChargingSlotsArray(IChargingSlot[] chSlotDuplicatArray)
	{
		this.chargingSlot  = chSlotDuplicatArray;
	}
	// ===================================================================

	public IChargingSlot getChargingSlot(int i)
	{
		return chargingSlot[i];
	}


	// ===================================================================

	@Override
	public double getBasicCost()
	{
		return basicCost;
	}

	// ===================================================================


	/**
	 * calculates the total distance to be traveled by the car in all activities in the schedule starting from the reference activitiy
	 * @param stInd index [0, size-1] of the reference activity in the schedule
	 * @param sched ArrayList of the IActivity
	 * @return total Distances [Km]
	 */
	private double getDistanceToTravelByCar(int stInd, ArrayList<IActivity> sched)
	{
		double distToDrive = 0;
		for(; stInd<sched.size(); stInd++)
		{
			if(sched.get(stInd).getJ_transportMode() == WidrsConstants.tm_car)
			{
				distToDrive +=sched.get(stInd).getJ_distance();
			}
		}
		return distToDrive;
	}

	// ===================================================================


	@Override
	public void setBasicCost(double calculateChargingPriceForCompleteSchedule)
	{
		basicCost = (float) calculateChargingPriceForCompleteSchedule;
	}


	@Override
	public FeasibleType analyseBatChargConsum(int pCnt, ISchedule sched, int itr)
	{
		IPowerConsumTracker pcTracker = powerSupplyManager.getDuplicateTracker();
		boolean reOpt = false;
		int actInd = 0;
		int size = sched.size();
		IChargingSlot slot;
		ArrayList<BatChargingDischargingStamp> chDisStmp = new ArrayList<BEV.BatChargingDischargingStamp>();


		print("##################################################################################################################");

		/*a SOC stamp with 0 change in SOC to start drawing it from first point*/
		double chStmpST = sched.get(0).getJ_beginningTime();
		double chStmpET = sched.get(0).getA_beginningTime_minOfDay();
		int prd = evcConventions.prdFloorValueFromTime(chStmpST);
		chDisStmp.add(new BatChargingDischargingStamp(chStmpST, chStmpET, 0, 0, 0, prd, "Start"));



		double socComp , minLevel = 0f;
		final double initSoc = battSOC;
		int firstPrdAfterBatFul = (initSoc < battCap ) ? getActivityStartingPerid(sched.getActivitiesList(),0) : -1;


		for(IActivity act : sched.getActivitiesList())
		{
			print("================================================================================================================");
			print("traveling pCount=["+pCnt+"], actInd=["+actInd+"] aCount=["+act.getA_activityCounter()+"]");



			if(act.getJ_transportMode() == WidrsConstants.tm_car && act.getJ_distance() > -1)
			{
				if(firstPrdAfterBatFul == -1)
					firstPrdAfterBatFul = evcConventions.prdFloorValueFromTime(act.getA_beginningTime_minOfDay());

				socComp = battSOC;
				updateSOCAfterTravel(act.getJ_distance());

				/*adding discharging energy stamp after travel to the list*/
				chStmpST = act.getJ_beginningTime();
				chStmpET = act.getA_beginningTime_minOfDay();
				prd = evcConventions.prdFloorValueFromTime(chStmpST);
				chDisStmp.add(new BatChargingDischargingStamp(chStmpST, chStmpET, (battSOC-socComp),0,0,prd, "End Travel"));
				//				double reqEnrgy = act.getJ_distance()*evDistSpecifConsum;


				int lstPrdUsableSlot = getActivityEndingPrd(sched.getActivitiesList(), actInd-1);
				minLevel = getminLevel(actInd, size,initSoc);

				while(isBatBelowLevel(minLevel) && !reOpt)
				{

					if(writeToFile && chDisStmp != null) createBuffWriter_appendSchedToLAstHome(pCnt, itr++,sched.getActivitiesList(), (ArrayList<BatChargingDischargingStamp>) chDisStmp.clone(), initSoc);

					print("---------------------------------------------------------------------------------------------------------");
					print("BatBelow Level => actInd=["+actInd+"] SOC=["+battSOC+"], minlvl=["+minLevel+"], diff=["+requiredEnergyToCharge(actInd, size,initSoc)+"], slotSearchHoriz=["+firstPrdAfterBatFul+","+lstPrdUsableSlot+"], ");

					CheapestChargableSlot cheapestSlot = getCheapestSlot(sched, firstPrdAfterBatFul, lstPrdUsableSlot, pcTracker);
					if(cheapestSlot != null)
					{
						double reqEnrgy = requiredEnergyToCharge(actInd, size,initSoc);
						double maxEngChargCap = this.reqChrgToFull(sched.getActivitiesList(), actInd, cheapestSlot, (ArrayList<BatChargingDischargingStamp>) chDisStmp.clone(), initSoc);
						print("reqEnrgy=["+reqEnrgy+"], maxEngChargCap=["+maxEngChargCap+"]");
						reqEnrgy = Math.min(reqEnrgy, maxEngChargCap);
						if(maxEngChargCap != 0)
						{
							print("dis=["+act.getJ_distance()+"], reqEnrgy=["+requiredEnergyToCharge(actInd, size,initSoc)+"], cheapestSlotInd=["+cheapestSlot.cheapestSlotInd+"]");

							BatChargingDischargingStamp chStamp = chargeEnergyAtslot(cheapestSlot,sched,reqEnrgy,pcTracker);
							if(chStamp != null) 
								chDisStmp.add(chStamp);
							else
								reOpt = true;
						}
						else
						{
							slot = chargingSlot[cheapestSlot.cheapestSlotInd];
							slot.markLocalBlocked(true);
							//if(isBattFull()) firstPrdAfterBatFul = -1;
							/*if it is already last activity and no energy is possible to charge more then no solution is possible 
							 * if we have more activities after the one where we found the cheapest slot, then find cheapest slot in later activities*/
							if(cheapestSlot.actInd < size -1)
							{
								//								print("firstPrdAfterBatFul first=["+firstPrdAfterBatFul+"], cheapestSlot.actInd=["+cheapestSlot.actInd+"], cheapestSlot=["+cheapestSlot.cheapestSlotInd+"]");
								firstPrdAfterBatFul = evcConventions.prdFloorValueFromTime(sched.get(cheapestSlot.actInd+1).getA_beginningTime_minOfDay());
								//								print("firstPrdAfterBatFul after=["+firstPrdAfterBatFul+"]");
							}
							else
							{
								reOpt = true;
								print("Batt full, and not possible to charge in future moments.");
							}
						}


					}
					else
					{
						//infeasible schedule
						reOpt = true;
						print("cheapestSlot not found.");
					}

					minLevel = getminLevel(actInd, size,initSoc);
				}

			}

			/*a SOC stamp with 0 change in charge to draw it to last point of the schedule*/
			chStmpST = act.getA_beginningTime_minOfDay() + act.getA_duration();
			chStmpET = act.getA_beginningTime_minOfDay() + act.getA_duration();
			prd = evcConventions.prdFloorValueFromTime(chStmpST);
			chDisStmp.add(new BatChargingDischargingStamp(chStmpST ,chStmpET , 0, 0, 0, prd, "End Act"));

			print("After Activity => SOC=["+battSOC+"]");
			actInd++;
		}



		if(writeToFile) createBuffWriter_appendSchedToLAstHome(pCnt, itr++,sched.getActivitiesList(), chDisStmp, initSoc);
		FeasibleType fType;
		if(!reOpt)
		{
			powerSupplyManager.updatePowerTracker(pcTracker);
			fType = FeasibleType.Feasible;
		}
		else
		{
			String msg = (battSOC > getminLevel(actInd, size, initSoc)) ?  " initSoc=["+initSoc+"] is not feasible"  : "Infeasible Schedule";
			printInteract(msg);
			if(battSOC>dcd*battCap)
				fType = FeasibleType.SoftInfeasible;
			else
				fType = FeasibleType.HardInfeasible;
			//			System.out.println(s);
		}

		//		System.out.println("p=["+pCnt+"], cost=["+basicCost+"]");
		return fType;
	}



	private double getminLevel(int actInd, int size, double initSoc)
	{
		double minLvl = 0;
		if(actInd == size - 1)
			minLvl = initSoc;
		else
			minLvl = getMinPossChrgLvl();

		return minLvl;
	}

	private double requiredEnergyToCharge(int actInd, int size, double initSoc)
	{
		double reqEng = 0;
		if(actInd == size -1)
		{
			reqEng = initSoc - battSOC;
		}
		else
		{
			reqEng = getMinPossChrgLvl()-battSOC;
		}
		return reqEng;
	}

	/**
	 * calculates the how much energy can be charged affectively. 
	 * @param cheapestSlot
	 * @param sched
	 * @param pcTracker 
	 * @return  chargingStamp 
	 */
	private BatChargingDischargingStamp chargeEnergyAtslot(CheapestChargableSlot cheapestSlot, ISchedule sched, double reqEnrgy, IPowerConsumTracker pcTracker)
	{
		double effCharging = 0f;
		BatChargingDischargingStamp stamp = null;
		//		reqEnrgy = Math.min(reqEnrgy, this.getMinPossChrgLvl() - battSOC);
		IActivity act = sched.get(cheapestSlot.actInd);
		double availPwr = chargerPwratActType(act.getA_activityType());

		assert (availPwr > 0) : Constants.PRECOND_VIOLATION+ " invalid state: it comes here iff power is available at actInd=["+cheapestSlot.actInd+"], actCounter=["+act.getA_activityCounter()+"]";


		double reqTime =  reqTimeToChargeBat(reqEnrgy , availPwr);
		print("reqTime=["+reqTime+"], reqEnrgy=["+reqEnrgy+"]");


		if(reqTime > 0 && reqTime < epsilon) reqTime = 0;



		double effCharTime = calculateEffectiveChTime(cheapestSlot, reqTime);
		print(" effCharTime=["+effCharTime+"]");
		//if there is energy limitation cap at any slot then available power is minimum of the available power from grid and available charger.
		// if there is no limit to charge at any moment then available power is equal to the power of the charger.
		availPwr = (energyLimitCap) ? Math.min(availPwr, pcTracker.getAvailablePwr(cheapestSlot.cheapestSlotInd, cheapestSlot.act.getA_location())) : availPwr;
		print(" availPwr=["+availPwr+"]\n");

		print("availPwr=["+availPwr+"], effCharTime=["+effCharTime+" h]");
		if(effCharTime > epsilon/10 && availPwr > epsilon )
		{
			effCharging = (float) (effCharTime  * availPwr);
			print(" effCharging=["+effCharging+"]\n");
			pcTracker.addUpConsumedpwr(cheapestSlot.cheapestSlotInd, cheapestSlot.act.getA_location(), availPwr);

			updateSOC(effCharging);
			totCharging += effCharging;


			double cost = (effCharging*powerSupplyManager.getPriceAtPrd(cheapestSlot.cheapestSlotInd));
			basicCost += cost;
			print(" effCharging=["+effCharging+"],  rate=["+powerSupplyManager.getPriceAtPrd(cheapestSlot.cheapestSlotInd)+"], basicCost=["+basicCost+"]");

			print("After Charging effEng=["+effCharging+"] at slot["+cheapestSlot.cheapestSlotInd+"], effCharTime=["+effCharTime+"], maxChargingTime=["+cheapestSlot.maxChargingTime+"], => SOC=["+battSOC+"]");
			chargingSlot[cheapestSlot.cheapestSlotInd].updateSaturation(effCharTime*60, cheapestSlot.maxChargingTime);
			stamp = new BatChargingDischargingStamp(cheapestSlot.affChargingST, cheapestSlot.affChargingET, effCharging, effCharging, cost, cheapestSlot.prd, "After Charging");

		}

		return stamp;
	}

	/**
	 * 
	 * @param cheapestSlot
	 * @param reqChTime
	 * @return Affective charging time [h]
	 */
	private double calculateEffectiveChTime(CheapestChargableSlot cheapestSlot, double reqChTime)
	{

		int effActST, effActET, pST, pET;

		pST = evcConventions.minOfDayFromPrd(cheapestSlot.prd);
		pET = evcConventions.minOfDayFromPrd(cheapestSlot.prd + 1);

		effActST = Math.max(pST, (int)cheapestSlot.actST);
		effActET = Math.min(pET, (int)cheapestSlot.actET);
		int chT = effActET - effActST;
		assert (chT <= WidrsConstants.PERIOD_LEN && chT >= 0 ) : Constants.POSTCOND_VIOLATION+ " charging time = ["+chT+"] can not exceed ["+0+","+WidrsConstants.PERIOD_LEN+"] min.";

		cheapestSlot.affChargingST = effActST;
		cheapestSlot.affChargingET = effActET;
		//		print("chT=["+chT+"], pST=["+pST+"],  pET=["+pET+"],cheapestSlot.actST=["+cheapestSlot.actST+"], cheapestSlot.actET=["+cheapestSlot.actET+"], effActST=["+effActST+"], effActET=["+effActET+"]");
		/**effective charging time [hours]*/
		double effCharTime =  Math.min(chT,reqChTime )/60;
		return effCharTime;
	}

	/**
	 * if vehicle is parked at least for the time 'time scope is not defined yet' at the location where charging switch is available.
	 * @param prd 
	 * @param sched
	 * @return if vehicle can be charged  returns index of the activity in the given activities list, otherwise return -1
	 */
	private CheapestChargableSlot ifvehicelCanBeChargedAt(int prd, ArrayList<IActivity> sched)
	{
		int sTPrd = evcConventions.minOfDayFromPrd(prd);
		int eTPrd = evcConventions.minOfDayFromPrd(prd+1);
		int actInd = 0;

		CheapestChargableSlot cheapChargableSlot = null;

		for(IActivity act : sched)
		{

			double actST = act.getA_beginningTime_minOfDay();
			double actET = act.getA_beginningTime_minOfDay()+act.getA_duration();
			if(chargerPwratActType(act.getA_activityType()) > 0)
			{
				if(evcConventions.doesTimeIntervalsOverlap(sTPrd,eTPrd,actST,actET) )
				{
					cheapChargableSlot = new CheapestChargableSlot();
					cheapChargableSlot.act = act;
					cheapChargableSlot.actInd  = actInd;
					cheapChargableSlot.actST = actST;
					cheapChargableSlot.actET  = actET;
					cheapChargableSlot.prd  = prd;
					cheapChargableSlot.maxChargingTime = Math.min(actET, eTPrd) - Math.max(sTPrd, actST);
					return cheapChargableSlot;
				}
			}
			actInd++;
		}
		return null;
	}




	/**
	 * Finds the cheapest usable slot in given periods.
	 * Finds is there any slot available which can be used to charge the car between first and last slot indices given in the parameters.
	 * That slot should not be marked saturated, it should not be blocked by the global tracker, and Vehicle should be parked at the location where power is available.
	 * @param sched  List Of IActivities, Schedule
	 * @param sPrd First index
	 * @param lPrd Last index
	 * @param pcTracker 
	 * @return cheapest slot index
	 */
	private CheapestChargableSlot getCheapestSlot(ISchedule sched, int sPrd, int lPrd, IPowerConsumTracker pcTracker)
	{
		//		assert(sPrd > -1): Constants.PRECOND_VIOLATION + "cheapest slot founder starting period=["+sPrd+"] is not valid. it should be grater than -1.";
		IChargingSlot slot;
		float slotPrice;
		float cheapestPrice = Float.MAX_VALUE;
		int cheapestSlotInd = Integer.MIN_VALUE;
		int actInd = -1;
		int cycInd ;
		CheapestChargableSlot cheapestChargableSlot = null, newFoundSlot = null;

		for(int ind = sPrd; ind <= lPrd; ind++)
		{
			cycInd = evcConventions.converCyclicPeriod(ind);
			IActivity act = sched.getActivityAtPeriod(ind);
			slot = chargingSlot[cycInd];
			//			print("getCheapestSlot => slot["+cycInd+"] => isMarkedExtra=["+slot.isMarkedExtra()+"], saturated=["+slot.isSaturated()+"], blocked=["+powerSupplyManager.isBlocked(cycInd)+"], price=["+powerSupplyManager.getPriceAtPrd(cycInd)+"]");
			if(!slot.isLocalBlocked() && !slot.isSaturated() &&	(!energyLimitCap  ||	act != null && !pcTracker.isBlocked(cycInd, act.getA_location())))
			{
				slotPrice = powerSupplyManager.getPriceAtPrd(cycInd);
				if(slotPrice < cheapestPrice)
				{
					newFoundSlot = ifvehicelCanBeChargedAt(ind, sched.getActivitiesList());
					if(newFoundSlot != null)
					{
						cheapestChargableSlot = newFoundSlot;
						cheapestSlotInd = cycInd;
						cheapestPrice = slotPrice;
					}
				}
			}
		}
		if(cheapestChargableSlot != null)
		{
			cheapestChargableSlot.cheapestSlotInd = cheapestSlotInd;
			int sTPrd = evcConventions.minOfDayFromPrd(cheapestChargableSlot.prd);
			int eTPrd = evcConventions.minOfDayFromPrd(cheapestChargableSlot.prd+1);
			IActivity act = cheapestChargableSlot.act;
			String st = "charging slot found at => aCount=["+act .getA_activityCounter()+"], actInd=["+cheapestChargableSlot.actInd+"], CheapestSlotInd_cyclic=["+cheapestSlotInd+"], prd=["+cheapestChargableSlot.prd+"], PrdSTime=["+sTPrd+"], PrdETime=["+eTPrd+"], JST=["+(act.getA_beginningTime_minOfDay()-act.getJ_duration())+"], AST=["+act.getA_beginningTime_minOfDay()+"], AET=["+(act.getA_beginningTime_minOfDay()+act.getA_duration())+"], overlaps=["+String.valueOf(evcConventions.doesTimeIntervalsOverlap(sTPrd,eTPrd,act.getA_beginningTime_minOfDay(),(act.getA_beginningTime_minOfDay()+act.getA_duration())))+"], pwr=["+chargerPwratActType(act.getA_activityType())+"";
			//			print(st);
		}
		return cheapestChargableSlot;
	}
	/**
	 * return the period of ending time of the activity which is at given index of the given array list.
	 * @param sched list of activities
	 * @param index
	 * @return period
	 */
	private int getActivityEndingPrd(ArrayList<IActivity> sched, int index)
	{
		IActivity act = sched.get(index);
		double st = act.getA_beginningTime_minOfDay() + act.getA_duration();
		return evcConventions.prdFloorValueFromTime(st);
	}

	/**
	 * return the period of starting time of the activity which is at given index of the given array list.
	 * @param sched list of activities
	 * @param index
	 * @return period
	 */
	private int getActivityStartingPerid(ArrayList<IActivity> sched, int index)
	{
		IActivity act = sched.get(index);
		return evcConventions.prdFloorValueFromTime(act.getA_beginningTime_minOfDay());
	}


	@Override
	/*calculates the total energy required to charge for all car trips in the schedule*/
	public double getEnergyToChargeToTravelByCar(int ind, ArrayList<IActivity> sched)
	{
		double disToTravel =  (getDistanceToTravelByCar(ind, sched));
		double reqEnergy = disToTravel*evDistSpecifConsum;
		return reqEnergy;
	}

	private void createBuffWriter_appendSchedToLAstHome(int pCnt, int itr, ArrayList<IActivity> sched, ArrayList<BatChargingDischargingStamp> chDisStmp, double initSoc) 
	{
		try
		{
			if(!battSocDirReset)
			{
				battSocDirReset = true;
				if(commonsService.deleteDirRecusively(battSocVisualDir) == false) print( battSocVisualDir+ " was not deleted successfully." );
			}

			String perSuff = battSocVisualDir+pCnt+"/"+battSocRadix+"_"+pCnt;
			String suffix = perSuff +"_"+itr;
			String socDataFileName = suffix +".dat";
			String pltFileName = suffix+battSocExt;

			File socDataFile = new File(socDataFileName);
			File pltFile = new File(pltFileName);
			File batFile = new File(perSuff+".bat");


			File dir = pltFile.getParentFile();
			if(dir.exists())
			{
				dir.delete();
			}
			dir.mkdirs();
			pltFile.createNewFile();
			socDataFile.createNewFile();

			writePltScriptFile(pCnt, itr, sched, pltFile, socDataFileName, suffix);

			writeDataFile(socDataFile, (ArrayList<BatChargingDischargingStamp>) chDisStmp.clone(), initSoc);

			writeBatFile(batFile, pltFile);

		} catch (IOException e)
		{
			logger.error("failed to create the file buffered writer.");
			logger.error(e.getMessage());
		} 
	}

	private void writePltScriptFile(int pCnt, int itr, ArrayList<IActivity> sched, File pltFile, String socDataFileName, String suffix) throws IOException
	{
		float minBattSoc = this.getMinPossChrgLvl();
		int obj = 0;

		FileWriter fw = new FileWriter(pltFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("set terminal pngcairo size 800,1000 enhanced font 'Verdana,10'  \n");
		bw.write("set output \""+suffix+".png\"\n");
		bw.write("set grid xtics ytics \n");
		bw.write("dcd = "+minBattSoc+"\n");
		bw.write("basicCost = "+basicCost+"\n");
		bw.write("batt_cap = "+battCap+"\n\n");

		int minPrd = evcConventions.prdFloorValueFromTime(sched.get(0).getJ_beginningTime());
		int maxPrd = evcConventions.prdFloorValueFromTime(sched.get(sched.size()-1).getA_beginningTime_minOfDay()  + sched.get(sched.size()-1).getA_duration());
		double xMin = evcConventions.minOfDayFromPrd(minPrd );
		double xMax = evcConventions.minOfDayFromPrd(1 + maxPrd );;


		bw.write("set xrange ["+xMin+":"+xMax+"]\n");
		bw.write("unset xtics\n");

		String xtic = "";
		for(int i = minPrd; i<=maxPrd; i=i+10)
		{
			xtic = xtic + "\""+evcConventions.converCyclicPeriod(i)+"\" "+evcConventions.minOfDayFromPrd(i)+ ", ";
		}
		xtic = xtic + "\""+evcConventions.converCyclicPeriod(maxPrd)+"\" "+evcConventions.minOfDayFromPrd(maxPrd);
		bw.write("set xtics format \" \"\n");
		bw.write("set xtics ("+xtic+")\n");

		bw.write("set xlabel \"period of day 15 [min] each\"\n\n");

		bw.write("yRanMin = "+(-minBattSoc)+"\n");
		bw.write("yRanMax = "+(battCap+1)+"\n");
		bw.write("set yrange [yRanMin:yRanMax]\n");
		bw.write("set ylabel \"State of battry charge [KWh]\"\n");
		bw.write("set ytics add (\"DCD\" dcd)\n");
		bw.write("set ytics add (\"BATT-CAP\" batt_cap)\n\n");

		minBattSoc = 0;
		bw.write("rectYOffset = dcd /4\n");
		bw.write("rectYOrig = dcd /4\n");
		double x1;
		double x2;
		double x3;
		for(IActivity act : sched)
		{
			obj++;
			bw.write("rectYOrig = (-1 * (rectYOffset + 0.1))\n");
			x1 = act.getJ_beginningTime();
			x2 = act.getA_beginningTime_minOfDay();
			x3 = act.getA_beginningTime_minOfDay() + act.getA_duration();

			String color = "green";
			if(act.getJ_transportMode() == WidrsConstants.tm_car)
				color = "red";
			else
				color = "gold";
			bw.write("set object "+obj+" rect from "+x1+",rectYOrig to "+x2+",rectYOrig + rectYOffset fc rgb \""+color+"\"\n");
			obj++;
			if(chargerPwratActType(act.getA_activityType()) > 0)
				color = "green";
			else
				color = "dark-green";
			bw.write("set object "+obj+" rect from "+x2+",rectYOrig to "+x3+",rectYOrig + rectYOffset fc rgb \""+color+"\"\n");
		}


		for(int i = 0; i<chargingSlot.length; i++)
		{
			x1 = evcConventions.minOfDayFromPrd(i);
			x2 = evcConventions.minOfDayFromPrd(i+1);
			if(x1 < xMin)
			{
				x1 += 1440;
				x2 += 1440;
			}
			if(chargingSlot[i].isLocalBlocked())
			{
				obj++;
				bw.write("rectYOrig = (-2 * (rectYOffset + 0.075))\n");
				bw.write("set object "+obj+" rect from "+x1+",rectYOrig to "+x2+",(rectYOrig + rectYOffset) fc rgb \"#FF8C00\"\n");
			}
			if(chargingSlot[i].isSaturated())
			{
				obj++;
				bw.write("rectYOrig = (-3 * (rectYOffset + 0.075))\n");
				bw.write("set object "+obj+" rect from "+x1+",rectYOrig to "+x2+",(rectYOrig + rectYOffset) fc rgb \"#00008B\"\n");
			}
			else if(chargingSlot[i].isChargingPlanned())
			{
				obj++;
				bw.write("rectYOrig = (-3 * (rectYOffset + 0.075))\n");
				bw.write("set object "+obj+" rect from "+x1+",rectYOrig to "+x2+",(rectYOrig + rectYOffset) fc rgb \"#00FFFF\"\n");
			}
			if(powerSupplyManager.isBlocked(i))
			{
				obj++;
				bw.write("rectYOrig = (-2 * (rectYOffset + 0.075))\n");
				bw.write("set object "+obj+" rect from "+x1+",rectYOrig to "+x2+",(rectYOrig + rectYOffset) fc rgb \"#B22222\"\n");
			}
		}

		bw.write("set arrow from "+xMin+",dcd to "+xMax+",dcd  lc rgb \"red\" nohead\n");
		bw.write("set arrow from "+xMin+",batt_cap to "+xMax+",batt_cap lc rgb \"red\" nohead\n");
		bw.write("set parametric\n");
		bw.write("set grid\n");
		bw.write("set key outside\n");
		bw.write("set multiplot layout 3,1 title \"Battry SOC and PRICE graph for "+pCnt+"-"+itr+"\"\n");
		bw.write("plot  \""+socDataFileName+"\"  using 1:2 with lines lc rgb \"black\" lw 2 title \"BatSoc\"\n\n");

		bw.write("yRanMin = 0\n");
		bw.write("yRanMax = "+(basicCost+1)+"\n");
		bw.write("set yrange [yRanMin:yRanMax]\n");
		bw.write("set ylabel \"Charging Cost [Euro/KWh]\"\n");
		bw.write("unset arrow\n");
		if(basicCost/10 > 0.05f)
			bw.write("set ytics 0, basicCost/10\n");
		else
			bw.write("set ytics 0, 0.05\n");
		bw.write("plot  \""+socDataFileName+"\"  using 1:3 with lines lc rgb \"medium-blue\" lw 2 title \" Price\"\n\n");

		bw.write("yRanMin = 0\n");
		bw.write("yRanMax = "+(totCharging+1)+"\n");
		bw.write("set yrange [yRanMin:yRanMax]\n");
		bw.write("set ylabel \"Total Energy Charged [kWh]\"\n");
		if(totCharging/10 > 0.5f)
			bw.write("set ytics 0, "+totCharging+"/10\n");
		else
			bw.write("set ytics 0, 0.5\n");

		bw.write("plot  \""+socDataFileName+"\"  using 1:4 with lines lc rgb \"dark-goldenrod\" lw 2 title \"TotEng\"\n\n");


		bw.close();

	}

	// script file to create the graphs from all plt scripts
	private void writeBatFile(File batFile, File pltFile) throws IOException
	{
		if(!batFile.exists())
			batFile.createNewFile();
		FileWriter fw = new FileWriter(batFile.getAbsoluteFile(), true); //append at the end of file 
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("start /min "+ pltFile.getAbsoluteFile().toString()+"\n");
		bw.close();

	}

	private void writeDataFile(File socDataFile,  ArrayList<BatChargingDischargingStamp> chDisStmp, double initSoc) throws IOException
	{
		if(chDisStmp != null)
		{
			FileWriter fw = new FileWriter(socDataFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);


			//			sort(chDisStmp);
			Collections.sort(chDisStmp);

			double totCost = 0, totCharg = 0;
			String s = "", msg="";
			int prd = Integer.MIN_VALUE;
			BatChargingDischargingStamp bcd, prevBcd = new BatChargingDischargingStamp(), nextBcd;
			int size = chDisStmp.size(), i = 0;
			boolean p = false;
			if(p) System.out.println("------------------------------------------------------------------------------------------------");
			if(p) System.out.println("file=["+socDataFile+"]");
			while(i<size)
			{	
				bcd = chDisStmp.get(i);
				if(p) bcd.print();

				if(i == 0 || prevBcd.period != bcd.period)
					s = s + bcd.stMinInTime+" "+initSoc + " " + totCost + " " + totCharg + " # - "+i+bcd.type+ "\n" ;

				initSoc = initSoc + bcd.chInCharg;
				totCharg = totCharg + bcd.totCharging;
				totCost = totCost + bcd.totcost;
				prevBcd = bcd;
				msg = bcd.type;
				i++;
				if(i == size || i != size && (bcd.period != chDisStmp.get(i).period || bcd.chInCharg > 0 && chDisStmp.get(i).chInCharg <= 0 || bcd.chInCharg <= 0 && chDisStmp.get(i).chInCharg > 0))
					s = s + bcd.enMinInTime+" "+initSoc + " " + totCost + " " + totCharg + " #   "+i+bcd.type+ "\n" ;
			}
			//		print(s);
			bw.write(s);
			bw.close();
		}
	}

	@Override
	public boolean is_BEV_feasible(BitSet bitSet, ArrayList<IActivity> sched)
	{
		boolean feasible = true;
		double soc = battSOC;
		float mimChLvl = getMinPossChrgLvl();
		double distSpecifConsum = evDistSpecifConsum;


		double distToDrive = 0;
		double reqEnergy = 0;
		for(IActivity act : sched)
		{
			// Distance to drive is required to charge the car at current charging availability location
			if(act.getJ_transportMode() == WidrsConstants.tm_car)
				distToDrive = act.getJ_distance();
			else
				distToDrive = 0;

			reqEnergy = distToDrive * distSpecifConsum ;

			soc -= reqEnergy;
			if(soc < mimChLvl)
			{
				feasible = false;
			}
			float availPwr = chargerPwratActType(act.getA_activityType());
			double maxEngCharg = maxEngChargPoss(availPwr,act.getA_duration());
			double maxBattSOC = soc  + maxEngCharg;
			soc = Math.min(battCap, maxBattSOC);
		}

		return feasible;
	}

	/**
	 * with given duration[min] and power [KW] maximum energy [KWh] which is possible to charge (power*duration/60)
	 * @param availPwr [KW]
	 * @param a_duration [min]
	 * @return energy [KWh]
	 */
	private double maxEngChargPoss(float availPwr, double a_duration)
	{
		return availPwr * a_duration / 60;

	}
	private void print(String msg) 
	{
		//				System.out.println(msg);
		//				logger.info(msg);
		//		s  = s + msg + "\n";
		//		s = "";
	}



	public void registerPowerCapManager(IPowerSupplyManager powerCapManager)
	{
		this.powerSupplyManager = powerCapManager;
	}

	@Override
	public float getMaxSwitchPower()
	{
		return largeChargerCap;
	}

	@Override
	public void reset()
	{
		battSocDirReset = false;
		battSOC = intitialSOC(); // TODO:{MU}: Initial battery state can vary from empty to full depending upon the case, should move to Config or read from file;
		basicCost = 0.0f;
		for(IChargingSlot ch : chargingSlot)
		{
			ch.reset();
		}
	}

	private void printInteract(String msg)
	{
		//		System.out.println(msg);
		//		logger.info(msg);
		//		Scanner in = new Scanner(System.in);
		//		in.nextLine();
	}



	class CheapestChargableSlot
	{
		/**maximum time for which vehicle can charge at the particular period*/
		public double maxChargingTime;
		/**period of the cheapest charging slot in min of Time*/
		public int prd;
		/**activity index which overlaps with cheapest time slot*/
		int actInd;
		/**cheapest slot index, circular index [0:95] for slots array*/
		int cheapestSlotInd;
		/**IActivity overlapping the slot where charging slot is found*/
		IActivity act;
		/**activity starting time*/
		double actST;
		/**activity ending time*/
		double actET;
		/**affective charging start time in min of the Time*/
		double affChargingST;
		/**affective charging end time in min of the Time*/
		double affChargingET;
	}


	/**
	 * This class registers the charging change stamp of the battery. Each stamp registers an event for the change in state of charge in battery.  
	 * <ul> Each stamp have the following information:
	 * <li> Change starting minute in Time </li>
	 * <li> Change ending minute in Time </li>
	 * <li> Change in SOC [KWh] </li>
	 * <li> Cost of getting charging energy: in case of charging the Battery, when change in SOC is positive, it means charging the battery which costs the money.  in case of negative change, change in SOC due to traveling, values for this parameter is used zero.</li>
	 * <li> Total energy charged to the battery. in case of negative change, change in SOC due to traveling, values for this parameter is used zero. </li>
	 * <li> SOC at the end of the stamp</li>
	 * </ul>
	 * @author MuhammadUsman
	 *
	 */
	class BatChargingDischargingStamp implements Comparable<BatChargingDischargingStamp>
	{

		/**Change starting minute in Time*/
		double stMinInTime;
		/**Change ending minute in Time*/
		double enMinInTime;
		/**Change in SOC [KWh]*/
		double chInCharg;
		/**Total energy charged to the battery. in case of negative change, change in SOC due to traveling, values for this parameter is used zero. */
		double totCharging;
		/**Cost of getting charging energy: in case of charging the Battery, when change in SOC is positive, it means charging the battery which costs the money.  in case of negative change, change in SOC due to traveling, values for this parameter is used zero.*/
		double totcost;
		/**period in which charging stamp lies*/
		int period;
		String type;
		DecimalFormat df = new DecimalFormat("#.##");

		/**
		 * 
		 * @param st Starting time of change in SOC 
		 * @param en Ending time of change in SOC
		 * @param change change in the SOC (positive or negative)
		 * @param chargingEngGrid in case of positive change (amount of energy charged from the grid) otherwise zero
		 * @param cost in case of positive change (cost of energy charged from grid) otherwise zero
		 * @param prd period number in which this change occurred
		 */
		public BatChargingDischargingStamp(double st, double en, double change, double chargingEngGrid, double cost, int prd, String tp)
		{
			stMinInTime = st;
			enMinInTime = en;
			chInCharg = change;
			totCharging = chargingEngGrid;
			totcost = cost;
			//			batSoc = btSoc;
			period = prd;
			type = tp;
			//			print("type=["+type+"], "+toString());

		}

		public void print()
		{
			System.out.println("stMinInTime=["+stMinInTime+"], enMinInTime=["+enMinInTime+"], chInCharg=["+chInCharg+"], period=["+period+"],  type=["+type+"]");

		}

		public BatChargingDischargingStamp()
		{
			period = -1;
		}

		public int compareTo(BatChargingDischargingStamp compareStamp) 
		{

			//			if(compareStamp == null || this == null) return 0;
			//ascending order
			return (int) (this.stMinInTime -  compareStamp.stMinInTime);

			//descending order
			//return (int) compareST - this.stMinInTime;

		}

		@Override
		public String toString()
		{
			return "stMinInTime=["+formatDouble(stMinInTime)+"], enMinInTime=["+formatDouble(enMinInTime)+"], change=["+formatDouble(chInCharg)+"], chargingEngGrid=["+formatDouble(totCharging)+"], totcost=["+formatDouble(totcost)+"], period=["+period+"]";
		}
		public double formatDouble(double d)
		{
			//			return Double.parseDouble(df.format(d));
			return d;
		}
		public BatChargingDischargingStamp clone()
		{
			return new BatChargingDischargingStamp(stMinInTime, enMinInTime, chInCharg, totCharging, totcost, period, type);

		}
	}


	@Override
	public boolean isCostOptimal(int pc, ArrayList<IActivity> sched, int[] sorted)
	{
		int i = 0;
		double energy = Double.MAX_VALUE;
		double value, preVal = Double.MAX_VALUE;
		boolean enableCheck = false;
		boolean isOptimal = true;
		ArrayList<Integer> t = new ArrayList<Integer>();
		int sp,ep;
		String s = "prs=["+pc+"] => \n";
		s = s + "travel ->";
		for(IActivity a : sched)
		{
			sp = evcConventions.prdFloorValueFromTime(a.getJ_beginningTime());
			ep = evcConventions.prdFloorValueFromTime(a.getJ_beginningTime()+a.getJ_duration());
			for(i = sp; i<=ep; i++)
			{
				t.add(evcConventions.converCyclicPeriod(i));
				s = s + evcConventions.converCyclicPeriod(i)+" ";
			}
		}
		s = s + "\n";
		i = 0;
		for(IChargingSlot c : chargingSlot)
		{
			if(c.getChargedEnergy()>0)s = s + i+"["+c.getChargedEnergy()+"]  ";
			i++;
		}
		s = s + "\n";
		for(i=0; i<sorted.length; i++)
		{
			value = chargingSlot[sorted[i]].getChargedEnergy();
			enableCheck = enableCheck || t.contains(i);
			if(enableCheck)
				if(preVal == 0 && value > 0)
				{
					//					s = s + " NotOptimal";
					isOptimal = false;
					break;
				}
			//			if(value <= energy)
			//			{
			//				energy = value;
			//			}
			//			else
			//			{
			//				isOptimal = false;
			//				break;
			//			}
			preVal = value;
		}


		//		System.out.println(s+"\n-----------------"+isOptimal+"----------------------------");
		return isOptimal;
	}

	//======================================================================================
	public ICar clone()
	{
		BEV bev = new BEV();
		bev.powerSupplyManager = powerSupplyManager;
		bev.writeToFile = writeToFile;
		bev.evDistSpecifConsum = evDistSpecifConsum;
		bev.pubRange = pubRange;
		bev.battCap = battCap;
		bev.dcd = dcd;
		bev.off2realCorrection = off2realCorrection;
		bev.chargerPwrAtHome = chargerPwrAtHome;
		bev.chargerPwrAtWork = chargerPwrAtWork;
		bev.basicCost = 0;
		bev.battSocDirReset = battSocDirReset;
		bev.epsilon = 0.005f;
		bev.totCharging = 0;
		bev.battSOC = bev.intitialSOC();
		bev.carType = carType;

		return bev;
	}
	@Override
	public double getTotalCharging()
	{
		return totCharging;
	}
}
