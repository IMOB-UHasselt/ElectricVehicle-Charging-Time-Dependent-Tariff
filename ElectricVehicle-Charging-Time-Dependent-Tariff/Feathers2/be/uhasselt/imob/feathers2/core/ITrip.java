package be.uhasselt.imob.feathers2.core;

import be.uhasselt.imob.feathers2.core.Constants.ExecutionState;
import java.util.List;

/** Trip models a movement from one {@link be.uhasselt.imob.feathers2.core.ILocation location} to another.
   @author IMOB/lk
   @version $Id: ITrip.java 51 2011-02-25 23:34:29Z LukKnapen $
*/
public interface ITrip
{
   enum TripPurpose
   {
      /** Sole or main purpose for the trip is moving in space (changing location). */
      utilitarian,
      /** Sole or main purpose for the trip is fun (joy). */
      touring
   }

   /** Delivers the trip's execution state.
      <ol>
         <li>A trip is {@link be.uhasselt.imob.feathers2.core.Constants.ExecutionState#ongoing ongoing} iff it has at least one tripComponent that is ongoing <code>((exists tc in {@link #components()} | tc.state() == ongoing) &lt;==&gt; this.state() == ongoing)</code>.</li>
         <li>A trip is {@link be.uhasselt.imob.feathers2.core.Constants.ExecutionState#scheduled scheduled} iff every tripComponent has state <em>scheduled</em> <code>((forall tc in {@link #components()} | tc.state() == scheduled) &lt;==&gt; this.state() == scheduled)</code>.</li>
         <li>A trip is {@link be.uhasselt.imob.feathers2.core.Constants.ExecutionState#finished finished} iff every tripComponent has state <em>scheduled</em> <code>((forall tc in {@link #components()} | tc.state() == finished) &lt;==&gt; this.state() == finished)</code>.</li>
         <li>A trip is {@link be.uhasselt.imob.feathers2.core.Constants.ExecutionState#canceled canceled} iff it has at least one tripComponent that is cancelled and all others are finished <code>(((exists tc in {@link #components()} | tc.state() == canceled) and (forall tc in {@link #components()} : (tc.state == canceled) or (tc.state() == finished))) &lt;==&gt; this.state() == canceled)</code>.</li>
      </ol>
   */
   ExecutionState state();

   /** Delivers the list of trip components in the order they are to be executed to complete the trip.
   
   <ol>
      <li>Let <code>pred(tc)</code> denote the direct predecessor of tripComponent tc in <code>components()</code>.</li>
      <li>Let <code>succ(tc)</code> denote the direct successor of tripComponent tc in <code>components()</code>.</li>
      <li>Let <code>predList(tc)</code> denote the list of all predecessors of tripComponent tc in <code>components()</code>. Thus <code>pred(pred(...pred(tc))) in predList(tc)</code></li>
      <li>Let <code>succList(tc)</code> denote the list of all successors of tripComponent tc in <code>components()</code>. Thus <code>succ(succ(...succ(tc))) in succList(tc)</code></li>
   </ol>
   @postcond. <code>(tc.{@link be.uhasselt.imob.feathers2.core.ITripComponent#state()} state() == scheduled) ==&gt; (forall tc0 in succList(tc) | tc0.state() == scheduled)</code>
   @postcond. <code>(tc.state() == ongoing) ==&gt; ((forall tc0 in succList(tc) | tc0.state() == scheduled) and (forall tc0 in predList(tc) | tc0.state() == finished))</code>
   @postcond. <code>(tc.state() == finished) ==&gt; (forall tc0 in predList(tc) | tc0.state() == finished)</code>
   @postcond. <code>(tc.state() == canceled) ==&gt; (forall tc0 in succList(tc) | tc0.state() == canceled)</code>
   */
   List <ITripComponent> components();

   /** Trip purpose */
   TripPurpose purpose();
}
