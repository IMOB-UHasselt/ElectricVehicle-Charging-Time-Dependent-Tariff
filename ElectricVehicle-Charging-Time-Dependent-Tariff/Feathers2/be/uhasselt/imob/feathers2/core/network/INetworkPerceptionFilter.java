package be.uhasselt.imob.feathers2.core.network;

import be.uhasselt.imob.feathers2.core.IPerceptionFilter;
/**
   A Network Perception Filter defines a view on the transportation network.
   <ol>
      <li>It thus defines a network : as a consequence, this is a subInterface.</li>
      <li></li>
      <li>TODO:LK DEFINE WHAT ATTRIBUTES CAN BE RETRIEVED</li>
      <li>TODO:LK DEFINE WHAT ATTRIBUTES CAN BE SET (ANY ?)</li>
   </ol>
   @author IMOB/lk
   @version $Id: INetworkPerceptionFilter.java 126 2011-06-03 14:26:13Z LukKnapen $
*/
public interface INetworkPerceptionFilter extends IPerceptionFilter, INetwork
{
   /** Reference to the filtered network view.
      @postcond. <code>network != null</code>
   */
   INetwork network();
}
