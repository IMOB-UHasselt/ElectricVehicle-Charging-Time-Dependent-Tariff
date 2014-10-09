package be.uhasselt.imob.feathers2.core;

/**
   Defines constants defined in <code>Feathers0</code> and used in services
   
 * @author IMOB/Luk Knapen
 * @version $Id: F0Constants.java 632 2013-01-24 14:50:54Z LukKnapen $
 *
 */
public interface F0Constants
{
   /** Activity type HOME */ public final int at_Home = 0;
   /** Activity tupe WORK */ public final int at_Work = 1;
   /** Activity tupe BRING_GET */ public final int at_BringGet = 3;
   /** Activity tupe DAILY_SHOPPING*/ public final int at_DailyShopping = 4;
   /** Activity tupe NON_DAILY_SHOPPING */ public final int at_NonDailyShopping = 5;
   /** Activity tupe SERVICES */ public final int at_Services = 6;
   /** Activity tupe SOCIAL_VISIT */ public final int at_SocialVisit = 7;
   /** Activity tupe LEISURE */ public final int at_Leisure = 8;
   /** Activity tupe TOURING */ public final int at_Touring = 9;
   /** Activity tupe OTHER */ public final int at_Other = 10;

   /** A schedule starts at 03:00h, so at a time offset 3*60 = 180[min]. */
   public final int FEATHERS0_TIME_OFFSET = 180;
   
}
