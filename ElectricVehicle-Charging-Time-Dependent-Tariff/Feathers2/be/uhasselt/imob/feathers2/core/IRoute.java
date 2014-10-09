package be.uhasselt.imob.feathers2.core;

import be.uhasselt.imob.feathers2.core.network.INetwork;
import be.uhasselt.imob.feathers2.core.network.INetworkLink;
import be.uhasselt.imob.feathers2.core.network.INetworkNode;
import java.util.List;

/**
   IRoute Sequence of network links (without repeated vertices) connecting a source to a destination.
   <ol>
      <li>A route is a graph-theoretical <em>simple path</em>.</li>
      <li>Origin and destination shall be different (hence cannot coincide on the same link).
   </ol>
   @author IMOB/lk
   @version $Id: IRoute.java 1391 2014-07-07 18:23:23Z LukKnapen $
*/
public interface IRoute
{
   /** Route identifier. */
   int id();

   /** Route origin : a location on the first link. */
   ILocation origin();

   /** Route destination : a location on the last link. */
   ILocation destination();

   /** First node : the node on the first link that is either not part of another link or the node or (in case of a single link route) has been designated explicitly (per construction) als the first one. */
   INetworkNode firstNode();

   /** Last node : the node on the last link that is either not part of another link or (in case of a single link route) the node or has been designated explicitly (per construction) als the last one. */
   INetworkNode lastNode();

   /** Indicates whether or not the specified link belongs to the route. */
   boolean containsLink(INetworkLink link);

   /** List of network nodes : {@link #origin()} is the first one, {@link #destination()} is the last one). */
   List <INetworkNode> pathNodes();

   /** List of network links: {@link #origin()} is on the first one, {@link #destination()} is on the last one). */
   List <INetworkLink> pathLinks();

   /** Embedding network. */
   INetwork network();

   /** Total cost to traverse the route.
      @param costFunctionId identifies the function to evaluate the cost to traverse the route.
   */
   public float costToTraverse (String costFunctionId);

   /** Total cost to traverse the route from origin to destination.
      @param costFunctionId identifies the function to evaluate the cost to traverse the route.
      @param origOffset offset for origin node in the route
      @param destOffset offset for destination node in the route
      @todo. implement alternative costFunctions; distance is used in current version.
      @precond. <code>(origOffset &gt;= 0) and (destOffset &gt;= origOffset) and (destOffset &lt; {@link #pathNodes()}.size())</code>
   */
   public float costToTraverse (String costFunctionId, int origOffset, int destOffset);

   /** Returns a new route that is derived from this one by reversing the node order.
      @param routeId Identifier for new route.
   */
   public IRoute reversed(int routeId);

   /** Predicate stating that route is a path in the graph theoretic sense (no duplicate vertex use except for first and last vertices.)
   */
   public boolean isPath();

   /** Specified route is to be used in forward order. */
   public boolean forward();
}
