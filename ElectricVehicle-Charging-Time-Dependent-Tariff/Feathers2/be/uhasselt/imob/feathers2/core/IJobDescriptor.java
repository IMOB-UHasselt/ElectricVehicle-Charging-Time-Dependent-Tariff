package be.uhasselt.imob.feathers2.core;
/**
   <ol>
      <li>Work periods in this descriptor are allowed to overlap.</li>
      <li></li>
   </ol>
   @author IMOB/lk
   @version $Id: IJobDescriptor.java 51 2011-02-25 23:34:29Z LukKnapen $
*/
public interface IJobDescriptor
{
   /** Maximal fraction of workTime to be done at home : this can be interpreted as the probability of working at home. */
   double maxFractionAtHome();

   /** Minimal daily work period duration [min]. */
   double minWorkDuration();

   /** Maximal daily work period duration [min]. */
   double maxWorkDuration();

   /** Lower boundary for work start period (floating hours) : earliest start time. */
   long floatStartLow();
   
   /** Upper boundary for work start period (floating hours) : latest start time. */
   long floatStartHigh();

   /** Lower boundary for work termination period (floating hours) : earliest stop time. */
   long floatEndLow();
   
   /** Upper boundary for work termination period (floating hours) : latest stop time. */
   long floatEndHigh();

   /** Is the car used for home-work transport to be used for work-related displacements ? I.e. is the agent forced to use car mode for home-work transport ? */
   boolean needsCarDuringWorkTime();

   /** Average number of kilometers driven for work related jobs during a working day. */
   double averageDistancePerDayInService();

   /** Job location. */
   ILocation location();

   /** Job category. TODO:LK SEE ALSO be.uhasselt.imob.feathers2.core.ISummableAreaStaticData : DOES THIS CATEGORY CORRESPOND TO TEH CATEGORY USED IN THE employment INDICATOR ?*/
   String category();
}
