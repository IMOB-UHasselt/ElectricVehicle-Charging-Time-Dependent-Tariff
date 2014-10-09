package be.uhasselt.imob.feathers2.services.environmentService;

import java.util.Date;

/**
 * External process activation statistics.
   Keeps 
   <ol>
      <li>pre-execution sleep time</li>
      <li>effective execution time</li>
      <li>post-execution sleep time</li>
      <li>number of activations</li>
   </ol>
   <em>Pre</em> and <em>post</em> sleep times are required in some cases to allow subprocesses to perform setup or cleanup respectively. Using those is bad practice (state transition signalling should be used instead) but unfortunately unavoidable in some cases.
 * @author IMOB/Luk Knapen
 * @version $Id: IExtActivStats.java 505 2012-11-28 21:53:52Z sboshort $
 */
public interface IExtActivStats
{
   // ======================================================================
   /**
      Add specified delay[msec] to the pre-activation sleep delay.
      @param milliSecDelay delay[msec] to add
   */
   public void accum_preActivSleepDelay(long milliSecDelay);

   // ======================================================================
   /**
      Add specified delay[msec] to the post-activation sleep delay.
      @param milliSecDelay delay[msec] to add
   */
   public void accum_postActivSleepDelay(long milliSecDelay);

   // ======================================================================
   /**
      Add specified delay[msec] to the effective subProcess execution time.
      @param milliSecDelay delay[msec] to add
   */
   public void accum_effSubProcExeTime(long milliSecDelay);

   // ======================================================================
   /**
      Increment number of activations by one.
   */
   public void incremNumActvations();

   // ======================================================================
   /**
      Return lifetime (lastUpdateTime - creationTime) in milliseconds.
   */
   public long lifetime();

   // ======================================================================
   /**
   */
   public String toString();
}
