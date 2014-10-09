package be.uhasselt.imob.feathers2.core;
/**
   An <em>evolvable</em> agent has a mutable position on the network so that it can participate in traffic evolution by an {@link be.uhasselt.imob.feathers2.core.IEvolver evolver}.
   <ol>
      <li>TODO:LK REGISTER SPECIAL REQUIREMENTS TO NETWORK PERCEPTION FILTERS HERE (IF ANY)</li>
      <li>The most interesting network to calculate traffic evolution is the smallest network used as the common source for perception filtering by all agents.</li>
   </ol>
   @author IMOB/lk
   @version $Id: IEvolvableAgent.java 52 2011-03-10 07:26:02Z LukKnapen $
*/
public interface IEvolvableAgent
{
   /** Returns agent position on the network.
      @postcond. <code>this.position() != null</code>
   */
   IAgentPosition position();
}
