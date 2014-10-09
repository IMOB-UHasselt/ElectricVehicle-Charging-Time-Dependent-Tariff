package be.uhasselt.imob.feathers2.core;

import be.uhasselt.imob.feathers2.core.Constants.ExecutionState;
import java.util.Collection;

/** Human activities thought to influence traffic demand.
   <ol>
      <li>Home based activities have been split in <em>work</em> and <em>non-work</em> categories because that is assumed to be useful for modeling telework scenarios.</li>
      <li>Note that {@link #state()} remains unchanged unless the activity belongs to en {@link be.uhasselt.imob.feathers2.core.IEvolvableAgent evolvable agent}.</li>
      <li>Note that (unlike in <code>Feathers0</code>) <em>touring</em> is a {@link be.uhasselt.imob.feathers2.core.ITrip.TripPurpose trip purpose} and not an activity.
   </ol>
   @author IMOB/lk
   @version $Id: IActivity.java 51 2011-02-25 23:34:29Z LukKnapen $
*/
public interface IActivity
{
   /** Supported activity categories. */
   enum ActivityType
   {
      /** home-based activity other than work */
      act_homeBasedNonWork,
      /** home-based work activity */
      act_workHomeBased,
      /** non-home-based work activity (external) */
      act_workExternal,
      /** family/ relatives people transport */
      act_bringGet,
      /** daily shopping */
      act_shopping_daily,
      /** non-daily shopping */
      act_shopping_non_daily,
      /** visiting service providers */
      act_services,
      /** socializing */
      act_social_visits,
      /** leisure */
      act_leisure,
      /** everytyhing else not mentioned before */
      act_other
   }

   /** Location where activity takes place. */
   ILocation location();

   /** Time at which the activity was started or is expected to start.
   <ol>
      <li>If <code>this.{@link #state()} == scheduled</code> then <code>{@link #startTs()}</code> denotes en <em>actual</em> time of activity execution begin (ATB).</li>
      <li>If <code>this.{@link #state()} in {finished, ongoing}</code> then <code>{@link #startTs()}</code> denotes en <em>expected</em> time of activity execution begin (ETB).</li>
   </ol>
   @postcond. <code>(this.{@link #startTs()} == {@link Constants#TS_NEVER TS_NEVER}) &lt;==&gt; (this.state() == {@link Constants.ExecutionState#canceled canceled})</code>
   */
   long startTs();

   /** Time at which the activity was stopped or is expected to stop.
   <ol>
      <li>If <code>this.{@link #state()} == scheduled</code> then <code>{@link #stopTs()}</code> denotes en <em>actual</em> time of activity execution end (ATE).</li>
      <li>If <code>this.{@link #state()} in {finished, ongoing}</code> then <code>{@link #stopTs()}</code> denotes en <em>expected</em> time of activity execution end (ETE).</li>
   </ol>
   @postcond. <code>(this.{@link #stopTs()} == {@link Constants#TS_NEVER TS_NEVER}) &lt;==&gt; (this.state() == {@link Constants.ExecutionState#canceled canceled})</code>
   */
   long stopTs();

   /** Planned (intended) agent/actor cooperation while executing this activity.
      @return <code>null</code> iff the activity is executed by a single agent/actor independently of any other agent/actor.
   */
   IActivBoundCooperation cooperation();

   /** State for this activity during time evolution (time evolution corresponds to traffic evolution). */
   ExecutionState state();

   /** Executor : the sole actor who executes the activity.
   <ol>
      <li>It can be argued that this method is superfluous (which is conceptually true) and that each activity is executed by a <em>set of</em> actors. In the vast majority of cases however, activities are assumed to be executed by a single actor. This method is provided for convenience and resource efficiency.</li>
      <li></li>
   </ol>
   @postcond. <code>(actualExecutor() == null) &lt;==&gt; (actualCooperators() != null)</code> (exclusive OR)
   */
   IAgent actualExecutor();

   /** Cooperating executors : a set of at least two actors who execute the activity.
   <ol>
      <li>See {@link #actualExecutor()}</li>
      <li></li>
   </ol>
   @postcond. <code>(actualExecutor() == null) &lt;==&gt; (actualCooperators() != null)</code> (exclusive OR)
   @postcond. <code>(actualCooperators() != null) ==&gt; (actualCooperators().size() &gt; 1)</code>
   @postcond. <code>(actualCooperators() != null) ==&gt; (actualCooperators() subsetOf({@link #cooperation()}.{@link be.uhasselt.imob.feathers2.core.IActivBoundCooperation#cooperators() cooperators()})</code>
   */
   Collection<IAgent> actualCooperators();
}
