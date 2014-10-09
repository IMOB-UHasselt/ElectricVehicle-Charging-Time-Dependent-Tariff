package be.uhasselt.imob.feathers2.services.impl.abstractService;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.library.base.LogReporter;
import be.uhasselt.imob.library.config.Config;
import be.uhasselt.imob.library.osgi.ServiceTracking;
import be.uhasselt.imob.services.configService.ConfigService;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
   Service bundle activator.
   <ol>
      <li>Concrete services shall extend this abstract class (to make software maintainable).</li>
      <li>This class determines what services are required at a given moment in time. This is compatible with but not identical to the dependencies registered in the bundle manifest.</li>
      <li>At any given moment in time, dependencies shall constitute a directed cycle free graph.</li>
      <li>{@link be.uhasselt.imob.services.configService.ConfigService ConfigService} is used.</li>
      <li>For doc see <em>Introduction to OSGi, Vorlesung Softwarekomponententechnologie Prof. Dr. Klaus Ostermann Sommersemester 2007 Technische Universitaet Darmstadt Fachbereich Informatik, Dr.-Ing. Michael Eichberg</em></li>
   </ol>
   @author IMOB/lk
   @version $Id: Activator.java 522 2012-12-01 17:06:55Z sboshort $
*/
abstract public class Activator implements BundleActivator
{
   // ======================================================================
   /** OSGi bundle context. */
   protected BundleContext context = null;

   /** Configuration service bundle. */
   protected Config        config  = null;

   /** Logger */
   protected Logger        logger  = Logger.getLogger(this.getClass().getName());

   /** LogReporter */
   protected LogReporter   logReporter = new LogReporter();

   /** List of services made available by this bundle. */
   protected List<ServiceRegistration> servicesProvided = new ArrayList<ServiceRegistration>();

   /** Service tracker. */
   protected ServiceTracking srvTrack = null;

   // ======================================================================
   @Override
   public synchronized void start (BundleContext bundleContext) throws Exception
   {
      assert (bundleContext != null) : Constants.PRECOND_VIOLATION + "bundleContext == null";
      context = bundleContext;

      // Setup service tracker
      srvTrack = new ServiceTracking(context, this);
   }

   // ======================================================================
   /** Start tracking service availability.
      @param srvName Class name for service to track.
      @precond. <code>srvName != null</code>
   */
   protected synchronized void startTracking(String srvName, String methodName) throws Exception
   {
      assert (srvName != null) : Constants.PRECOND_VIOLATION + "srvName == null";
      assert (methodName != null) : Constants.PRECOND_VIOLATION + "methodName == null";

      // Setup service tracker
      logger.debug("Activator.start() : created service tracker for srvName=["+srvName+"]");
      logger.debug("activator ClassLoader=["+getClass().getClassLoader()+"] delegate=["+((DefaultClassLoader)getClass().getClassLoader()).getDelegate()+"] bundle=["+((DefaultClassLoader)getClass().getClassLoader()).getBundle().getSymbolicName()+"]");
      logger.debug("srvTrack ClassLoader=["+srvTrack.getClass().getClassLoader()+"] delegate=["+((DefaultClassLoader)srvTrack.getClass().getClassLoader()).getDelegate()+"] bundle=["+((DefaultClassLoader)srvTrack.getClass().getClassLoader()).getBundle().getSymbolicName()+"]");
      logger.debug("context ClassLoader=["+context.getClass().getClassLoader()+"]");
      
      // The registry is a mapping : interface ==> Activator method.
      // By default, the activator uses the method name "set" for all cases; at runtime cases are distinguished by the parameter type which is an interface.
      // For required services, the *interface* object is to be registered with the serviceTracker, not the *interface name*.

      // In order to keep things flexible, interfaces are tracked and not (implementation) classes.
      try
      {
         // Use this bundle's class loader to find the interface object.
         // The class loader's classpath is determined by 'import' en 'required-bundle' settings in the manifest file.
         if (!srvTrack.addServiceToTrack(((DefaultClassLoader)getClass().getClassLoader()).getBundle().loadClass(srvName),methodName,null))
         {
            logger.info("activator ClassLoader=["+getClass().getClassLoader()+"] delegate=["+((DefaultClassLoader)getClass().getClassLoader()).getDelegate()+"] bundle=["+((DefaultClassLoader)getClass().getClassLoader()).getBundle().getSymbolicName()+"]");
            logger.info("srvTrack ClassLoader=["+srvTrack.getClass().getClassLoader()+"] delegate=["+((DefaultClassLoader)srvTrack.getClass().getClassLoader()).getDelegate()+"] bundle=["+((DefaultClassLoader)srvTrack.getClass().getClassLoader()).getBundle().getSymbolicName()+"]");
            logger.fatal("Class=["+srvName+"] not registered with ServiceTracking");
         }
         if (!srvTrack.initiate())
         {
            logger.error("Failed to setup ServiceTracker");
         }
         logger.debug("Activator.start() : tracking service=["+srvName+"] via notication method=["+methodName+"]");
      }
      catch (ClassNotFoundException cnfe)
      {
         logReporter.reportThrowable(logger,"Activator.start()",cnfe);
      }
   }

   // ======================================================================
   /** Start tracking service availability.
      @param srvName Class name for service to track.
      @precond. <code>srvName != null</code>
   */
   protected synchronized void startTracking(String srvName) throws Exception
   {
      startTracking(srvName,"set");
   }

   // ======================================================================
   @Override
   public void stop (BundleContext bundleContext) throws Exception
   {
      logger.info("Stopping bundleContext=["+bundleContext+"]");
      srvTrack.stop();
      unRegister(); // unRegister providing services

      // according to Knopferfish OSGi tutorial, requested services do not need
      // to be released : when a bundle stops every requested service is
      // released by the framework code.
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
   abstract public void set(ConfigService cfgSrv) throws Exception;

   // ======================================================================
   /**
      UnRegisters all services provided by this bundle (in the bundleContext).
   */
   protected synchronized void unRegister()
   {
      // unregister all provided services because bundle will reinitialize
      for (ServiceRegistration reg : servicesProvided) reg.unregister();

      servicesProvided.clear();
   }

}
