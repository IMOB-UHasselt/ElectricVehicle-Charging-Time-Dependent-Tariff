package be.uhasselt.imob.feathers2.services.impl.environmentService;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.environmentService.IEnvironmentService;
import be.uhasselt.imob.feathers2.services.environmentService.IExtActivStats;
import be.uhasselt.imob.feathers2.services.environmentService.IIncident;
import be.uhasselt.imob.feathers2.services.environmentService.IRandoms;
import be.uhasselt.imob.feathers2.services.environmentService.ITA_NetworkMgr;
import be.uhasselt.imob.feathers2.services.impl.abstractService.Service;
import be.uhasselt.imob.feathers2.services.transcadService.ITranscadService;
import be.uhasselt.imob.library.config.Config;
import be.uhasselt.imob.services.configService.ConfigService;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Environment/Support service.
 * @author IMOB/Luk Knapen
 * @version $Id: EnvironmentService.java 715 2013-03-02 17:11:00Z LukKnapen $
 */
public class EnvironmentService implements IEnvironmentService
{
   /** Reference to configurator. */
   private Config config = null;
   /** Config data validity status indicator */
   private boolean configError = false;

   private Logger logger = null;
   private final static DecimalFormat fmt_i02 = new DecimalFormat("00");

   /** The traffic assignement network manager. */
   private TA_NetworkMgr_Step ta_NetworkMgr = null;
   /** The incident descriptor. */
   private Incident incident = null;
   /** The random number generators. */
   private Randoms randoms = null;
   /** The external proces activity statistics manager. */
   private IExtActivStats extActivStats = null;
   /** Commons */
   private ICommonsService commonsService = null;

   // =========================================================================
	public EnvironmentService(Config config, ICommonsService commonsService, ITranscadService transcadService) throws F2Exception
   {
		assert (config != null) : be.uhasselt.imob.feathers2.core.Constants.PRECOND_VIOLATION + "config == null";

		if (logger == null) 
			this.logger = Logger.getLogger(getClass().getName());

      configError = false;
      loadConfig(config);
      if (configError)
      {
         throw new F2Exception("Config error(s) : consult log.");
      }
      else
      {
         randoms = new Randoms();
         incident = new Incident(config,commonsService.conventions());
         ta_NetworkMgr = new TA_NetworkMgr_Step(config,commonsService,transcadService,incident);
         transcadService.reportTCADstats();
      }
	}

   // =========================================================================
   private boolean loadConfig(Config config)
   {
      return !configError;
   }

   // =========================================================================
   private String fetchStringConfigValue(String name)
   {
      String value = config.stringValueForName(name);
      if (value == null)
      {
         logger.error("Config error : no value for name=["+name+"]");
         configError = true;
      }
      return value;
   }

   // =========================================================================
   private int fetchIntConfigValue(String name)
   {
      int value = 0;
      try
      {
         value = config.intValueForName(name);
      }
      catch (NumberFormatException nfe)
      {
         logger.error("Config error : no value for name=["+name+"] : Exception=["+nfe.getMessage()+"]");
         configError = true;
      }
      return value;
   }

   // =========================================================================
   /* inherit from interface */
   public IIncident incident()
   {
      return this.incident;
   }

   // =========================================================================
   /* inherit from interface */
   public ITA_NetworkMgr ta_NetworkMgr()
   {
      return this.ta_NetworkMgr;
   }

   // =========================================================================
   /* inherit from interface */
   public IRandoms randoms()
   {
      return this.randoms;
   }

   // =========================================================================
   /* inherit from interface */
   public IExtActivStats extActivStats()
   {
      return this.extActivStats;
   }

   // =========================================================================
   /* inherit from interface */
   public void cleanup()
   {
   }

}
