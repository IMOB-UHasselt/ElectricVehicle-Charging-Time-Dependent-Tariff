package be.uhasselt.imob.feathers2.services.impl.environmentService;

import be.uhasselt.imob.feathers2.services.environmentService.ITA_NetworkMgr;

import org.apache.log4j.Logger;

/**
   Traffic Assignment Network Manager abstract class : the root of a amily of classes modeling the effect of incidents by creating sets of network descriptions.
   <ol>
      <li>Each network has a specific set of reduced capacity links.</li>
      <li>Each network holds for a given period.</li>
      <li>The transitions between the capacity values of a link over time, defines the name of the TA_NetworkMgr subclass.</li>
   </ol>
*/
public abstract class TA_NetworkMgr implements ITA_NetworkMgr
{
   protected Logger logger = null;

   // --------------------------------------------------------------------------------
   /** Constructor: sets up everything required to supply networks later on.
   */
   protected TA_NetworkMgr()
   {
      if (logger == null) logger = Logger.getLogger(getClass().getName());
   }

   // --------------------------------------------------------------------------------
   /** Returns the name for the network that holds for the <em>taBasePeriod</em> specified by <code>prd</code>.
      @param period <em>taBasePeriod</em> offset within the day
   */
   abstract public String networkName(int period);

}

