package be.uhasselt.imob.feathers2.core.impl.agent;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.IAgentPosition;
import be.uhasselt.imob.feathers2.core.IAgentPositions;
import be.uhasselt.imob.feathers2.core.IEvolvableAgent;

/** Standard basic <code>Feathers2</code> <em>evolvable</em> agent (actor).
   <ol>
      <li>This is the minimal operational agent that can participate in traffic evolution (simulation).</li>
      <li>TODO:LK ADD INVARIANTS ABOUT position() AND schedule().trip()</li>
   </ol>
@author IMOB/lk
@version $Id: EvolvableAgent.java 48 2011-02-24 14:20:02Z LukKnapen $
*/

public class EvolvableAgent extends BasicAgent implements IEvolvableAgent

{
   // -----------------------------------------------------------------------
   /** See {@link be.uhasselt.imob.feathers2.core.IEvolvableAgent#position()} */
   public IAgentPosition position()
   {
      return null; // TODO THIS ONE VIOLATES POSTCONDITION FOR NOW
   }

   // -----------------------------------------------------------------------
   /** Make class aware of agent position register to enable agents to report their position.
      @precond. <code>agentPositions != null</code>
   */
   public void registerAgentPositions(IAgentPositions agentPositions)
   {
      assert (agentPositions != null) : Constants.PRECOND_VIOLATION + "agentPositions == null";
      // TODO:LK
   }
}
