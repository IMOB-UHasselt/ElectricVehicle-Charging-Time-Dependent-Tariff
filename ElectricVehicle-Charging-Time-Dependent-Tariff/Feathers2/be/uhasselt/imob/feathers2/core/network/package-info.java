/**
 * Core interfaces for Feathers2 project.
 * <br>
 * $Id: package-info.java 131 2011-06-03 23:43:25Z LukKnapen $
 * <br>
<h2>Notes and Hints</h2>
<h2>Design Decisions</h2>
   <h3>Network Structure</h4>
   <ol>
      <li>An hierarchical structure of areas is provided. This structure is not hardcoded but loaded from a config file. The number of levels is not predefined. See {@link be.uhasselt.imob.feathers2.core.area.IArea}.</li>
      <li>Each {@link be.uhasselt.imob.feathers2.core.network.INetworkNode node} belongs to exactly one area of the lowest level defined in the configuration. This concept is compatible with <code>Feathers0</code>.</li>
      <li>A {@link be.uhasselt.imob.feathers2.core.network.INetworkLink link} can join two nodes belonging to different areas. During aggregation one shall take care of this phenomenon.</li>
   </ol>
   <h3><code>AESAS</code> : <u><em>A</em></u>lgorithm <u><em>E</em></u>xecution <u><em>S</em></u>pecific <u><em>A</em></u>ttribute <u><em>S</em></u>et for network links and nodes</h4>
   <ol>
      <li>Many graph algorithms (e.g. shortest path, connectivity check, ...) require execution state descripting data to be associated with (stored in) nodes and links.</li>
      <li>It is not known a priori what data will be required by algorithms that have not yet been identified. Furthermore, when more than one client executes such algorithms, they shall not interfere.</li>
      <li>Therefore, state data will be kept in objects managed by a <code>AESAS</code> manager ({@link be.uhasselt.imob.feathers2.core.network.IAesasMgr}) specified by the calling client/algorithm. Each client thus can provide a specific factory. The <code>AESAS</code> objects (see {@link be.uhasselt.imob.feathers2.core.network.AesasLinkAttrib} and {@link be.uhasselt.imob.feathers2.core.network.AesasNodeAttrib}) are registered in array elements in <em>link</em> and <em>node</em> objects respectively. Thereto, a client needs to request a {@link be.uhasselt.imob.feathers2.core.network.INwToken} : that gives access to a specific <code>AESAS</code> registered in nodes and links.</li>
      <li>It has been decided to integrate the <code>AESAS</code> concept in the Feathers2 core because it allows for arbitrary functions on the {@link be.uhasselt.imob.feathers2.core.network.INetworkLink link} and {@link be.uhasselt.imob.feathers2.core.network.INetworkNode node} domains to be defined.</li>
   </ol>
<h2>More info</h2>
<ol>
   <li>Definitions for the concepts used can be found in the <a href="http://wiki.imob/uhasselt.be/mediawiki/"> IMOB wiki</a></li>.
</ol>
 */
package be.uhasselt.imob.feathers2.core.network;
