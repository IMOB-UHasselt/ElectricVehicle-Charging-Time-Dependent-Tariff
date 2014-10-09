package be.uhasselt.imob.feathers2.core;
/**
   Socio-economic class (income category).
   <ol>
      <li>Categories are not fixed a priori. Both the number of categories and the category boundaries probably depend on input data.</li>
      <li>This interface is meant for lookup only. Modification actions are implementation class specific.</li>
      <li>Constraints
      <ol>
         <li>ISocioEconClass partitions a subrange (closed interval) of {@link java.lang.Integer} delimited by {@link #lowLimit() lowLimit()} and {@link #highLimit() highLimit()}.</li>
         <li>ISocioEconClass defines an ordered set of categories (since it is defined using <code>Integer</code> that has a natural order).</li>
         <li>The low limit can equal {@link java.lang.Integer#MIN_VALUE}</li>
         <li>The high limit can equal {@link java.lang.Integer#MAX_VALUE}</li>
         <li><code>{@link #lowLimit() lowLimit()} &lt; {@link #highLimit() highLimit()}</code></li>
         <li>Each category has a unique index (zero based, consecutively numbered).</li>
         <li>Each category has a unique name.</li>
      </ol>
      </li>
   </ol>
   @author IMOB/lk
   @version $Id: ISocioEconClass.java 51 2011-02-25 23:34:29Z LukKnapen $
*/
public interface ISocioEconClass
{
   /** Number of categories (including (of course) the open categories near lower and upper limits respectively). */
   int nCategories();

   /** Index value for the category that contains the specified value.
      @postcond. <code>indexForValue() in [0, {@link #nCategories() nCategories()}]</code>
      @precond. <code>value in [{@link #lowLimit() lowLimit()},{@link #highLimit() highLimit()}]</code>
   */
   int indexForValue(int value);

   /** Array containing category boundaries (including the lower and upper limits.
   @postcond. <code>boundaries.length == {@link #nCategories() nCategories()}</code>
   */
   int[] boundaries();

   /** Returns the name for a category identified by its index.
      @postcond. <code>nameForValue != null</code>
      @postcond. <code>not (exists i, j in [0, {@link #nCategories() nCategories()}-1] | (i != j ) and (nameForValue(i).equals(nameForValue(j))))</code>
   */
   String nameForValue(int value);

   /** Returns boundaries for category identified by its index.
      @precond. <code>index in [0,  {@link #nCategories() nCategories()}-1]</code>
      @postcond. <code>boundariesForIndex.length == 2</code>
   */
   int[] boundariesForIndex(int index);

   /** Returns the lower limit for the subrange covered. */
   int lowLimit();

   /** Returns the upper limit for the subrange covered. */
   int highLimit();
}
