package be.uhasselt.imob.feathers2.core;

/**
   The set of all <code>Agent</code>s handled by a specific virtual machine.
   <ol>
      <li>The set of agent can get partitioned into subsets that contain only agents depending on agents in the same subset.</li>
      <li>The dependencies are defined by <em>Activity-bound</em> and <em>TripComponent-bound</em> relationships.</li>
      <li>Note that the {@link be.uhasselt.imob.feathers2.core.IEvolver Evolver} needs to know all agents because of mutual influencing on the transportation network. An Evolver however only needs a small amount of data about an agent (and consequently does not need an instance of this class).</li>
   </ol>
   
   @author IMOB/lk
   @version $Id: IAgentSet.java 52 2011-03-10 07:26:02Z LukKnapen $
*/
public interface IAgentSet
{
   /** Maximum number of agents in the set. */
   int capacity();

   /** Fetch agent identified by number.
       @param id agent identification number
       @return null iff no agent registered for specified identifier
       @precond. <code>(id in [0 .. {@link #capacity()}-1])</code>
   */
   IAgent agentForId(int id);

   /** Register agent in the directory
       @param agent to be registered
   */
}
