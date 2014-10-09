/**
 * Core interfaces for Feathers2 project.
 * <br>
 * $Id: package-info.java 125 2011-06-03 14:11:58Z LukKnapen $
 * <br>
<h2>Notes and Hints</h2>
   <h3>Touring</h3>
   <ol>
      <li><em>Touring</em> is implemented as an attribute of a {@link be.uhasselt.imob.feathers2.core.ITrip trip} and not as an activity (like in Feathers0). Thereto, the {@link be.uhasselt.imob.feathers2.core.ITrip#purpose() trip purpose} has been provided.</li>
      <li>A {@link be.uhasselt.imob.feathers2.core.IRoute route} is associated to each {@link be.uhasselt.imob.feathers2.core.ITripComponent trip component}. A trip component and its associated route, (conceptually) inherit the purpose of the trip they belong to.</li>
      <li>Note that a multiMode trip can have <em>Touring</em> as its purpose.</li>
   </ol>
<h2>Design Decisions</h2>
   <h3>Perception filtering</h3>
      <h4>AgentSet Perception filtering</h4>
      <ol>
         <li>Perception filtering for the set of actors/agents has not (yet) been explicitely defined (i.e. there is no AgentPerceptionFilter object) because sets of cooperating agents are readily available {@link be.uhasselt.imob.feathers2.core.IActivBoundCooperation#cooperators()} and {@link be.uhasselt.imob.feathers2.core.ITripCompBoundCooperation#cooperators()}</li>
      </ol>
      <h4>Network Perception filtering</h4>
      <ol>
         <li>Network perception filtering has been explicitly specified.</li>
      </ol>
   <h3>Identifiers - Maps</h3>
   <ol>
      <li>In several maps integers are used as a key. In order for this to work, <code>put()</code> and <code>get()</code> operations shall use primitive <code>int</code> and not <code>Integer</code> because there is no <code>String.inter()</code>-like concept for <code>Integer</code>. Clearly, there is some internal not directly accessible mechanism.
   </ol>
<h2>More info</h2>
   <ol>
      <li>Refer to sub-packages too.</li>
      <li>Definitions for the concepts used can be found in the <a href="http://wiki.imob/uhasselt.be/mediawiki/"> IMOB wiki</a></li>.
   </ol>
 */
package be.uhasselt.imob.feathers2.core;
