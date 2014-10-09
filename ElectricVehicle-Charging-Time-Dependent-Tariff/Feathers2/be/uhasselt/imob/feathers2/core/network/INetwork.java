package be.uhasselt.imob.feathers2.core.network;

import be.uhasselt.imob.feathers2.core.area.IArea;
import be.uhasselt.imob.feathers2.core.area.IAreaLevel;
import be.uhasselt.imob.feathers2.core.F2Exception;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
   The transportation network.
   <ol>
      <li>A transportation network is a directed graph. It consists of {@link be.uhasselt.imob.feathers2.core.network.INetworkLink}s (edges) and {@link be.uhasselt.imob.feathers2.core.network.INetworkNode}s (vertices). </li>
      <li>A transportation network is considered to be valid iff (if and only if) it is connected : since the network is a digraph, it shall not contain any <em>dead end</em> (cul de sac)</li>
      <li>Each node is assigned to an {@link be.uhasselt.imob.feathers2.core.area.IArea IArea} that is a leaf node in a tree structure (see {@link be.uhasselt.imob.feathers2.core.area.IAreaLevel IAreaLevel}).</li>
   </ol>
    
   @author IMOB/lk
   @version $Id: INetwork.java 1382 2014-07-06 22:34:58Z LukKnapen $
*/
public interface INetwork
{
   /** Returns nodes : Map is used because it is assumed that nodes will be retrieved by number for several applications. Perform lookup using <code>get(int)</code>
      @postcond. <code>n0, n1 in nodes() ==&gt; !n0.id().equals(n1.id())</code>
   */
   public Map<Integer,INetworkNode> nodesMap();

   // -----------------------------------------------------------------------
   /** Returns links : Map is used because it is assumed that links will be retrieved by number for several applications. Perform lookup using <code>get(int)</code>
      @postcond. <code>ln0, ln1 in links() ==&gt; !ln0.id().equals(ln1.id())</code>
   */
   public Map<Integer,INetworkLink> linksMap();

   // -----------------------------------------------------------------------
   /** Returns links.
   */
   public Collection<INetworkLink> links();
   
   // -----------------------------------------------------------------------
   /** Determine Strongly Connected Components, generate sumamry report and register SCC numbers in database. */
   NetworkSccSummary sccSummary();

   // -----------------------------------------------------------------------
   /** Return array with area levels.
   */
   public IAreaLevel[] areaLevels();

   // -----------------------------------------------------------------------
   /** Returns root in the hierarchical IArea structure (the IArea representing the <em>universe</em>).
   */
   public IArea root();

   // -----------------------------------------------------------------------
   /** Returns an index to fetch leaf IAreas in the hierarchical structure. Leaf IAreas contain INetworkNodes.
   */
   public Map<Integer,IArea>leafAreas();

   // -----------------------------------------------------------------------
   /** Mapping from leaf areas to lists of contained nodes.
   */
   public Map<IArea,List<INetworkNode>> leafAreaToNodesMap();

   // -----------------------------------------------------------------------
   /** Number of links dropped (during network loading) because one or more nodes are undefined. */
   public int nLinksDroppedForUndefNodes();

   // -----------------------------------------------------------------------
   /** Number of links dropped (during network loading) because of duplicate definition. */
   public int nLinksDroppedForDupDef();

   // -----------------------------------------------------------------------
   /** Number of unresolved node references detected while loading network. */
   public int nUnresolvedNodeRefs();

   // -----------------------------------------------------------------------
   /** Frequency table for nodes inDegree. Array size is {@link be.uhasselt.imob.feathers2.services.impl.networkService.Network#nDegrees} . The last element counts nodes having {@link be.uhasselt.imob.feathers2.services.impl.networkService.Network#nDegrees}-1 or more elements. */
   public int[] inDegreeFreq();

   // -----------------------------------------------------------------------
   /** Frequency table for nodes outDegree. Array size is {@link be.uhasselt.imob.feathers2.services.impl.networkService.Network#nDegrees} . The last element counts nodes having {@link be.uhasselt.imob.feathers2.services.impl.networkService.Network#nDegrees}-1 or more elements. */
   public int[] outDegreeFreq();

   // -----------------------------------------------------------------------
   /** Valid network indicator. Instead of stopping as soon as an error has been detected, the software continues and delivers an result that is an attempt to build a network. This structrure is unusable for production runs but allows for statistics to be printed (which can help debugging the data). Consult the log for detailed messages explaining the errors. */
   public boolean validNetwork();

   // -----------------------------------------------------------------------
   /** Request a token to access the network using an algorithm that registers state data (<code>AESAS</code>).
       @param aesasMgr <code>AESAS</code> manager
       @param clientId client identifier
       @precond. <code>aesasMgr != null</code>
       @postcond. <code>(returnValue != null) ==&gt;((forall l in network().links() | l.aesas() != null) and (forall n in network().nodes() | n.aesas() != null))</code>
       @return null iff token was not granted because of maximum number of tokens already issued
   */
   INwToken requestToken(IAesasMgr aesasMgr, String clientId);
   
   // -----------------------------------------------------------------------
   /** Release a token.
       @param token to be released
       @precond. <code>token != null</code>
       @throws F2Exception iff token was not granted
   */
   void releaseToken(INwToken token) throws F2Exception;
   
   // -----------------------------------------------------------------------
   /** Returns maximum number of simulteneously valid {@link be.uhasselt.imob.feathers2.core.network.INwToken INwToken} that will be emitted.
   */
   int maxTokensToEmit();
   
   // -----------------------------------------------------------------------
   /** Returns number of tokens currently in use.
   */
   int nTokensCurrentlyInUse();
   
   // -----------------------------------------------------------------------
   /** Find a link defined by an ordered pair of nodes (if any).
       @param orig origin node
       @param dest destination node
       @precond. <code>(orig != null) and (dest != null)</code>
       @return null iff the specified directed link <code>(orig,dest)</code> does not exist.
   */
   INetworkLink link (INetworkNode orig, INetworkNode dest);
   
   // -----------------------------------------------------------------------
   /** Find a link defined by an ordered pair of nodes (if any).
       @param orig origin node
       @param dest destination node
       @precond. <code>(orig != null) and (dest != null)</code>
       @return null iff the specified directed link <code>(orig,dest)</code> does not exist.
   */
   INetworkLink link (int orig, int dest);
   
   // -----------------------------------------------------------------------
   /** Get an AESAS manager. */
   IAesasMgr aesasMgr(Class aesasLinkAttrib, Class aesasNodeAttrib, boolean keep);

}

