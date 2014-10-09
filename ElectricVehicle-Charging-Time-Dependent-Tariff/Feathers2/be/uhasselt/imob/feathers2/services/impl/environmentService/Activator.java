package be.uhasselt.imob.feathers2.services.impl.environmentService;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.transcadService.ITranscadService;
import be.uhasselt.imob.services.configService.ConfigService;
import java.util.Hashtable;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;

/**
 * WIDRS Environment/Support bundle
 *   <ol>
 *    <li>This service activator was derived from {@link be.uhasselt.imob.feathers2.services.impl.abstractService.Activator}</li>
 *    <li>It instantiates an EnvironmentService but (for now) no EnvironmentCommandProvider. </li>
 *    <li>It uses the Config service : whenever that one is restarted, the services supplied by this bundle are renewed (new objects are instantiated and registered in the bundle context).</li>
 * </ol>
 * @author IMOB/Luk Knapen
 * @version $Id: Activator.java 654 2013-02-06 22:07:35Z LukKnapen $
 *
 */
public class Activator extends be.uhasselt.imob.feathers2.services.impl.abstractService.Activator
{
	private EnvironmentService envService = null;
	private ITranscadService tcService = null;
	private ICommonsService commonsService = null;

	public synchronized void start (BundleContext bundleContext) throws Exception
	{
		assert (bundleContext != null) : Constants.PRECOND_VIOLATION + "bundleContext == null";
		super.start(bundleContext);
		String msg = "Activator.start() : "+getClass().getName();
		System.out.println(msg);
		logger.info(msg);
		startTracking("be.uhasselt.imob.services.configService.ConfigService");
		startTracking("be.uhasselt.imob.feathers2.services.transcadService.ITranscadService");
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
		this.config = cfgSrv.getConfig();
		reRegister();
	}

	// ======================================================================
	/**
      Set reference to TranscadService detected.
      Every service provided is unRegistered; the bundle reinitializes
      using the new configuration and s services provided.
      This method is called by a ServiceTracking object (using reflection).
      @param commonsSrv the service detected by a listener
      @precond. <code>tcSrv != null</code>
	 */
	public void set(ICommonsService commonsSrv) throws Exception
	{
		this.commonsService = commonsSrv;
		reRegister();
	}

	// ======================================================================
	/**
      Set reference to TranscadService detected.
      Every service provided is unRegistered; the bundle reinitializes
      using the new configuration and s services provided.
      This method is called by a ServiceTracking object (using reflection).
      @param tcSrv the service detected by a listener
      @precond. <code>tcSrv != null</code>
	 */
	public void set(ITranscadService tcSrv) throws Exception
	{
		this.tcService = tcSrv;
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
		if (envService != null) envService.cleanup();
		// unregister all provided services because bundle will reinitialize
		unRegister();

		if (config != null && tcService != null && commonsService != null)
		{
			try
			{
				envService = new EnvironmentService(config,commonsService,tcService);
				logger.debug("Created EnvironmentService");
				if (envService == null) logger.error("Activator.reRegister() : envService == null");

            // In order to be flexible, register a service under the name of the interface it implements (and do not use class names).
            // The registry is a "mapping" : key ==> value : object ==> method.
            // The key is the type used in the specified method (in order to generate an unambiguous mechanism to find the method to be called (using reflection)).
				Hashtable<String, String> opts = new Hashtable<String, String>();
				servicesProvided.add(context.registerService("be.uhasselt.imob.feathers2.services.environmentService.IEnvironmentService", envService, opts));
				// servicesProvided.add(context.registerService(CommandProvider.class.getName(), envCP, opts));
				logger.debug("Registered Environment/Support services");
			}
			catch (Throwable e)
			{
				logReporter.reportThrowable(logger,"Activator.reRegister()",e);
			}
		}
	}
}
