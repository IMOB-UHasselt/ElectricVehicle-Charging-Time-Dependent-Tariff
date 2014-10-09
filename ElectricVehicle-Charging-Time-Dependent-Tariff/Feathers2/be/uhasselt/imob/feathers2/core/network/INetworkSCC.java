package be.uhasselt.imob.feathers2.core.network;

/**
   INetworkSCC determines the Strongly Connected Components the given network
   consists of.
   <ol>
      <li>For info on concept and on <em>Tarjan</em>'s algorithm : see
      <ol>
         <li><a href="http://en.wikipedia.org/wiki/Tarjan's_strongly_connected_components_algorithm">Tarjan's Algorithm</a></li>
         <li><a href="http://en.wikipedia.org/wiki/Strongly_connected_component">Strongly Connected Component</a></li>
         <li><a href="http://en.wikipedia.org/wiki/Connected_component_(graph_theory)">Connected Component</a></li>
      </ol>
      </li>
      <li>For transportation purposes the number of components shall be one (1) since it not every component is reachable from every other one. In undirected graphs, nodes in one component are unreachable from nodes in a different component. In digraphs, some components behave as a pitfall : they can be entered but not left.</li>
      <li>SCC form a DAG (directed acyclic graph). For debugging the graph, info about this DAG is helpfull. Therefore, this class produces
      <ol>
         <li>a list of the edges in the DAG formed by the SCC of the original network.
         <li>a list of {@link be.uhasselt.imob.feathers2.core.network.INetworkLink links} connecting {@link be.uhasselt.imob.feathers2.core.network.INetworkNode nodes} in different SCC.</li>
      </ol>
      </li>
   </ol>
   @author IMOB/lk
   @version $Id: INetworkSCC.java 1318 2014-06-22 11:27:25Z LukKnapen $
*/
public interface INetworkSCC
{
   /** Since a SCC determinator needs to register some data in the network description (using {@link be.uhasselt.imob.feathers2.core.network.AesasNodeAttrib AESAS}) it needs to be {@link #open()}ed to allocate data structures. After use, it shall be {@link #close()}d as soon as possible to free heap space.
      @precond. <code>not {@link #isOpen()}</code>
   */
   void open();

   // -----------------------------------------------------------------------
   /** Close SCC determinator to free allocated {@link be.uhasselt.imob.feathers2.core.network.AesasNodeAttrib AESAS}
      @precond. <code>{@link #isOpen()}</code>
   */
   void close();
   
   // -----------------------------------------------------------------------
   /** Indicates that {@link #open()} has been called and {@link #close()} has not been called
   */
   boolean isOpen();

   // -----------------------------------------------------------------------
   /** Register the SCC number in each node in the database the network was loaded from (if any).
      <ol>
         <li>If the network was not loaded from a DB, nothing happens.</li>
         <li>If the DB the network was loaded from, does not support SCC number registration, nothing happens.</li>
      </ol>
      @return false iff no registration attempt has been made. Do not assume the attempt <em>fully</em> suceeded when <code>true</code> is returned.
      @precond. <code>{@link #isOpen()}</code>
   */
   boolean registerSCCinDB();
   
   // -----------------------------------------------------------------------
   /** Get summary results.
      @precond. <code>{@link #isOpen()}</code>
   */
   NetworkSccSummary summary();

}

