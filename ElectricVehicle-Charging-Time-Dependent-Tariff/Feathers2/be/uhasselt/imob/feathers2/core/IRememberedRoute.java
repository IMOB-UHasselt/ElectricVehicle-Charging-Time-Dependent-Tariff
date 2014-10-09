package be.uhasselt.imob.feathers2.core;

import  java.util.Map;

/**
   Remembered route along with some context data.
   <ol>
      <li>A <code>RememberedRoute</code> can belong to a specific Agent/Actor or to <em>collective memory</em>  (see also {@link be.uhasselt.imob.feathers2.core.IRememberedSchedule IRememberedSchedule}).</li>
      <li>Note that a {@link be.uhasselt.imob.feathers2.core.IRoute route} always is associated to a {@link be.uhasselt.imob.feathers2.core.ITripComponent TripComponent}.</li>
      <li>Context representation is rather generic because the intended use for those data is not yet completely known.</li>
      <li>Some examples listing some attributes that are important while determining a route between origin and destination. Since the attributes are relevant during route determination, they are relevant too when inspecting memory in order to find comparable routes used before.
      <ul>
         <li>vehicleType : electric-powered, fuel-powered : choice determines charging duration and range (radius of action)</li>
         <li>weather conditions</li>
         <li>{@link be.uhasselt.imob.feathers2.core.ITrip.TripPurpose trip purpose}</li>
         <li>day of week (working day, weekend)</li>
         <li>route execution (qualification) parameters (i.e. duration) : TODO SHALL WE PROVIDE A SPECIFIC ATTRIBUTE/INTERFACE/OBJECT FOR THIS ?</li>
         <li></li>
      </ul>
      </li>
   </ol>
   @author IMOB/lk
   @version $Id: IRememberedRoute.java 52 2011-03-10 07:26:02Z LukKnapen $
*/
public interface IRememberedRoute
{
   /** Remembered route.
      @postcond. <code>route() != null</code>
   */
   IRoute route();

   /** Dictionary holding data that characterizes the context for which the route was used.
      <ol>
         <li>Nothing has been specified (yet) about the context data in order not to constrain flexibility. A route <em>score</em> for instance can be kept.</li>
         <li>It is to be investigate whether or not a stronger specification is required.</li>
      </ol>
      @postcond. <code>context() != null</code>
   */
   Map<String,String> context();

   /** Route origin location.
      @postcond. <code>origin() != null</code>
   */
   ILocation origin();

   /** Route destination location.
      @postcond. <code>destination() != null</code>
   */
   ILocation destination();

}
