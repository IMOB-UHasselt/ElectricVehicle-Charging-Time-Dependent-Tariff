package be.uhasselt.imob.feathers2.services.impl.environmentService;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F0Constants;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.commonsService.IConventions;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.environmentService.IIncident;
import be.uhasselt.imob.feathers2.services.transcadService.ITranscadService;
import be.uhasselt.imob.library.config.Config;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
   Traffic Assignment Network Manager that makes use of step functions to model the capacity on links.
   <ol>
      <li>The capacity of each link is modeled as a step function.</li>
      <li>The capacity discontinuously drops at the incident start time and discontinuously is restored at the end.</li>
      <li>This TA_NetworkMgr has two networks, one for the incident period and one for the non-incident period.</li>
   </ol>
   @version $Id: TA_NetworkMgr_Step.java 749 2013-03-24 23:53:49Z LukKnapen $
   @author IMOB/LK
*/
public class TA_NetworkMgr_Step extends TA_NetworkMgr
{
   private static final int tBasePrd = WidrsConstants.PERIOD_LEN;
   private IConventions conventions = null;

   // --------------------------------------------------------------------------------
   /** Configurator. */
   private Config config = null;
   /** Root for WIDRS data. */
   private String dataDir = null;
   /** Normal network radix name */
   private String normalNetworkRadix = null;
   /** Disturbed network radix name */
   private String disturbedNetworkRadix  = null;
   /** TransCAD service */
   private ITranscadService transcadService = null;
   /** Minimal relative fraction of an NSE_period that needs to be overlapped with the incident period in order to use the 'reduced capacity network'. */
   private double minRelFrac_NSE_periodOverlap = 0.1;
   /** Reference to Incident descriptor. */
   private IIncident incident = null;
   /** Initialisation indicator. Initialisation implies <em>traffic networks setup</em> and hence some <code>TransCAD</code> activity. This is deliberatly not done
       in the constructor because that makes all depending OSGi bundles dependent on the success of the (external) <code>TransCAD</code> process execution.
       Network initialisation is deferred until the complete framework has been started. */
   private boolean initialized = false;

   // --------------------------------------------------------------------------------
   /**
      Constructor: sets up everything required to supply networks later on.
      @param config configuration
      @param transcadService service to execute TransCAD scripts
      @param incident descriptor for the trouble situation
      @precond. <code>(config != null) and (transcadService != null) and (incident != null)</code>
   */
   public TA_NetworkMgr_Step(Config config, ICommonsService commonsService, ITranscadService transcadService, IIncident incident) throws F2Exception
   {
      assert config != null : "Constants.PRECON_VIOLATION : config == null";
      assert transcadService != null : "Constants.PRECON_VIOLATION : transcadService == null";
      assert incident != null : "Constants.PRECON_VIOLATION : incident == null";

      if (logger == null) logger = Logger.getLogger(getClass().getName());
      this.config = config;
      this.transcadService = transcadService;
      this.incident = incident;
      this.conventions = commonsService.conventions();

      this.dataDir = config.stringValueForName("rescheduler:dataDir");
      this.normalNetworkRadix = config.stringValueForName("transcad:normalNetwork");
      this.disturbedNetworkRadix = config.stringValueForName("transcad:disturbedNetwork");
      this.minRelFrac_NSE_periodOverlap = config.doubleValueForName("incident:minRelFrac_NSE_periodOverlap");
   }

   // --------------------------------------------------------------------------------
   /**
      Initialize.
   */
      public void initialize()
      {
         transcadService.prepNetworks_StepDisturber();
         initialized = true;
      }

   // --------------------------------------------------------------------------------
   /**
      Returns the name for the network that holds for the <em>taBasePeriod</em> specified by <code>prd</code>.
      @param period <em>taBasePeriod</em> offset within the day
   */
   public String networkName(int period)
   {
      String name = null;
      if (!initialized) this.initialize();
      int nsePrdStartST = conventions.shiftedTimeFromPrd(period);
      int incStartST = conventions.shiftedTimeFromTimeOfDay(incident.occurrenceTime());
      int incStopST = conventions.shiftedTimeFromTimeOfDay(incident.occurrenceTime() + incident.duration());
      assert (incStartST <= incStopST) : Constants.SOFTWARE_ERROR + "incStartST=["+incStartST+"] <= incStopST=["+incStopST+"] required";

      int overlapDur = Math.max(0,(Math.min(nsePrdStartST+tBasePrd,incStopST)-Math.max(nsePrdStartST,incStartST)));
      if ((double)overlapDur/(double)tBasePrd < minRelFrac_NSE_periodOverlap)
      {
         name = config.stringValueForName("transcad:normalNetwork");
      }
      else
      {
         name = config.stringValueForName("transcad:disturbedNetwork");
      }
      if (name == null)
      {
         logger.error("No networkName for period=["+period+"]");
      }
      return name;
   }

}

