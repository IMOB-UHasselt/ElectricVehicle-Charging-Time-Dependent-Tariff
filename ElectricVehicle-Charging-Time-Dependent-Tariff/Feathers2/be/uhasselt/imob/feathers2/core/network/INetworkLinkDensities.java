package be.uhasselt.imob.feathers2.core.network;

/**
   INetworkLinkDensities contains the density for each link on the transportation network for a specific moment in time (snapshot).
   <ol>
      <li><code>NetworkLinkDensities</code> maps the set of <code>NetworkLink</code>s onto ??? TODO </li>
   </ol>
   
   @author IMOB/lk
   @version $Id: INetworkLinkDensities.java 126 2011-06-03 14:26:13Z LukKnapen $
*/
public interface INetworkLinkDensities
{
   /** Moment in time for which the densities snapshot holds.
       @return number millisecs since 1970-jan-01 00:00:00 GMT */
   long snapshotTime();

   /** Get (reference to) the <code>Network</code> for which the <code>IAgentPositions</code> holds. */
   INetwork network();

   /** 
   */
   void registerDensity(INetworkLink networkLink, INetworkLinkDensity networkLinkDensity);
   
   /** Fetch density for given link.
       @param link selected link
       @return density specifier
       @precond. <code>(networkLink != null)</code>
       @precond. <code>(networkLink.id() in [0 .. {@link #network()}.nLinks()-1])</code>
   */
   INetworkLinkDensity densityForLink(INetworkLink link);

   /** Fetch density for link identifier.
       @param linkId selected link identifier
       @return density specifier
       @precond. <code>(link.id in [0 .. {@link #network()}.nLinks()-1])</code>
   */
   INetworkLinkDensity densityForLink(int linkId);

}

