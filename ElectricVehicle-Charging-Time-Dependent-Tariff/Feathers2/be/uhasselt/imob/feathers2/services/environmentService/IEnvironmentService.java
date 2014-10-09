package be.uhasselt.imob.feathers2.services.environmentService;

import be.uhasselt.imob.feathers2.services.environmentService.IIncident;
/**
   The TranscadService is the gateway to call TransCAD services by macro execution via the TransCAD AutomationServer.
   @author IMOB/Luk Knapen
   @version $Id: IEnvironmentService.java 505 2012-11-28 21:53:52Z sboshort $
 *
 */
public interface IEnvironmentService
{
   /** Returns incident descriptor. */
   public IIncident incident();

   /** Returns traffic assignment network manager. */
   public ITA_NetworkMgr ta_NetworkMgr();

   /** Returns IRandoms random numbers provider. */
   public IRandoms randoms();

   /** Returns IExtActivStats manager. */
   public IExtActivStats extActivStats();

   /** Cleanup. */
   public void cleanup();
}
