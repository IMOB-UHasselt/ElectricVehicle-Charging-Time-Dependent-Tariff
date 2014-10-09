package be.uhasselt.imob.feathers2.core;
import java.util.Collection;

/**
   Activity bound cooperation.
   <ol>
      <li><em>Activity Bound Relationships</em> emerge from activity bound cooperation.</li>
      <li>Activity bound cooperation sets can be used to implement {@link be.uhasselt.imob.feathers2.core.IAgentSetPerceptionFilter}s (because the cooperators set is a set of agents).</li>
   </ol>
   @author IMOB/lk
   @version $Id: IActivBoundCooperation.java 47 2011-02-24 14:16:48Z LukKnapen $
*/
public interface IActivBoundCooperation
{
   enum IActivBoundCoopKind
   {
      /** Exactly one actor shall execute the activity. */
      coopOne,
      /** The activity is to be executed by least one actor in a cooperation. */
      coopAtLeastOne,
      /** All actors in the cooperators set shall work together to execute the activity. */
      coopAll
   }

   /** The nonempty set of agents/actors that will cooperate in some way.
      @postcond. <code>(cooperators() != null) and (cooperators().size() &gt; 1)</code>
   */
   Collection<IAgent> cooperators();

   /** The activity on which agents cooperate.
      @postcond. <code>activity() != null</code>
   */
   IActivity activity();

   /** Cooperation kind : the way in which agents cooperate on teh activity. */
   IActivBoundCoopKind kind();
}
