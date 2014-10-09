package be.uhasselt.imob.feathers2.core.network;

/** Representation of an edge in the DAG that is constituted by the
    condensed SCC.
*/
public class SccDagEdge
{
   public int scc0, scc1;
   public boolean equals(SccDagEdge other)
   {
      return (this.scc0 == other.scc0) && (this.scc1 == other.scc1);
   }
}
