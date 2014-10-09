package be.uhasselt.imob.feathers2.core.network;

import be.uhasselt.imob.feathers2.core.area.IArea;
import java.util.Collection;

/**
   Network node, vertex in the digraph representing the transportation network.
   <ol>
      <li>Coordinates are assumed to be available in every network description.</li>
      <li>GWS84 coordinates are used.</li>
   </ol>
   @author IMOB/lk
   @version $Id: INetworkNode.java 1042 2014-01-03 21:23:41Z LukKnapen $
*/
public interface INetworkNode
{
   /** Identifier, unique */
   int id();

   /** GWS84 Latitude. */
   double lat();

   /** GWS84 Longitude. */
   double lon();

   /** Returns links entering the node.
      @postcond. <code>forall l0 in n0.inLinks() | l0.destNode() == n0</code>
   */
   Collection<INetworkLink> inLinks();

   /** Returns links exiting the node.
      @postcond. <code>forall l0 in n0.outLinks() | l0.origNode() == n0</code>
   */
   Collection<INetworkLink> outLinks();

   /** Returns area to which the node belongs. This always is a leaf in the area tree structure.
      @postcond. <code>containingArea() != null</code>
      @postcond. <code>not exists A0 | A0.{@link be.uhasselt.imob.feathers2.core.area.IArea#parent() parent()} = containingArea()</code>
   */
   IArea containingArea();

   // -----------------------------------------------------------------------
   // -- AESAS --------------------------------------------------------------
   // -----------------------------------------------------------------------
   /** Register <code>AESAS</code> with node.
      @param token granted to the client as an access permission
      @param aesas Algorithm execution specific attribute set
   */
   void registerAesas(INwToken token, AesasNodeAttrib aesas);

   /** Remove <code>AESAS</code> from node.
      @param token granted to the client as an access permission and to which the <code>AESAS</code> is associated
   */
   void clearAesas(INwToken token);

   /** Get <code>AESAS</code> registered for specified token
      @param token for which <code>AESAS</code> is requested
   */
   AesasNodeAttrib aesas(INwToken token);

   /** Shortest distance [m] (along great circle or straight line in plane) between this node and another one.
   characterized by their LAT,LON GWS84 coordinates (using <em>haversine</em> formula).
      Formula taken from http://www.movable-type.co.uk/scripts/gis-faq-5.1.html
      @param n Node to which distance (along great circle) is to be calculated.
   */
   double geoDistanceTo(INetworkNode n);
}
