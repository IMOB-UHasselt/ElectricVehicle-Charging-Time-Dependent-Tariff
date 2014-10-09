package be.uhasselt.imob.feathers2.core.impl.agent;

import be.uhasselt.imob.feathers2.core.IActivBoundCooperation;
import be.uhasselt.imob.feathers2.core.ISocialAgent;
import be.uhasselt.imob.feathers2.core.ITripCompBoundCooperation;
import java.util.Collection;

/** Fully equipped agent.
@author IMOB/lk
@version $Id: EvolvableSocialAgentWithMemory.java 47 2011-02-24 14:16:48Z LukKnapen $
*/

public class EvolvableSocialAgentWithMemory extends EvolvableAgentWithMemory implements ISocialAgent
{
   // -----------------------------------------------------------------------
   /** See {@link be.uhasselt.imob.feathers2.core.ISocialAgent#activBoundCooperations()} */
   public Collection<IActivBoundCooperation> activBoundCooperations()
   {
      return null; // TODO
   }

   // -----------------------------------------------------------------------
   /** See {@link be.uhasselt.imob.feathers2.core.ISocialAgent#tripCompBoundCooperations()} */
   public Collection<ITripCompBoundCooperation> tripCompBoundCooperations()
   {
      return null; // TODO
   }

}
