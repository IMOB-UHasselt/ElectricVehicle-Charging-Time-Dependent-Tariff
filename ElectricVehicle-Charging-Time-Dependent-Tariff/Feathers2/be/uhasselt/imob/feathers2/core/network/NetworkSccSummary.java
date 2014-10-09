package be.uhasselt.imob.feathers2.core.network;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;

/**
   INetworkSccSummary contains results about Strongly Connected Components determination.
   @author IMOB/lk
   @version $Id: NetworkSccSummary.java 140 2011-06-07 13:10:41Z LukKnapen $
*/
public class NetworkSccSummary
{
   // ----------------------------------------------------------------------
   /** The network this summary holds for. */
   INetwork network;

   /** Condensed DAG edges. */
   private HashSet<SccDagEdge> condensedDagEdges = new HashSet<SccDagEdge>();

   /** Links bridging between SCC. */
   private LinkedList<INetworkLink> bridges = new LinkedList<INetworkLink>();

   /** Last used SCC number, numbering starts at zero. */
   private int lastSCCnr = -1;

   /** SCC sizes. */
   private int[] sccSizes = null;

   // ----------------------------------------------------------------------
   public NetworkSccSummary (INetwork network)
   {
      this.network = network;
   }

   // ----------------------------------------------------------------------
   /** Set last used SCC number. */
   public void set_lastSCCnr(int lastUsedNr)
   {
      this.lastSCCnr= lastUsedNr;
   }

   // ----------------------------------------------------------------------
   public void addCondensedDagEdge(SccDagEdge edge)
   {
      condensedDagEdges.add(edge);
   }

   // ----------------------------------------------------------------------
   /** Indicates whether or the directed acyclic graph (DAG) contains the specified edge. */
   public boolean condensedDagContainsEdge(SccDagEdge edge)
   {
      return condensedDagEdges.contains(edge);
   }

   // ----------------------------------------------------------------------
   public void addBridge(INetworkLink link)
   {
      bridges.add(link);
   }

   // ----------------------------------------------------------------------
   public void setSCCsizes(int[] sccSizes)
   {
      this.sccSizes = sccSizes;
   }

   // ----------------------------------------------------------------------
   public void toText(PrintStream ps, boolean showDetails)
   {
      ps.println("#SCC=["+lastSCCnr+"] (number of Strongly Connected Components)");
      ps.println("#condensedDagEdges=["+condensedDagEdges.size()+"] (number of edges in condensed DAG (Directed Acyclic Graph))");
      if (showDetails)
      {
         ps.println("SCC sizes (an SCC is a condensed DAG node)");
         for (int i = 0; i < sccSizes.length; i++)
         {
            ps.println("   SCC_nr=["+(i)+"] nNodes=["+sccSizes[i]+"]");
         }
         ps.println("Edges in condensed DAG (numbers are SCC identifiers)");
         for (SccDagEdge cde : condensedDagEdges)
         {
            ps.println("   from=["+cde.scc0+"] to=["+cde.scc1+"]");
         }
         ps.println("Links bridging SCC boundaries (numbers refer to original network identifiers)");
         for (INetworkLink link : bridges)
         {
            ps.println("   "+link.toStringSummary());
         }
      }
   }
}

