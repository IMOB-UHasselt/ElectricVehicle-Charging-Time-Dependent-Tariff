package be.uhasselt.imob.feathers2.services.impl.environmentService;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.IConventions;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.environmentService.IIncident;
import be.uhasselt.imob.library.config.Config;
import com.csvreader.CsvReader;
import java.io.IOException;
import java.util.Hashtable;
import org.apache.log4j.Logger;

/**
   Incident descriptor
   @author IMOB/Luk Knapen
   @version $Id: Incident.java 740 2013-03-22 06:55:14Z MuhammadUsman $
   <ol>
      <li>The period during which the incident exists (reduces road capacity) shall be contained in a single day expressed in <em>shiftedTime</em>.</li>
      <li>The incident is defined in the config, expressed as timeOfDay (not as <em>shiftedTime</em>).</li>
   </ol>
 */
public class Incident implements IIncident
{
	private Logger logger = null;
	/** Time [minOfDay] at which incident starts. */
	private int occurrenceTime = 0;
	/** Time [shiftedTime] at which incident starts (kept in order to avoid  millions of conversions). */
	private int occurrenceTimeShifted = 0;
	/** Incident duration [min] as predicted by the TIS */
	private int duration = -1;
	/** Incident related information delivery delay: period [min] between incident start time and broadcast notification time. */
	private int deliveryDelay;
	/** Set of OD pairs affected by the incident. */
	private Hashtable<String,OD_pair> affected_OD_pairs;
	/** TIS (Traffic Information Service) : travel duration multiplication factor */
	private double tisInfo_mulFac;
	/** TIS (Traffic Information Service) : reference time gap starting at incident endTime, defines time at which decay reaches specified level. */
	private double tisInfo_refGap;
	/** TIS (Traffic Information Service) : decay level reached at reference time (see <code>refGap</code>) */
	private double tisInfo_level;
   /** Conventions used in WIDRS. */
   private IConventions conventions = null;

	// ======================================================================
	/** Constructor
	 */
	public Incident(Config config, IConventions conventions) throws F2Exception
	{
      assert (config != null) : Constants.SOFTWARE_ERROR + "config==[null]";
      assert (conventions != null) : Constants.SOFTWARE_ERROR + "conventions==[null]";
      this.conventions = conventions;
		if (logger == null) logger = Logger.getLogger(this.getClass().getName());
		this.occurrenceTime = config.intValueForName("incident:occurrrenceTime");
      this.occurrenceTimeShifted = conventions.shiftedTimeFromTimeOfDay(this.occurrenceTime);
		this.duration = config.intValueForName("incident:duration");
		this.deliveryDelay = config.intValueForName("incident:deliveryDelay");
		this.tisInfo_mulFac = config.doubleValueForName("incident:tisInfo_mulFac");
		this.tisInfo_refGap = config.doubleValueForName("incident:tisInfo_refGap");
		this.tisInfo_level = config.doubleValueForName("incident:tisInfo_level");
		affected_OD_pairs = new Hashtable<String,OD_pair>();

		if (this.duration < 0)
		{
			String msg = "Invalid incident duration=["+this.duration+"] found";
			logger.error(msg);		
			throw new F2Exception(msg);
		}
      
		if (this.occurrenceTime < 0 || this.occurrenceTime > WidrsConstants.MINUTES_IN_DAY)
		{
			String msg = "Invalid incident occurrence time=["+this.occurrenceTime+"] found";
			logger.error(msg);		
			throw new F2Exception(msg);
		}

		if (this.occurrenceTimeShifted + this.duration > conventions.shiftedTimeFromTimeOfDay(WidrsConstants.MINUTES_IN_DAY - 1))
		{
			String msg = "Incident cannot extend to following 'shiftedTime' day : occurrenceTime=["+this.occurrenceTime+"] duration=["+this.duration+"]. Day boundary is at minute t=["+F0Constants.FEATHERS0_TIME_OFFSET+"]";
			logger.error(msg);		
			throw new F2Exception(msg);
		}

		if (this.tisInfo_mulFac < 0)
		{
			String msg = "Invalid incident mulFac=["+this.tisInfo_mulFac+"] found";
			logger.error(msg);		
			throw new F2Exception(msg);
		}

		if (this.tisInfo_refGap <= 0)
		{
			String msg = "Invalid incident reference gap length=["+this.tisInfo_refGap+"] found";
			logger.error(msg);		
			throw new F2Exception(msg);
		}

		if ((this.tisInfo_level <= 0) || (this.tisInfo_level >= 1))
		{
			String msg = "Invalid incident decay reference level=["+this.tisInfo_level+"] found";
			logger.error(msg);		
			throw new F2Exception(msg);
		}
	}

	// ======================================================================
	/** Incident occurrence time [minOfDay] */
	public int occurrenceTime()
	{
		return occurrenceTime;
	}

	// ======================================================================
	@Override
	public int occurrenceTimeShifted()
	{
		return occurrenceTimeShifted;
	}

	// ======================================================================
	/** Incident duration [min] */
	public int duration()
	{
		return duration;
	}

	// ======================================================================
	public int deliveryDelay()
	{
		return deliveryDelay;
	}

	// ======================================================================
	public double tisInfo_mulFac()
	{
		return tisInfo_mulFac;
	}

	// ======================================================================
	public double tisInfo_refGap()
	{
		return tisInfo_refGap;
	}

	// ======================================================================
	public double tisInfo_level()
	{
		return tisInfo_level;
	}

	// ======================================================================
	/** Value for attenuation factor as a function of the gap between incident endTime and trip startTime. */
	public double attenuationAfter()
	{
		return -Math.log(tisInfo_level)/tisInfo_refGap;
	}

   // ======================================================================
   /** Is specified OD pair affected by incident ? */
   public boolean odPairAffected(String odPairUid)
   {
      return (affected_OD_pairs.get(odPairUid) != null);
   }

	// ======================================================================
	/** Loads new set of affected OD pairs from specified CSV file.
       @param affectedOdPairsFileName name for CSV file to read
       @return true iff data loaded successfully
	 */
	public boolean loadAffectedOdPairs(String affectedOdPairsFileName)
	{
		logger.info("Loading affected OD-pairs from file=["+affectedOdPairsFileName+"]");
		boolean ok = false;
      int numAffectedODpairs = 0;
		try
		{
			CsvReader csvReader = new CsvReader(affectedOdPairsFileName,',');
			csvReader.readHeaders();
			while (csvReader.readRecord())
			{
				int origin = Integer.parseInt(csvReader.get(0));
				int destin = Integer.parseInt(csvReader.get(1));
				OD_pair odPair = new OD_pair(origin,destin);
				affected_OD_pairs.put(odPair.uid(),odPair);
            numAffectedODpairs++;
			}
         logger.info("numAffectedODpairs=["+numAffectedODpairs+"]");
			ok = true;
		}
		catch (IOException ioe)
		{
			// TODO:{LK} cleanup exception handling
			assert(false);
		}
		catch (NumberFormatException nfe)
		{
			// TODO:{LK} cleanup exception handling
			assert(false);
		}
		return ok;
	}

	// ======================================================================

}
