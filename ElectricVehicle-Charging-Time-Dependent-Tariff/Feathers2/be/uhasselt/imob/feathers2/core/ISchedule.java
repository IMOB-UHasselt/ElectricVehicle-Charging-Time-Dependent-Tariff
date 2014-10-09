package be.uhasselt.imob.feathers2.core;
import java.util.List;

/**
   A <code>Schedule</code> is a plan for daily activities to be performed by an Agent.
   <ol>
      <li>A schedule has a <em>current state</em> that is used during traffic {@link be.uhasselt.imob.feathers2.core.IEvolver evolution}.</li>
      <li>Many of the methods defined below, apply to current state management.</li>
   </ol>
   

   @author IMOB/lk
   @version $Id: ISchedule.java 47 2011-02-24 14:16:48Z LukKnapen $
*/

public interface ISchedule
{
   /** Returns (a reference to) the list of <code>episodes</code> that constitute the schedule. TODO SHALL WE PROVIDE A EpisodeIterator IN WHICH THE REMOVE METHOD HAS BEEN DISABLED ?
       @return null iff the schedule is empty. The returned list is aimed at <em>readOnly</em> use and shall not be modified.
   */
   List<IEpisode> episodes();
   
   /** Start displacement : stop current activity, make successor episode the current one and start moving.
   @return false iff no activity is currently ongoing or there is no next episode.
   */
   boolean startDisplacement();

   /** Start activity : stop current displacement and start the activity for the current episode.
   @return false iff no displacement is currently ongoing.
   */
   boolean startActivity();

   /** 
   TODO
   */
   IEpisode currentEpisode();

   /** 
   TODO
   */
   boolean activityOnGoing();

   /** Predicate : indicates whether or not the remaining part of schedule is feasible.
       <ol>
          <li>The <em>remaining part</em> part of the schedule is the part that is still to be executed.</li>
          <li>Feasibility is decided by the agent : it depends on the agents perception of the universe. The universe is perceived a.o. via the schedule. Schedule {@link be.uhasselt.imob.feathers2.core.IEpisode episodes} refer to {@link be.uhasselt.imob.feathers2.core.IActivity activities} that in turn refer to {@link be.uhasselt.imob.feathers2.core.IActivBoundCooperation cooperator sets} </li>
          <li>Postcondition can be made stronger : TODO LK</li>
      </ol>
       @postcond. <code>(exists e in this.episodes() and exists tc in e.trips().components() | not tc.feasible()) ==&gt; not this.isFeasible()</code>
       @return true iff schedule is feasible
   */
   boolean isFeasible();

}

