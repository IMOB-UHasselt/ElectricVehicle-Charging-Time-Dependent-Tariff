package be.uhasselt.imob.feathers2.core;

import be.uhasselt.imob.feathers2.core.network.INetwork;
import be.uhasselt.imob.feathers2.core.network.INetworkLink;

/**
   IAgentPositions contains the position (space and time data) on the transportation network for a set of <code>Agent</code>s.
   <ol>
      <li><code>AgentPositions</code> maps the set of <code>Agent</code> onto the set of <code>NetworkLink</code> identifiers.</li>
      <li><code>AgentPositions</code> shall be modified by an {@link be.uhasselt.imob.feathers2.core.IEvolver Evolver} only : it is to be considered as a lookup directory by every other module.</li>
   </ol>
   
   @author IMOB/lk
   @version $Id: IAgentPositions.java 125 2011-06-03 14:11:58Z LukKnapen $
*/
public interface IAgentPositions
{
   /** Get (reference to) the <code>AgentSet</code> for which the <code>IAgentPositions</code> holds. */
   IAgentSet agentSet();

   /** Get (reference to) the <code>Network</code> for which the <code>IAgentPositions</code> holds. */
   INetwork network();

   /** Make <code>agent</code> position known by registering it in the directory. 
   @param agent whose position is to be registered
   @param networkLink the agent location
   @param tsEntry actual time of arrival on link
   @param tsExit actual time of departure from link
   @precond. <code>(agent != null) and (agent.id() in [0 .. {@link #agentSet()}.capacity()-1])</code>
   @precond. <code>(networkLink != null) and (networkLink.id() in [0 .. {@link #network()}.capacity()-1]) </code>
   @precond. <code>(tsEntry != Constants.TS_NEVER) and (tsExit &gt; tsEntry)</code>
   */
   void registerAgentPosition(IAgent agent, INetworkLink networkLink, long tsEntry, long tsExit);
   
   /** Make <code>agent</code> position known by registering it in the directory. 
   @param ap describing agent location
   @precond. <code>(agentPosition != null)</code>
   @precond. <code>(agentPosition.id() in [0 .. {@link #agentSet()}.capacity()-1])</code>
   @precond. <code>(agentPosition.linkNr() in [0 .. {@link #network()}.nLinks()-1])</code>
   @precond. <code>(agentPosition.tsEntry() != Constants.TS_NEVER)</code>
   @precond. <code>(agentPosition.tsExit() &gt; agentPosition.tsEntry())</code>
   */
   void registerAgentPosition(IAgentPosition ap);

   /** Fetch position for given agent.
       @param agent selected agent
       @return null iff no position has been registered for specified agent
       @precond. <code>(agent != null)</code>
   */
   IAgentPosition positionForAgent(IAgent agent);

   /** Fetch position for agent identified by identification number.
       @param agentId identification number for selected agent
       @return null iff no position has been registered for identified agent
       @precond. <code>(agentId in [0 .. {@link #agentSet()}.capacity()-1])</code>
   */
   IAgentPosition positionForAgent(int agentId);
}

