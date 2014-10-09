package be.uhasselt.imob.feathers2.core.impl.agent;

import be.uhasselt.imob.feathers2.core.IActivBoundCooperation;
import be.uhasselt.imob.feathers2.core.ISocialAgent;
import be.uhasselt.imob.feathers2.core.ITripCompBoundCooperation;
import java.util.Collection;

/** Standard basic <code>Feathers2</code> agent (actor).
<ol>
   <li>This is the minimal operational social agent.</li>
   <li>It can be used for schedule generation but not for traffic simulation.</li>
</ol>
@author IMOB/lk
@version $Id: EvolvableSocialAgent.java 48 2011-02-24 14:20:02Z LukKnapen $
*/

public class EvolvableSocialAgent extends EvolvableAgent implements ISocialAgent

{
   // -----------------------------------------------------------------------
   /** See {@link be.uhasselt.imob.feathers2.core.ISocialAgent#activBoundCooperations()} */
   public Collection<IActivBoundCooperation> activBoundCooperations()
   {
      return null; // TODO:LK
   }

   /** See {@link be.uhasselt.imob.feathers2.core.ISocialAgent#tripCompBoundCooperations()} */
   public Collection<ITripCompBoundCooperation> tripCompBoundCooperations()
   {
      return null; // TODO:LK
   }

}
