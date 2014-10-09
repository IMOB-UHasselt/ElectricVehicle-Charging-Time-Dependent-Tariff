package be.uhasselt.imob.feathers2.core;

public class Constants
{
   /** Number of seconds in a day. */
   public static final int nSecInDay = 86400;

   /** Moments in time : <em>never </em>(represented by big bang moment) */
   public static final long TS_NEVER = 0L;

   /** Complaint used in exceptions */
   public static final String DATA_ERROR = "Data error : ";
   /** Complaint used in assertions : invariant or constraint violated */
   public static final String SOFTWARE_ERROR = "Software error : ";
   /** Complaint used in assertions : stated precondition violated */
   public static final String PRECOND_VIOLATION = "Precondition violation : ";
   /** Complaint used in assertions : stated postcondition violated */
   public static final String POSTCOND_VIOLATION = "Postcondition violation : ";
   /** Complaint used in assertions : some type of overflow occurred */
   public static final String OVERFLOW = "Overflow : ";

   /** Execution state for an action (activity and travel). This state applies to every activity type execution and to every tripComponent traveling. */
   enum ExecutionState
   {
      /** planned to be executed */
      scheduled,
      /** being executed */
      ongoing,
      /** execution terminated */
      finished,
      /** no longer planned for execution, not being executed, not terminated : action will never be executed */
      canceled
   }
}

