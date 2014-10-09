package be.uhasselt.imob.feathers2.core;
import java.util.Collection;

/**
   A social agent has a network of <em>relatives</em> that influence schedule generation and schedule execution (if applicable).
   @author IMOB/lk
   @version $Id: ISocialAgent.java 48 2011-02-24 14:20:02Z LukKnapen $
*/
public interface ISocialAgent
{
   /** Returns list of <em>activity bound cooperations</em>
      @return null iff none defined
   */
   public Collection<IActivBoundCooperation> activBoundCooperations();

   /** Returns list of <em>trip-component bound cooperations</em>
      @return null iff none defined
   */
   public Collection<ITripCompBoundCooperation> tripCompBoundCooperations();

}
