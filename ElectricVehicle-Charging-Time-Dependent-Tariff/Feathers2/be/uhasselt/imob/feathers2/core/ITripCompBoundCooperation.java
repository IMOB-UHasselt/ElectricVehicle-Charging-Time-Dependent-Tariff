package be.uhasselt.imob.feathers2.core;
import java.util.Collection;

/**
   Trip component bound cooperation.
   <ol>
      <li><em>Trip Component Bound Relationships</em> emerge from trip component bound cooperation.</li>
      <li>Trip component bound cooperation sets can be used to implement {@link be.uhasselt.imob.feathers2.core.IAgentSetPerceptionFilter}s (because the cooperators set is a set of agents).</li>
      <li>Trip component cooperation always requires all agents/actors to cooperate.</li>
   </ol>
   @author IMOB/lk
   @version $Id: ITripCompBoundCooperation.java 47 2011-02-24 14:16:48Z LukKnapen $
*/
public interface ITripCompBoundCooperation
{
   /** The nonempty set of agents/actors that will cooperate in some way.
      @postcond. <code>(cooperators() != null) and (cooperators().size() &gt; 1)</code>
   */
   Collection<IAgent> cooperators();

   /** The trip component that will be traveled in cooperation (carpooling or equivalent).
      @postcond. <code>tripComponent() != null</code>
   */
   ITripComponent tripComponent();

}
