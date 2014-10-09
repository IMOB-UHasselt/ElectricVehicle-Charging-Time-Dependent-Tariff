package be.uhasselt.imob.feathers2.services.impl.abstractService;

import be.uhasselt.imob.library.base.Constants;
import be.uhasselt.imob.library.base.LogReporter;
import be.uhasselt.imob.library.config.Config;
import org.apache.log4j.Logger;

/**
   Extend this class to create your services.
   @author IMOB/lk
   @version $Id: Service.java 152 2012-04-11 15:58:28Z LukKnapen $
*/
public abstract class Service
{
   // ----------------------------------------------------------------------
   /** The logger is not created here but in the derived classes in order to
       get specific logger names.
   */
   protected Logger logger = null;

   /** LogReporter is used to log exceptions in a uniform way. */
   protected LogReporter logReporter = new LogReporter();

   /** Each service makes use of the config service. */
   protected Config config = null;

   // ----------------------------------------------------------------------
   /** Constructor.
      @param cfg f2-wide configuration data source
      @precond. <code>cfg != null</code>
   */
   public Service(Config cfg)
   {
      assert (cfg != null) : Constants.PRECOND_VIOLATION + "cfg == null";
      config = cfg;
   }

}
