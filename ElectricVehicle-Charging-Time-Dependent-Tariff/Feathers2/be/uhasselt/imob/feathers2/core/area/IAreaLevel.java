package be.uhasselt.imob.feathers2.core.area;

/**
   AreaLevels constitute a totally ordered list.
   <ol>
      <li><code>IArea</code>s are organised in a tree structure. Each level in the tree corresponds with an <code>IAreaLevel</code>.</li>
      <li><code>IAreaLevel</code>s are numbered with consecutive positive integers.</li>
      <li>Numbering is zero-based. The <em>root</em> level has rank zero.</li>
      <li>There is no constraint on level names (but is seems at least to be wise to assign unique names).</li>
   </ol>
   @author IMOB/lk
   @version $Id: IAreaLevel.java 127 2011-06-03 14:29:35Z LukKnapen $
*/
public interface IAreaLevel
{
   /** Level name. */
   String name();

   /** Level zero-based rank (number). */
   int rank();
}
