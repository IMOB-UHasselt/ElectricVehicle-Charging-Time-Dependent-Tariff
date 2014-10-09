/**
 * Feathers2 sample service that can be used as a template for more useful stuff.
 * Please start from this package and help to upgrade is when required. When everyone sticks to a single style, we end up with maintainable software.
 * $Id: package-info.java 514 2012-11-30 23:19:36Z sboshort $
<h2>Implementation notes</h2>
<ol>
   <li><em>Never</em> specify any path composition in the <code>Java</code> code of any <code>package</code>.</li>
   <li>Each design decision shall be coded exactly once (path composition is a design decision).</li>
   <li>There several options to accomplish this
      <ul>
         <li>encode all path composition in a specific <code>pathComposition</code> class and specify simple names in the config files</li>
         <li>specify all path composition in the config files. Ths is the option chosen for <code>WIDRS</code>. Each path composition shall have a symbolic name in the config so that each definition occurs exactly once (kind of a normalized or canonical specification).</li>
      </ul>
   </li>
</ol>
 */
package be.uhasselt.imob.feathers2.services.impl.evcPriceMinimisingService;
