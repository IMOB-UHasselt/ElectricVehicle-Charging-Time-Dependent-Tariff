package be.uhasselt.imob.feathers2.core.network;

/**
   Abstract class from which all <code>AESAS</code> attribute classes shall be derived.
   <ol>
      <li>Most of those classes will be <code>struct</code>-like classes having public fields (for efficiency and because they are algorithm specific and as a consequence probably not reusable).</li>
      <li>Refer to package-info for design decisions about <code>AESAS</code>.
   </ol>
   
   @author IMOB/lk
   @version $Id: AesasNodeAttrib.java 132 2011-06-03 23:44:31Z LukKnapen $
*/
public abstract class AesasNodeAttrib
{
   /** Initializer : is called by AesasMgr before delivering object. */
   public abstract void init();
}
