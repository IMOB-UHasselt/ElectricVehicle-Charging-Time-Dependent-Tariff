package be.uhasselt.imob.feathers2.services.impl.entityManagerService;


import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService;
import be.uhasselt.imob.feathers2.services.environmentService.IEnvironmentService;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.services.configService.ConfigService;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;

/**
   Fetch Episode Service bundle activator.
   <ol>
      <li>This service activator was derived from {@link be.uhasselt.imob.feathers2.services.impl.abstractService.Activator}</li>
      <li>It instantiates an Fetch Episode service </li>
      <li>It uses the Config service : whenever that one is restarted, the services supplied by this bundle are renewed (new objects are instantiated and registered in the bundle context).</li>
   </ol>
   @author MuhammadUsman
   @version $Id: Activator.java 849 2013-05-21 11:41:58Z MuhammadUsman $
 */
public class Activator extends be.uhasselt.imob.feathers2.services.impl.abstractService.Activator {

	private EntityManagerService ems = null;

	private IEnvironmentService envirSrv = null;

	private ICommonsService commonsSrv = null;

	public synchronized void start (BundleContext bundleContext) throws Exception
	{
		assert (bundleContext != null) : Constants.PRECOND_VIOLATION + "bundleContext == null";
		super.start(bundleContext);
		String msg = "Activator.start() : "+getClass().getName();
		logger.info(msg+"  .");
		startTracking("be.uhasselt.imob.services.configService.ConfigService");
		startTracking("be.uhasselt.imob.feathers2.services.commonsService.ICommonsService");
		startTracking("be.uhasselt.imob.feathers2.services.environmentService.IEnvironmentService");
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
	}
	// ======================================================================
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
	      Set reference to detected service.
	      Every service provided is unRegistered; the bundle reinitializes
	      using the new services provided.
	      This method is called by a ServiceTracking object (using reflection).
	      @param envSrv the service detected by a listener
	      @precond. <code>comSrv != null</code>
	 */
	public void set(IEnvironmentService envSrv) throws Exception
	{
		logger.info("Setting newly detected IEnvironmentService");
		this.envirSrv = envSrv;
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
		if (ems != null) ems.cleanup();
		// unregister all provided services because bundle will reinitialize
		unRegister();

		if (config != null &&
				commonsSrv != null &&
            envirSrv != null
         )
		{
			try
			{
				ems= new EntityManagerService(config,commonsSrv,envirSrv);
				logger.debug("Created EntityManagerService");
				if (ems == null) logger.error("Activator.reRegister() : fes == null");

				Hashtable<String, String> opts = new Hashtable<String, String>();
				// In order to be flexible, register a service under the name of the interface it implements (and do not use class names).
				// The registry is a "mapping" : key ==> value : object ==> method.
				// The key is the type used in the specified method (in order to generate an unambiguous mechanism to find the method to be called (using reflection)).
				servicesProvided.add(context.registerService("be.uhasselt.imob.feathers2.services.entityManagerService.IEntityManagerService", ems, opts));
				logger.debug("Registered FetchEpisode services");
			}
			catch (Throwable e)
			{
				logReporter.reportThrowable(logger,"Activator.reRegister()",e);
			}
		}
	}

}
