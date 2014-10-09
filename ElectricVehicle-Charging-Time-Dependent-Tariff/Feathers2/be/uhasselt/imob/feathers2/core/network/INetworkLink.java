package be.uhasselt.imob.feathers2.core.network;

/** A network link is an edge in the directed graph representing the transportation network.
   <ol>
      <li><code>float</code> is used instead of <code>double</code> because that suficces and takes less memory.</li>
      <li>Iff a link is {@link #biDirectional()} then is represents two edges in the directed graph : both share a single set of attribute values.</li>
      <li>If a link is not biDirectional, the direction is from {@link #origNode()} to {@link #destNode()}</li>
      <li>For <em>capacity</em> and <em>saturation</em> see <a href="http://www.webs1.uidaho.edu/niatt_labmanual/Chapters/signaltimingdesign/theoryandconcepts/CapacityAndSaturationFlowRate.htm">CapacityAndSaturationFlowRate</a></li>
   </ol>
*/

public interface INetworkLink extends Comparable
{
   /** Origin node on directed edge.
      @postcond. <code>returnValue != null</code>
   */
   INetworkNode origNode();

   /** Destination node on directed edge.
      @postcond. <code>returnValue != null</code>
   */
   INetworkNode destNode();

   /** Distant node (node different from the specified one). This one is useful when dealing with bidirectional links,
      @precond. <code>(origNode().id() == id) or (destNode().id() == id)</code>
      @postcond. <code>returnValue != null</code>
   */
   INetworkNode distantNode(int id);

   /** Returns link identifier. */
   int id();
   
   /** Link represents two edges in the directed graph. */
   boolean biDirectional();
 
   /** Indicates whether or not the link supports activity.
   <ol>
      <li>A link that does not support activity can be used for transit only.</li>
      <li>Note that <em>living</em> consists of <em>at-home-activities</em> and thus living (having a home address) is allowed on a link only if it supports activity.</li>
      <li>A point on a link that supports activity can be the origin or destination of a trip : this is important for map-matching GPS-traces.</li>
   </ol>
     */
   boolean supportsActivity();

   /** Length [m] */
   float length(); 

   /** Road kind (wegvaktype) */
   int roadType();

   /** Speed valid as long as capacity not reached. */
   float freeSpeed();

   /** Hourly capacity [vehicles/hour]. */
   float capacity();

   /** Saturation flow [vehicles/hour]. */
   float satFlow();

   /** Speed at capacity flow [km/h]. */
   float speedAtCap();
   
   // TODO level : shall that be a function ?
   // TODO occupation (load, #agents)
   // TODO expectedExitTime(entryTime)
   // EXPECTED_OCCUPATION (TYPE_OF_DAY, TIME_OF_DAY) : AFLEIDEN UIT VORIGE RUNS
   // HIER MOET EEN FUNCTIE INGEVOERD WORDEN DIE VOOR EEN LINK DE VERWACHTE BEZETTING OP EEN GEGEVEN TIJDSTIP GEEFT : DAT
   // WORDT GEBRUIKT DOOR DE ROUTER;
   // MEN KAN DIE IN EERSTE INSTANTIE AFHANKELIJK VAN DE ACTUELE STATUS MAKEN ALS VOLGT (EEN EXPONENTIELE TERUGKEER
   // (MET 1 OF ANDERE TIJDCONSTANTE) NAAR EXPECTED_OCCUPATION VOOR GEGEVEN TIJDSTIP)
   // TODO supportedModes : probably best a BitSet index by ITranspMode

   // -----------------------------------------------------------------------
   // -- AESAS --------------------------------------------------------------
   // -----------------------------------------------------------------------
   /** Register <code>AESAS</code> with link.
      @param token granted to the client as an access permission
      @param aesas Algorithm execution specific attribute set
   */
   void registerAesas(INwToken token, AesasLinkAttrib aesas);

   // -----------------------------------------------------------------------
   /** Remove <code>AESAS</code> from link.
      @param token granted to the client as an access permission and to which th
   */
   void clearAesas(INwToken token);

   // -----------------------------------------------------------------------
   /** Get <code>AESAS</code> registered for specified token
      @param token for which <code>AESAS</code> is requested
   */
   AesasLinkAttrib aesas(INwToken token);
   
   // -----------------------------------------------------------------------
   /** Summary string representation */
   public String toStringSummary();

}
