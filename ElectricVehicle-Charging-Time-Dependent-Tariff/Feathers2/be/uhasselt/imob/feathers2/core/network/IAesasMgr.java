package be.uhasselt.imob.feathers2.core.network;

import be.uhasselt.imob.feathers2.core.network.AesasNodeAttrib;
import be.uhasselt.imob.feathers2.core.network.AesasLinkAttrib;

/**
   An <code>AESAS</code> (<em>Algorithm execution specific attribute set</em>) manager delivers both node and link specific <code>AESAS</code>; it accepts them when they are returned after use (either to forget about (resulting in garbage collection) either for later re-use (re-issueing).
   
   @author IMOB/lk
   @version $Id: IAesasMgr.java 1316 2014-06-21 16:49:34Z LukKnapen $
*/
public interface IAesasMgr
{
   // -----------------------------------------------------------------------
   /** Returns reference to network on behalf of which the IAesasMgr operates.
   */
   INetwork network();
   
   // -----------------------------------------------------------------------
   /** Request AESAS for link.
      @return not necessarily different from null
   */
   AesasLinkAttrib getAesasForLink();
   
   // -----------------------------------------------------------------------
   /** Return ASEAS for link. */
   void returnAesasForLink(AesasLinkAttrib aesas);
   
   // -----------------------------------------------------------------------
   /** Request AESAS for node.
      @return not necessarily different from null
   */
   AesasNodeAttrib getAesasForNode();
   
   // -----------------------------------------------------------------------
   /** Return ASEAS for node. */
   void returnAesasForNode(AesasNodeAttrib aesas);
   
}

