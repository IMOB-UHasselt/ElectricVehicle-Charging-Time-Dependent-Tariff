package be.uhasselt.imob.feathers2.services.environmentService;

import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.environmentService.IIncident;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.library.config.Config;
import com.csvreader.CsvReader;
import java.io.IOException;
import java.util.Hashtable;
import org.apache.log4j.Logger;

/**
   Incident descriptor
   @author IMOB/Luk Knapen
   @version $Id: IIncident.java 740 2013-03-22 06:55:14Z MuhammadUsman $
 */
public interface IIncident
{
	// ======================================================================
	/** Incident occurrence time [minOfDay] */
	public int occurrenceTime();

	// ======================================================================
	/** Incident occurrence time [shiftedTime] */
	public int occurrenceTimeShifted();

	// ======================================================================
	/** Incident duration [min] */
	public int duration();

	// ======================================================================
	public int deliveryDelay();

	// ======================================================================
	public double tisInfo_mulFac();

	// ======================================================================
	public double tisInfo_refGap();

	// ======================================================================
	public double tisInfo_level();

	// ======================================================================
	/** Value for attenuation factor as a function of the gap between incident endTime and trip startTime. */
	public double attenuationAfter();

   // ======================================================================
   /** Is specified OD pair affected by incident ? */
   public boolean odPairAffected(String odPairUid);

	// ======================================================================
	/** Loads new set of affected OD pairs from specified CSV file.
       @param affectedOdPairsFileName name for CSV file to read
       @return true iff data loaded successfully
	 */
	public boolean loadAffectedOdPairs(String affectedOdPairsFileName);

}
