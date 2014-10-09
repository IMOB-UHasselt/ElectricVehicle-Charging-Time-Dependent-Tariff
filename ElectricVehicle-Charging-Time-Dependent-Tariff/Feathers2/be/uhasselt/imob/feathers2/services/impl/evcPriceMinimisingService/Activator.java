package be.uhasselt.imob.feathers2.services.impl.evcPriceMinimisingService;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService;
import be.uhasselt.imob.services.configService.ConfigService;
import java.util.Hashtable;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;

/**
   EVCPriceMinimisingService bundle activator.
   <ol>
      <li>This service activator was derived from {@link be.uhasselt.imob.feathers2.services.impl.abstractService.Activator}</li>
      <li>It instantiates an EVCPriceMinimisingService and the associated EVCPriceMinimisingCommandProvider.</li>
      <li>It uses the Config service : whenever that one is restarted, the services supplied by this bundle are renewed (new objects are instantiated and registered in the bundle context).</li>
   </ol>
   @author IMOB/lk
   @version $Id: Activator.java 505 2012-11-28 21:53:52Z sboshort $
 */
public class Activator extends be.uhasselt.imob.feathers2.services.impl.abstractService.Activator
{
	// ======================================================================
	/** EVCPriceMinimisingService : a reference is kept in order to be able to cleanly
       shutdown after use.
	 */
	private EVCPriceMinimisingService evcpms = null;
	private EVC_VitoIntegrationService evIntegMdl = null;
	private EVCAverageCostService evcAvgCost = null;
	private EVC_WIDRSCostCompService evcwidrs = null;
	private IEntityManagerService entityManagerService;
	private ICommonsService commonsSrv;

	// ======================================================================
	public synchronized void start (BundleContext bundleContext) throws Exception
	{
		assert (bundleContext != null) : Constants.PRECOND_VIOLATION + "bundleContext == null";
		super.start(bundleContext);
		String msg = "Activator.start() : "+getClass().getName();
		System.out.println(msg);
		logger.info(msg);
		startTracking("be.uhasselt.imob.services.configService.ConfigService");
		startTracking("be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService");
		startTracking("be.uhasselt.imob.feathers2.services.commonsService.ICommonsService");
	}

	// ======================================================================
	/**
      Set reference to Config detected service.
      Every service provided is unRegistered; the bundle reinitializes
      using the new configuration and s services provided.
      This method is called by a ServiceTracking object (using reflection).
      @param cfgSrv the configuration service detected by a listener
      @precond. <code>cfg != null</code>
	 */
	public void set(ConfigService cfgSrv) throws Exception
	{
		logger.info("Setting newly detected config service");
		this.config = cfgSrv.getConfig();
		reRegister();
	}	// ======================================================================
	/**
    Set reference to ICommonsService detected service.
    Every service provided is unRegistered; the bundle reinitializes
    using the new services provided.
    This method is called by a ServiceTracking object (using reflection).
    @param comSrv the service detected by a listener
    @precond. <code>comSrv != null</code>
	 */
	public void set(ICommonsService comSrv) throws Exception
	{
		logger.info("Setting newly detected ICommonsService");
		this.commonsSrv = comSrv;
		reRegister();
	}
	// ======================================================================
	/**
      Set reference to EntityManagerService.
      Every service provided is unRegistered; the bundle reinitializes
      using the new service provided.
      This method is called by a ServiceTracking object (using reflection).
      @param entityManagerService the entityManagerService service detected by a listener
      @precond. <code>entityManagerService != null</code>
	 */
	public void set(IEntityManagerService entityManagerService) throws Exception
	{
		logger.info("Setting newly detected FetchEpisode service");
		this.entityManagerService = entityManagerService;
		reRegister();
	}

	// ======================================================================
	/**
      ReRegisters all services provided by this bundle (in the bundleContext).
      <ol>
         <li>First all services are unRegistered, then if everything that is needed is detected to be effectively available, the services provided by this bundle are registered again.</li>
         <li>This is the most simple but drastic way : other bundles can/shall use more specific delicate methods.</li>
      </ol>
	 */
	private synchronized void reRegister()
	{
		// first nicely shutdown services
		if (evcpms != null) evcpms.cleanup();
		// unregister all provided services because bundle will reinitialize
		unRegister();

		if (config != null &&
				entityManagerService != null &&
				commonsSrv != null &&
				// add here other services the specific bundle depends on
				true)
		{
			try
			{

				evcpms= new EVCPriceMinimisingService(config,entityManagerService,commonsSrv);
				logger.debug("Created EVCPriceMinimisingService");
				if (evcpms == null) logger.error("Activator.reRegister() : evcpms == null");
				
				evIntegMdl= new EVC_VitoIntegrationService(config,entityManagerService,commonsSrv);
				logger.debug("Created EVC_VitoIntegrationService");
				if (evIntegMdl == null) logger.error("Activator.reRegister() : evIntegMdl == null");
				
				evcAvgCost = new EVCAverageCostService(config,entityManagerService,commonsSrv);
				logger.debug("Created EVCAverageCostService");
				if (evcAvgCost == null) logger.error("Activator.reRegister() : evcAvgCost == null");
				
				evcwidrs = new EVC_WIDRSCostCompService(config,entityManagerService,commonsSrv);
				logger.debug("Created EVC_WIDRSCostCompService");
				if (evcwidrs == null) logger.error("Activator.reRegister() : evcwidrs == null");

				CommandProvider evcprCp = new EVCPriceMinimisingCommandProvider(config,evcpms,evIntegMdl,evcAvgCost,evcwidrs);
				logger.debug("Created CommandProvider");
				if (evcprCp == null) logger.error("Activator.reRegister() : evcprCp == null");

				// In order to be flexible, register a service under the name of the interface it implements (and do not use class names).
				// The registry is a "mapping" : key ==> value : object ==> method.
				// The key is the type used in the specified method (in order to generate an unambiguous mechanism to find the method to be called (using reflection)).
				Hashtable<String, String> opts = new Hashtable<String, String>();
				servicesProvided.add(context.registerService(EVCPriceMinimisingService.class.getName(), evcpms, opts));
				servicesProvided.add(context.registerService(EVC_WIDRSCostCompService.class.getName(), evcwidrs, opts));
				servicesProvided.add(context.registerService(EVC_VitoIntegrationService.class.getName(), evIntegMdl, opts));
				servicesProvided.add(context.registerService(EVCAverageCostService.class.getName(), evcAvgCost, opts));
				servicesProvided.add(context.registerService(CommandProvider.class.getName(), evcprCp, opts));
				logger.debug("Registered EVCPriceMinimisingService");
			}
			catch (Throwable e)
			{
				logReporter.reportThrowable(logger,"Activator.reRegister()",e);
				e.printStackTrace();
			}
		}
	}

}
