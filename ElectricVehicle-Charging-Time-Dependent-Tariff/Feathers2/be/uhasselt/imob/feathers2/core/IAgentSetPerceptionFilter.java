package be.uhasselt.imob.feathers2.core;

import java.util.Collection;

/**
   Perception filter defining a restrictive view on the set of agents.
   <ol>
      <li>It defines a set of agents.</li>
   </ol>
   @author IMOB/lk
   @version $Id: IAgentSetPerceptionFilter.java 45 2011-02-23 17:05:01Z LukKnapen $
*/
public interface IAgentSetPerceptionFilter
{
   /** Returns the subset of agents that passed the filter. */
   Collection agents();
}
