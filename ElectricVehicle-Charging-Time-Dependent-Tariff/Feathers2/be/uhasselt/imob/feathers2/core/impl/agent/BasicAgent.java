package be.uhasselt.imob.feathers2.core.impl.agent;

import be.uhasselt.imob.feathers2.core.IAgent;
import be.uhasselt.imob.feathers2.core.IAgentStaticData;
import be.uhasselt.imob.feathers2.core.IJobDescriptor;
import be.uhasselt.imob.feathers2.core.IPerceptionFilter;
import be.uhasselt.imob.feathers2.core.ISchedule;
import be.uhasselt.imob.feathers2.core.network.INetworkPerceptionFilter;
import java.util.Collection;
import java.util.List;

/** Standard basic <code>Feathers2</code> agent (actor).
<ol>
   <li>This is the minimal operational agent.</li>
   <li>It can be used for schedule generation but not for traffic simulation.</li>
</ol>
@author IMOB/lk
@version $Id: BasicAgent.java 125 2011-06-03 14:11:58Z LukKnapen $
*/

public class BasicAgent implements IAgent

{
   // -----------------------------------------------------------------------
   /** See {@link be.uhasselt.imob.feathers2.core.IAgent#id()} */
   public int id()
   {
      return 0; // TODO
   }

   // -----------------------------------------------------------------------
   /* see javadoc implemented interfaces */
   public IAgentStaticData staticData()
   {
      return null; // TODO
   }

   // -----------------------------------------------------------------------
   /* see javadoc implemented interfaces */
   public ISchedule schedule()
   {
      return null; // TODO
   }

   // -----------------------------------------------------------------------
   /* see javadoc implemented interfaces */
   public boolean hasFeasibleSchedule()
   {
      return false; // TODO
   }

   // -----------------------------------------------------------------------
   /* see javadoc implemented interfaces */
   public boolean hasFeasibleRoute()
   {
      return false; // TODO
   }

   // -----------------------------------------------------------------------
   /** Returns the set of perception filters used by the agent to look at the world. */
   public INetworkPerceptionFilter networkPerceptionFilter()
   {
      return null; // TODO
   }

   // -----------------------------------------------------------------------
   /** Returns the list of jobDesriptors.
      TODO:LK DO WE NEED A PRE_POST_CONDITION ? HAS_WORK &lt;==&gt; jobDescriptos.size() &gt; 0
   */
   public List<IJobDescriptor> jobDescriptors()
   {
      return null; // TODO
   }

}
