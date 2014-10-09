package be.uhasselt.imob.feathers2.core;

import be.uhasselt.imob.feathers2.core.Constants.ExecutionState;

/**
   Component in a trip.
   <ol>
      <li>Invariants :
      <ol>
         <li><code>(this.{@link #arrivalTs()} != {@link Constants#TS_NEVER TS_NEVER}) ==&gt; (this.{@link #departureTs()} != {@link Constants#TS_NEVER TS_NEVER})</code></li>
         <li><code>(this.{@link #arrivalTs()} != {@link Constants#TS_NEVER TS_NEVER}) ==&gt; (this.{@link #departureTs()} &gt; {@link #arrivalTs()})</code></li>
      </ol>
      </li>
      <li>Note that {@link #state()} remains unchanged unless the activity belongs to en {@link be.uhasselt.imob.feathers2.core.IEvolvableAgent evolvable agent}.</li>
   </ol>
   @author IMOB/lk
   @version $Id: ITripComponent.java 47 2011-02-24 14:16:48Z LukKnapen $
*/
public interface ITripComponent
{
   /** The trip this component belongs to. */
   ITrip trip();

   /** The rank is the offset of the component in the trip.
      @postcond. <code>0 &lt; rank &lt; this.{@link #trip()}.{@link be.uhasselt.imob.feathers2.core.ITrip#components() components()}.size()</code>
   */
   int rank();

   /** State for this component during traffic evolution. */
   ExecutionState state();

   /** Transportation mode used for this component. */
   IMode mode();
   
   /** Route to be traveled on the transportation network. */
   IRoute route();

   /** Time at which traveling this component was started or is expected to start.
   <ol>
      <li>If <code>this.{@link #state()} == scheduled</code> then <code>{@link #departureTs()}</code> denotes en <em>actual</em> time of departure (ATD).</li>
      <li>If <code>this.{@link #state()} in {finished, ongoing}</code> then <code>{@link #departureTs()}</code> denotes en <em>expected</em> time of departure (ETD).</li>
   </ol>
   @postcond. <code>(this.{@link #departureTs()} == {@link Constants#TS_NEVER TS_NEVER}) &lt;==&gt; (this.state() == {@link Constants.ExecutionState#canceled canceled})</code>
   */
   long departureTs();

   /** Time at which traveling this component was stopped or is expected to stop.
   <ol>
      <li>If <code>this.{@link #state()} == scheduled</code> then <code>{@link #arrivalTs()}</code> denotes en <em>actual</em> time of arrival (ATD).</li>
      <li>If <code>this.{@link #state()} in {finished, ongoing}</code> then <code>{@link #arrivalTs()}</code> denotes en <em>expected</em> time of arrival (ETD).</li>
   </ol>
   @postcond. <code>(this.{@link #arrivalTs()} == {@link Constants#TS_NEVER TS_NEVER}) &lt;==&gt; (this.state() == {@link Constants.ExecutionState#canceled canceled})</code>
   */
   long arrivalTs();

   /** Agent/actor cooperation while executing this trip component.
      @return <code>null</code> iff the trip component is traveled by a single agent/actor independently of any other agent/actor.
   */
   ITripCompBoundCooperation cooperation();

}
