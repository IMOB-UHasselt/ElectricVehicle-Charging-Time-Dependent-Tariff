package be.uhasselt.imob.feathers2.core;

import be.uhasselt.imob.feathers2.core.network.INetworkLink;
/**
   Location address. Can be used for a.o
   <ol>
      <li><em>home</em> address : this is where the agent officially sleeps</li>
      <li>work location addess</li>
      <li>bus stop</li>
      <li>...</li>
   </ol>
   @author IMOB/lk
   @version $Id: ILocation.java 999 2013-12-26 12:42:59Z LukKnapen $
*/
public interface ILocation
{
   /** Network link containing <em>home</em>, <em>work location addess</em>, <em>bus stop</em> or whatever physical loaction is designated by the object..
      @postcond. {@link #link()}.{@link be.uhasselt.imob.feathers2.core.network.INetworkLink#supportsActivity() supportsActivity()}
   */
   INetworkLink link();

   /** Relative position on link.
   Not that a link is not necessarily represented by a straight line.
   <ol>
      <li>Value 0.0 (zero) corresponds to {@link be.uhasselt.imob.feathers2.core.network#origNode()}</li>
      <li>Value 1.0 (one) corresponds to {@link be.uhasselt.imob.feathers2.core.network#destNode()}</li>
      <li>The relative position is relevant on long links.</li>
   </ol>
      @postcond. <code>0.0 &lt;= returnValue &lt;= 1.0</code>
   */
   double relPos();
}
