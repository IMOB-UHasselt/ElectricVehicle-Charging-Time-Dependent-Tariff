package be.uhasselt.imob.feathers2.core;
import java.util.Collection;

/**
   IAgentMemory contains everything a non-amnesiac agent knows about the past.
   <ol>
      <li>Agent memory </li>
   </ol>
   
   @author IMOB/lk
   @version $Id: IAgentMemory.java 38 2011-02-22 17:02:08Z LukKnapen $
*/
public interface IAgentMemory
{
   /** Collection of remembered routes (if any).
      @return <code>null iff no routes remembered already</code>
   */
   Collection<IRememberedRoute> routeMem();

   /** Collection of remembered schedules (if any).
      @return <code>null iff no routes remembered already</code>
   */
   Collection<IRememberedSchedule> scheduleMem();
}

