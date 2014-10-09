package be.uhasselt.imob.feathers2.core.area;
/**
   Area related scalar attribute data that can be summed (aggregated).
   @author IMOB/lk
   @version $Id: ISummableAreaImmutableData.java 127 2011-06-03 14:29:35Z LukKnapen $
*/
public interface ISummableAreaImmutableData
{
   /** Number of parking lots.
      @postcond. <code>nParkingLots &gt;= 0</code>
   */
   int nParkingLots();

   /** Area [km^2].
      @postcond. <code>area &gt; 0.0</code>
   */
   double area();

   /** Employment figures : number of FTE (full time equivalent) units.
       TODO:LK ISN'T IT REQUIRED TO PROVIDE A STRUCTURE GIVING THE employment FOR A SERIES OF SPECIFIC JOB CATEGORIES ? SEE ALSO {@link be.uhasselt.imob.feathers2.core.IJobDescriptor} : SHALL THIS CATEGORIES CORRESPOND TO CATEGORIES USED IN IJobDescriptor ?
      @postcond. <code>employment  &gt;= 0.0</code>
   */
   double employment();

   // TODO:LK MORE ?

   /** Accumulate data.
      @param sasd accumulators set
      @precond. <code>sasd != null </code>
   */
   public void accumulateInto(ISummableAreaImmutableData sasd);
}
