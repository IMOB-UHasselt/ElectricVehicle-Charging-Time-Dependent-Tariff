package be.uhasselt.imob.feathers2.core;

/**
   Feathers2 Exception
   @author IMOB/lk
   @version $Id: F2Exception.java 106 2011-03-30 19:55:08Z LukKnapen $
*/
public class F2Exception extends Exception
{
   private static final long serialVersionUID = 0xf2916003;

   // -----------------------------------------------------------------------
   public F2Exception(String msg)
   {
      super(msg);
   }
   
   // -----------------------------------------------------------------------
   public F2Exception(String msg, Throwable cause)
   {
      super(msg,cause);
   }
   
   // -----------------------------------------------------------------------
   public F2Exception(Throwable cause)
   {
      super(cause);
   }
   
}
