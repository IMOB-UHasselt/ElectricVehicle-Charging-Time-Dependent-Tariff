package be.uhasselt.imob.feathers2.core.impl.agent;

import be.uhasselt.imob.feathers2.core.IAgentMemory;
import be.uhasselt.imob.feathers2.core.IRememberedRoute;
import be.uhasselt.imob.feathers2.core.IRememberedSchedule;
import java.util.Collection;

/** <code>Feathers2</code> agent (actor) having memory.
<ol>
   <li>The memory applies to schedule execution (and thus to trip traveling). As a consequence, this agent is an extension of EvolvableAgent.</li>
   <li></li>
</ol>
@author IMOB/lk
@version $Id: EvolvableAgentWithMemory.java 47 2011-02-24 14:16:48Z LukKnapen $
*/

public class EvolvableAgentWithMemory extends EvolvableAgent
{
   // -----------------------------------------------------------------------
   /* see javadoc implemented interfaces */
   public Collection<IRememberedRoute> routeMem()
   {
      return null; // TODO
   }

   // -----------------------------------------------------------------------
   /* see javadoc implemented interfaces */
   public Collection<IRememberedSchedule> scheduleMem()
   {
      return null; // TODO
   }

}
