package be.uhasselt.imob.feathers2.core.network;

/**
   A INwToken represents a NetworkService client. It allows the client to make register transient data with te network for exclusive access.
   @author IMOB/lk
   @version $Id: INwToken.java 126 2011-06-03 14:26:13Z LukKnapen $
*/
public interface INwToken
{
   // -----------------------------------------------------------------------
   /** Name registered by the requester when token was created.
   */
   String requesterId();
   
   // -----------------------------------------------------------------------
   /** Thread run by requesting client.
       @return the Thread that from which the token was requested.
   */
   Thread requesterThread();
   
   // -----------------------------------------------------------------------
   /** Get <code>AESAS</code> manager registered.
       @return <code>AESAS</code> manager
   */
   IAesasMgr aesasMgr();
   
   // -----------------------------------------------------------------------
   /** Index identifying the <code>AESAS</code> to be used.
       @return index value assigned to token.
   */
   int index();
   
}

