package be.uhasselt.imob.feathers2.core;

import java.util.BitSet;
import java.util.Collection;
import be.uhasselt.imob.feathers2.core.IMode.ModeType;

/**
   Transport modes.
   <ol>
      <li>There are two <em>modeType</em>s :
      <ol>
         <li><code>TCM</code> : TripComponent related mode</li>
         <li><code>TCAM</code> : (TripComponent,Agent)-tuple related mode</li>
      </ol>
      </li>
      <li>A collection of tree-structured {@link be.uhasselt.imob.feathers2.core.IMode}s is kept.</li>
      <li>Initially, the collection is empty.</li>
      <li>Each time an {@link be.uhasselt.imob.feathers2.core.IMode} is added, it gets a unique identifying number automatically. The identification numbers are allocated by this object by assigning consecutive integers starting from zero. The identification number is used as an index in <code>BitSet</code>s for efficiency, so this design decision is fairly relevant.</li>
      <li>Let <code>MNP</code> be the longest prefix shared by mode names <code>MN0</code> and <code>MN1</code>. Then <code>(MN0.parent() == MN1.parent()) and (MN0.parent().name.equals(MNP))</code>
   </ol>
   @author IMOB/lk
   @version $Id: IModes.java 52 2011-03-10 07:26:02Z LukKnapen $
*/
public interface IModes
{
   /** Create a transport mode having the specified name.
   <ol>
      <li>The specified name shall not have been assigned to mode previously declared mode.</li>
      <li>There is no requirement with respect to declaration order. As a consequence, declaring a mode can increase, decrease or leave unchanged the number of trees.</li>
   </ol>
      @precond. <code>modeForName(name) == null</code>
   */
   IMode declareMode(String name);

   /** Retrieves mode identified by name.
      @postcond. (returnValue == null) &lt;==&gt; (not exists m0 | (m0.name().equals(name) and (m0 in declaredModes())))
   */
   IMode modeForName(String name);

   /** Retrieves mode identified by number.
      @postcond. (returnValue == null) &lt;==&gt; (not exists m0 | (m0.nr().equals(nr) and (m0 in declaredModes())))
   */
   IMode modeForNumber(int nr);

   /** Returns collection of all successfully declared modes.
   */
   Collection<IMode> declaredModes();

   /** Returns collection of all top-level modes (modes that do not have a parent mode).
   */
   Collection<IMode> topLevelModes();

   /** Returns bit set representing exactly the set of modes contained in the specified collection.
   */
   BitSet modesBitSet(Collection<IMode> modes);

   /** Returns bit set representing the set of modes contained in the specified collection and their descendants (children, recursively).
   */
   BitSet modesBitSetR(Collection<IMode> modes);

   /** ModeType */
   ModeType modeType();
}
