package be.uhasselt.imob.feathers2.core;

import java.util.BitSet;

/**
   Transport mode specifications are read from a configuration store.
   <h3>Name structure</h3>
   <ol>
      <li>Transport mode can have specialisations and thus is the root in a (sub)tree. This decision has been taken for both clarity and efficiency.</li>
      <li>Refer to {@link be.uhasselt.imob.feathers2.core.IModes} for the constraints on modes.</li>
      <li>Mode name is used to set up mode hierarchies. A mode name has following syntax :
<pre>
   &lt;modeName&gt; ::= &lt;simpleName&gt; [ '.' &lt;modeName&gt; ]
   &lt;simpleName&gt; ::= &lt;alphanum&gt;+
   &lt;alphanum&gt; ::= {A-Za-z0-9}
</pre>
      </li>
   </ol>
   <h3>ModeType and name structure</h3>
   <ol>
      <li>A fully qualified mode identifier (FQMI) for a tree node <em>N0</em> is the concatenation of the names of all tree nodes encountered while traversing the tree from the the root to <em>N0</em>.</li>
      <li>The configuration specifies the {@link be.uhasselt.imob.feathers2.core.IMode.ModeType} for each <code>FQMI</code>. 
      <ol>
         <li>All children of node <em>N0</em> shall share the same ModeType.</li>
         <li>If a node <em>N0</em> has <code>TCAM</code> ModeType, it has at least one antecedent with nodeType <code>TCM</code> and no descendants with nodeType <code>TCM</code>.</li>
         <li>The tree root has ModeType <code>TCM</code>.</li>
      </ol>
      </li>
      <li>Name structure
      <ol>
         <li>the <em>head</em> of the <code>FQMI</code> is the concatenation of identifers for nodes having <code>TCM</code> (cannot be empty)</li>
         <li>the <em>tail</em> of the <code>FQMI</code> is the concatenation of identifers for nodes having <code>TCAM</code> (can be empty)</li>
      </ol>
      </li>
   @author IMOB/lk
   @version $Id: IMode.java 52 2011-03-10 07:26:02Z LukKnapen $
*/
public interface IMode
{
   /** Mode types */
   enum ModeType
   {
      /** TripComponent related mode */
      TCM,
      /** (TripComponent,Agent)-tuple related mode */
      TCAM
   }

   /** Parent mode in tree structure. */
   IMode parent();

   /** Mode name assigned in the configuration. */
   String name();

   /** Unique identification number (used to construct BitSets for efficiency, not to be exposed to user). */
   int nr();

   /** Indicates whether or not the mode belongs to the set represented by the specified bitSet. */
   int belongsTo(BitSet modesBitSet);

   /** Returns ModeType
      @postcond. <code>({@link #modeType() modeType()} == {@link be.uhasselt.imob.feathers2.core.IMode.ModeType#TCM TCM}) ==&gt; (({@link #parent() parent()} == null) or (parent().modeType() == TCM))</code>
      @postcond. <code>({@link #modeType() modeType()} == {@link be.uhasselt.imob.feathers2.core.IMode.ModeType#TCAM TCAM}) ==&gt; (not exists N1 | N1 in this.descendents() and N1.modeType() == TCM)</code>
   */
   ModeType modeType();

   /** Returns TCM-related part of the identifier : that is the <code>head</code> part of the <code>FQMI</code> identifier. It is the identifier for the most specific antecedent having ModeType <code>TCM</code>.
      @postcond. <code>({@link #modeType() modeType()} == {@link be.uhasselt.imob.feathers2.core.IMode.ModeType#TCM TCM}) &lt;==&gt; ({@link #tcmId() tcmId()}.equals({@link #name() name()}))</code>
   */
   String tcmId();
}
