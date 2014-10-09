package be.uhasselt.imob.feathers2.services.transcadService;

/**
   The TranscadService is the gateway to call TransCAD services by macro execution via the TransCAD AutomationServer.
   @author IMOB/Luk Knapen
   @version $Id: ITranscadService.java 676 2013-02-22 00:12:52Z sboshort $
 *
 */
public interface ITranscadService
{
   /** Runs TransCAD macro to determine shortest travel duration for a given NSE_period for all OD-pairs.
      <ol>
         <li>The network to use is derived from the period.</li>
         <li>It creates the </em>impedance matrix</em>.</li>
      </ol>
      @param impedMtxGenMacroName name for the TransCAD macro to calculate the impedance matrix
      @param impedMtxGenMacroDbd name for the file containing the TransCAD macro
      @param map TransCAD <code>.map</code> file containing the map corresponding to the network on which travel times are calculated (not sure why we need this one ...)
      @param taResultsRadix absolute path radix for files containing Traffic Assignment Results (link attributes <code>AB_Flow, AB_Time, AB_Speed, ...</code>. This string specifies everything except the filename extension.
      @param tazCentroidNodeId2tazIdMapping name for <code>.csv</code> file containing a table that maps Feathers TAZ (bb, subZone, ...) identifiers (1-based) to TransCAD node identifiers
      @param impedMatrixFile name for the file containing the impedanceMatrix
      @param sep separator character used in the output <code>csv</code> file (<code>affectedShortestPathFile</code>)

      @return true iff no error detected (which does not mean that there was no error ...)
      @precond. <code>impedMtxGenMacro != null</code>
      @precond. <code>map != null</code>
      @precond. <code>taResultsRadix != null</code>
      @precond. <code>tazCentroidNodeId2tazIdMapping != null</code>
      @precond. <code>impedMatrixFile != null</code>
      @precond. <code>sep != null</code>
   */
   public boolean genImpedMatrix(
      String impedMtxGenMacroName, String impedMtxGenMacroDbd,
      String map, String taResultsRadix, String tazCentroidNodeId2tazIdMapping,
      String impedMatrixFile, String sep);

   // ----------------------------------------------------------------------
   /** Runs TransCAD macro to assign traffic for a specified period.
      <ol>
         <li>The network to use is derived from the period.</li>
         <li>The OD-matrix is specified in the config file.</li>
      </ol>
      @param trafficAssignMacroName TransCAD macro name
      @param trafficAssignMacroDbd name for the <code>dbd</code> file containing the macro
      @param networkDbdFileName name for the <code>dbd</code> file containing the network to which to assign the traffic
      @param networkMapFileName name for the <code>map</code> file containing the network to which to assign the traffic
      @param networkNetFileName name for the <code>net</code> file containing the network to which to assign the traffic
      @param TTime field in network file containing the travel time
      @param Capacity field in network file containing the link capacity
      @param Alpha field in network file, <em>BPR</em> coefficient
      @param Beta field in network file, <em>BPR</em> coefficient
      @param period offset sequence number of <em>NSE_period</em> in the day
      @param AMFRGT field in network file, morning (AM) peak freight load (?)
      @param PMFRGT field in network file, evening (PM) peak freight load (?)
      @param OPFRGT field in network file, off peak freight load (?)
      @param odMatrix_mtx OD matrix to be loaded onto the network (TransCAD <code>mtx</code> file)
      @param assignedTrafficFn assigned traffic flows (loaded network, TransCAD <code>bin</code> file) (file written by this method)
      @return true iff no error detected (which does not mean that there was no error ...)
      @precond. <code>period &gt;= 0</code>
   */
   public boolean assignTraffic( String trafficAssignMacroName,
      String trafficAssignMacroDbd, String networkDbdFileName, String networkMapFileName,
      String networkNetFileName, String TTime, String Capacity, String Alpha,
      String Beta, int period, String AMFRGT, String PMFRGT, String OPFRGT,
      String odMatrix_mtx, String assignedTrafficFn);

   // ----------------------------------------------------------------------
   /** Create binary OD matrix from <code>csv</code> file for given NSE_period..
      @param odMatrixDir directory where odMatrix in <code>csv</code> file is found
      @param csvToMtxMacroName name for the TransCAD macro that converts the <code>csv</code> OD-matrix to the binary representation OD-Matrix
      @param csvToMtxMacroDbd the TransCAD <em>dbd</em> file containing the macro
      @param tazCentroidNodeId2tazIdMapping name for file containing the bijection between centroid NodeId and TazId (one-based)
      @param oldMatrixID column name in the original (<code>csv</code>) matrix table 
      @param newMatrixID column name in the new (binary) matrix table 
      @param odMatrix_InputFile the name for the <code>csv</code> file
      @param odMatrix_OutputFile the name for the binary file
      @return true iff no error detected (which does not mean that there was no error ...)
      @precond. <code>period &gt;= 0</code>
   */
   public boolean createODmatrix_MtxFromCsv(String odMatrixDir,
      String csvToMtxMacroName, String csvToMtxMacroDbd,
      String tazCentroidNodeId2tazIdMapping, String oldMatrixID, String newMatrixID,
      String odMatrix_InputFile, String odMatrix_OutputFile);

   // ----------------------------------------------------------------------
   /** Prepare <em>normal</em> and <em>disturbed</em> networks for a <em>step</em> disturber.
      A <em>step</em> disturbance is modeled by just two networks
      <ol>
         <li>the <em>normal</em> state</li>
         <li>the <em>disturbed</em> state</li>
      </ol>
      The transition between those networks is discontinuous (<code>step</code> function).
   */
   public boolean prepNetworks_StepDisturber();

   // ----------------------------------------------------------------------
   /** Report time used for TransCAD macro execution on log. */
   public void reportTCADstats();

   // ----------------------------------------------------------------------
   /** Returns the name for the <em>reference</em> (undisturbed) network.
   */
   public String refNetworkName();

   // ----------------------------------------------------------------------
   /** Cleanup managed directories.
   */
   public void cleanupManagedDirs();

   // ----------------------------------------------------------------------
   /** Cleanup. */
   public void cleanup();

}
