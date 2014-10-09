package be.uhasselt.imob.feathers2.core.area;

import java.util.HashMap;

/**
   Areas constitute a forest (set of tree) structure.
   <ol>
      <li>Every node in a tree implements this interface.</li>
      <li><code>INominalAreaImmutableData</code> are kept at each level in the tree(s).</li>
      <li><code>ISummableAreaImmutableData</code> are kept at leaf nodes only : for other nodes <code>ISummableAreaImmutableData</code> are aggregated over their children.</li>
      <li>This interface specifies an abstraction (generalisation) of the <em>SUPERZONE, ZONE, SUBZONE</em> hierarchy in <code>Albatross</code>. The generalisation makes is feasible to read the hierarchy levels specification from a config file. The number of levels is not to be hardcoded.</li>
      <li>The (sole) purpose of the area concept is <em>aggregation</em>.</li>
   </ol>
   @author IMOB/lk
   @version $Id: IArea.java 127 2011-06-03 14:29:35Z LukKnapen $
*/
public interface IArea
{
   /** Variable having this value does not designate a valid <code>IArea</code>. */
   public static final int NO_AREA = -1;

   // ----------------------------------------------------------------------
   /** Unique identifier. Besides uniqueness, no other cnstraints apply. */
   int ident();

   // ----------------------------------------------------------------------
   // -- Tree structuring methods ------------------------------------------
   // ----------------------------------------------------------------------
   /** Parent area. Areas are organized in a forest (set of tree) structure. There are one or more areas that have no children or no parent (because of forest concept). */
   IArea parent();

   /** Register child area.
      @param area the child
      @return false iff child already registered
      @precond. <code>area != null</code>
      @postcond. <code>(area in PRE({@link #children()}) &lt;==&gt; returnValue == false) and (area in {@link #children()})</code>
   */
   boolean registerChild(IArea area);

   /** Level in area hierarchy.
      @return Zero based level (usable as index in {@link be.uhasselt.imob.feathers2.core.network.INetwork#areaLevels()}
   */
   int level();

   /** Indicates whether or not the area is leaf node (has no children).
      This method has been defined for convenience and to express pre- and post-conditions.
      @postcond. <code>isLeafArea() &lt;==&gt; (children() == null)</code>
   */
   public boolean isLeafArea();

   // ----------------------------------------------------------------------
   // -- Nominal immutable data related methods ----------------------------
   // ----------------------------------------------------------------------
   /** Nominal data attributed to this specific area level. */
   INominalAreaImmutableData nominalData();

   /** Register nominalData.
      @param nasd data to be registered.
      @precond. <code>nihil<code>
   */
   public void registerNominalData(INominalAreaImmutableData nasd);

   /** Child areas.
      @postcond. <code>foreach c in this.children() : c.parent() == this</code>
   */
   HashMap<Integer,IArea> children();
   
   // ----------------------------------------------------------------------
   // -- Summable data methods ---------------------------------------------
   // ----------------------------------------------------------------------
   /** Register summableImmutableData.
      @param sasd data to be registered.
      @precond. <code><code>
   */
   public void registerSummableData(ISummableAreaImmutableData sasd);

   /** Aggregate (sum) area specific data : this comes to adding
   <ol>
      <li>either non-aggregated data contained in the Area (in case the Area is a leaf node) in the area tree structure</li>
      <li>or aggregated data summed over all child nodes (in case the Area is not a leaf node)</li>
   </ol>
   Actual data thus always are contained in the leaf nodes.
   @param summableAreaImmutableData into which sums are to be stored
   @precond. <code>summableAreaImmutableData != null</code>
   */
    void aggregate (ISummableAreaImmutableData summableAreaImmutableData);

}
