package be.uhasselt.imob.feathers2.core;

/**
   An <code>Episode</code> is an elementary part of a Schedule.
   <ol>
      <li>An episode consists of a trip followed (in time) by an activity execution.</li>
      <li>The activity shall be significant (cannot equal <code>null</code>).</li>
      <li>The trip can equal <code>null</code> (cases where no movement is required)..</li>
   </ol>
   
   @author IMOB/lk
   @version $Id: IEpisode.java 37 2011-02-22 17:00:07Z LukKnapen $
*/
public interface IEpisode
{
   /** Returns trip required to be able to start the activity.
      @return null iff no movement required
   */
   ITrip trip();

   /** Returns the activity to perform.
      @postcond. <code>returnValue != null</code>
      @return activity
   */
   IActivity activity();
}

