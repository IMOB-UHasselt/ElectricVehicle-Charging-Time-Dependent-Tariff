package be.uhasselt.imob.feathers2.core;

import  java.util.Map;

/**
   Schedule remembered by an agent/actor along with some context data.
   <ol>
      <li>A remembered schedule always is associated with a specific Agent/Actor (see also {@link be.uhasselt.imob.feathers2.core.IRememberedRoute IRememberedRoute}).</li>
   </ol>
   @author IMOB/lk
   @version $Id: IRememberedSchedule.java 52 2011-03-10 07:26:02Z LukKnapen $
*/
public interface IRememberedSchedule
{
   /** Remembered schedule
      @postcond. <code>route() != null</code>
   */
   ISchedule schedule();

   /** Dictionary holding data that characterizes the context for which the schedule was used.
      <ol>
         <li>Nothing has been specified (yet) about the context data in order not to constrain flexibility. A schedule <em>score</em> for instance can be kept.</li>
         <li>It is to be investigate whether or not a stronger specification is required.</li>
      </ol>
      @postcond. <code>context() != null</code>
   */
   Map<String,String> context();

}
