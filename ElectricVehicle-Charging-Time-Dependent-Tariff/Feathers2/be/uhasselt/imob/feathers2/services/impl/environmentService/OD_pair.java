package be.uhasselt.imob.feathers2.services.impl.environmentService;

/**
   OD pair helper class.
   @author IMOB/Luk Knapen
   @version $Id: OD_pair.java 504 2012-11-28 01:23:19Z sboshort $
*/
public class OD_pair
{
   public OD_pair(int orig, int dest)
   {
      origin = orig;
      destination = dest;
      uid = OD_pair.uid(orig,dest);
   }

   private int origin, destination;
   private String uid; // used to maintain Hashtable

   public int origin () { return origin; }
   public int destination () { return destination; }
   public String uid() { return uid; }
   public static String uid(int origin, int destination)
   {
      return ("OD"+origin+":"+destination).intern();
   }
}
