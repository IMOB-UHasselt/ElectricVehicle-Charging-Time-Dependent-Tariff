package be.uhasselt.imob.feathers2.core;

/**
   IAgentPosition specifies the network link on which an agent resides along with <em>actual arrival</em> (link entry) and <em>expected departure</em> (link exit) times.
   <ol>
      <li>The position is completely defined by the number of the network {@link be.uhasselt.imob.feathers2.core.network.INetworkLink link} the <code>Agent</code> resides on.</li>
   </ol>
   
   @author IMOB/lk
   @version $Id: IAgentPosition.java 125 2011-06-03 14:11:58Z LukKnapen $
*/
public interface IAgentPosition
{
   /** <code>NetworkLink</code> identification number identifying current location. */
   int linkNr();

   /** Actual arrival time on <code>NetworkLink</code> (milliseconds after January 1, 1970 00:00:00 GMT) */
   long entryTs();

   /** Expected departure time from <code>NetworkLink</code> (milliseconds after January 1, 1970 00:00:00 GMT) */
   long exitTs();
}

