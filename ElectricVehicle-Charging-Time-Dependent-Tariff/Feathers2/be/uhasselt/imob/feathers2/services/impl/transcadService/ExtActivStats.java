package be.uhasselt.imob.feathers2.services.impl.transcadService;

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
 * @version $Id: ExtActivStats.java 579 2013-01-12 19:58:07Z LukKnapen $
 */
public class ExtActivStats
{
   /** Identifier */
   private String id;
   /** Decsriptive text used for reporting. */
   private String description;
   /** Pre-activation sleep delay. */
   private long preActivSleepDelay = 0;
   /** Post-activation sleep delay. */
   private long postActivSleepDelay = 0;
   /** Effective subProcess execution time. */
   private long effSubProcExeTime= 0;
   /** Number of activations */
   private long nActivations = 0;
   /** Creation time */
   private long creationTime = 0;
   /** Last update time */
   private long lastUpdateTime = 0;

   // ======================================================================
   /**
      Constructor
      @param id Identifier
      @param description descritive text used for reporting
   */
   public ExtActivStats(String id, String description)
   {
      assert id != null : "Anonymous ExtActivStats not allowed (id == null)";
      assert description != null : "description == null";
      this.id = id;
      this.description = description;
      creationTime = (new Date()).getTime();
      lastUpdateTime = creationTime;
   }

   // ======================================================================
   /**
      Add specified delay[msec] to the pre-activation sleep delay.
      @param milliSecDelay delay[msec] to add
   */
   public void accum_preActivSleepDelay(long milliSecDelay)
   {
      preActivSleepDelay += milliSecDelay;
      lastUpdateTime = (new Date()).getTime();
   }

   // ======================================================================
   /**
      Add specified delay[msec] to the post-activation sleep delay.
      @param milliSecDelay delay[msec] to add
   */
   public void accum_postActivSleepDelay(long milliSecDelay)
   {
      postActivSleepDelay += milliSecDelay;
      lastUpdateTime = (new Date()).getTime();
   }

   // ======================================================================
   /**
      Add specified delay[msec] to the effective subProcess execution time.
      @param milliSecDelay delay[msec] to add
   */
   public void accum_effSubProcExeTime(long milliSecDelay)
   {
      effSubProcExeTime += milliSecDelay;
      lastUpdateTime = (new Date()).getTime();
   }

   // ======================================================================
   /**
      Increment number of activations by one.
   */
   public void incremNumActvations()
   {
      nActivations++;
      lastUpdateTime = (new Date()).getTime();
   }

   // ======================================================================
   /**
      Return lifetime (lastUpdateTime - creationTime) in milliseconds.
   */
   public long lifetime()
   {
      return lastUpdateTime - creationTime;
   }

   // ======================================================================
   /**
   */
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("Id=[").append(id)
        .append("] Descr=[").append(description)
        .append("] AccumPreExecSleepDelay=[").append(preActivSleepDelay)
        .append("] AccumPostExecSleepDelay=[").append(postActivSleepDelay)
        .append("] AccumEffSubProcExecTime=[").append(effSubProcExeTime)
        .append("] Lifetime=[").append(lifetime())
        .append("] NumActivations=[").append(nActivations)
        .append("] (all times [msec])");
      return sb.toString();
   }
}
