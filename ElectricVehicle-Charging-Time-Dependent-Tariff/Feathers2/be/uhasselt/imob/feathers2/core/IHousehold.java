package be.uhasselt.imob.feathers2.core;
/**
   Household Composition.
   <ol>
      <li><em>Household Composition</em> is implemented as an enumerated type.</li>
      <li>New types of households that are relevant for the transport demand problem, are expected to rarely emerge over time.</li>
      <li>TODO: Fields have been taken from <code>Feathers0</code>.i Exact meaning is to be defined :
      <ol>
         <li>Age limits : for young children ? for adults ?</li>
         <li>Is <code>NT = nPersons() - nAdults() - nChildren()</code> the number of teennagers ?</li>
      </ol>
      </li>
   </ol>
   @author IMOB/lk
   @version $Id: IHousehold.java 38 2011-02-22 17:02:08Z LukKnapen $
*/
public interface IHousehold
{
   enum HHComposition {SingleNoChildren, SingleWithChildren, SingleWithParents,
      DoubleNoChildren, DoubleWithChildren }

   /** Household composition. */
   HHComposition composition();

   /** Number of young children : TODO Taken from F0 ('Child') : what is this ? */
   int nYoungChildren();

   /** Number of adult people belonging to household. */
   int nPersons();

   /** Number of fuel powered cars available to household. */
   int nFuelCars();

   /** Number of electric cars available to household. */
   int nElecCars();

}
