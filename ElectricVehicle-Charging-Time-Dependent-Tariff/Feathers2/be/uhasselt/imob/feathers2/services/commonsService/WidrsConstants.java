package be.uhasselt.imob.feathers2.services.commonsService;

/**
 * Provides the constants used in the With In day Rescheduling.
 * @author IMOB/Luk Knapen
 * $Id: WidrsConstants.java 976 2013-11-28 12:07:57Z MuhammadUsman $
 */
public class WidrsConstants
{
   
   
   
   /** Length of one period (taBasePeriod) [min] */
   public static final int PERIOD_LEN = 15;
   
   /** Number of hours in one Day*/
   public static final int NUMBER_OF_HOURS_IN_ONE_DAY = 24;
   
   /** Number of hours in one Day*/
   public static final int NUMBER_OF_MINUTES_IN_ONE_HOUR = 60;

   /** Number of periods within an hour: shall always a divisor of 60. */
   public static final int NUMBER_OF_PERIODS_IN_ONE_HOUR = NUMBER_OF_MINUTES_IN_ONE_HOUR / PERIOD_LEN;
   
   /** Number of periods within a day. */
   public static final int NUMBER_OF_PERIODS_IN_ONE_DAY = NUMBER_OF_PERIODS_IN_ONE_HOUR * NUMBER_OF_HOURS_IN_ONE_DAY;
   
   public static final int MINUTES_IN_DAY = NUMBER_OF_MINUTES_IN_ONE_HOUR * NUMBER_OF_HOURS_IN_ONE_DAY;

   public static final double ScheduleTotalDurationTolerance = 1.0e-4;

   public static final String id_subProc_impedMatrixGen= "impedMatrixGen";
   public static final String id_subProc_prepareNetworks = "prepareNetworks";
   public static final String id_subProc_createMatrixFromCsv= "createMatrixFromCsv";
   public static final String id_subProc_createMatrixFromCsv_prd= "createMatrixFromCsv_prd";
   public static final String id_subProc_trafficAssignment= "trafficAssignment";
   public static final String id_subProc_trafficAssignment_prd= "trafficAssignment_prd";

   public static final String desc_subProc_impedMatrixGen= "TransCAD : impedance matrix generator";
   public static final String desc_subProc_prepareNetworks = "TransCAD : created networks (normal and reduced capacity cases)";
   public static final String desc_subProc_createMatrixFromCsv= "TransCAD : create Matrix files from OD given in csv file";
   public static final String desc_subProc_createMatrixFromCsv_prd= "TransCAD : create Matrix files from OD given in csv file for given taBasePeriod";
   public static final String desc_subProc_trafficAssignment= "TransCAD : create ODbin files from OD given in csv file";
   public static final String desc_subProc_trafficAssignment_prd= "TransCAD : create ODbin files from OD given in csv file for given period";

	/** Feathers0 code for transportation mode : undefined (irrelevant)*/
	public static final int tm_undefined = -1;
	/** Feathers0 code for transportation mode : car */
	public static final int tm_car = 1;
	/** Feathers0 code for transportation mode : slow (walk, bike, skateboard, skeelers, ...) */
	public static final int tm_slow = 3;
	/** Feathers0 code for transportation mode : public (bus, tram, metro) */
	public static final int tm_public = 4;
	/** Feathers0 code for transportation mode : carPassenger */
	public static final int tm_carPassenger = 6;
}

