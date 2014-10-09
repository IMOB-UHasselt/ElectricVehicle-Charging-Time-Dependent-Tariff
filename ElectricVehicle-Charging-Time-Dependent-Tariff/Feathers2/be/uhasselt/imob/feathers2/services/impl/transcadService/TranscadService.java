package be.uhasselt.imob.feathers2.services.impl.transcadService;

import be.uhasselt.imob.feathers2.core.Constants;
import be.uhasselt.imob.feathers2.core.F2Exception;
import be.uhasselt.imob.feathers2.services.commonsService.ICommonsService;
import be.uhasselt.imob.feathers2.services.commonsService.WidrsConstants;
import be.uhasselt.imob.feathers2.services.impl.abstractService.Service;
import be.uhasselt.imob.feathers2.services.transcadService.ITranscadService;
import be.uhasselt.imob.library.base.LexicoDirCleaner;
import be.uhasselt.imob.library.config.Config;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

/**
 * Transcad service.
 * @author IMOB/Luk Knapen
 * @version $Id: TranscadService.java 1116 2014-03-18 08:51:14Z MuhammadUsman $
 * 
 <ol>
    <li>Delegates specific to <code>TransCAD</code>.</li>
    <li><code>TransCAD</code> calls are executed in a loop because TransCAD crashes every now and then (between 1% and 3% (?) of the runs crashes due to a <em>No hardware block present</em>). In simulations requiring hundreds of runs, the probability to finish a simulation loop crash-free is quite low.</li>
    <li>The success of a run is evaluated by checking that the required files are present.</li>
 </ol>
 */
public class TranscadService implements ITranscadService
{
	/** Statistics about TransCAD runs (for reporting). */
	private HashMap<String,ExtActivStats> tcadStatistics = new HashMap<String,ExtActivStats>();

	/** Reference to configurator. */
	private Config config = null;
	/** Reference to commonsService. */
	private ICommonsService commonsService = null;
	/** Config data validity status indicator */
	private boolean configError = false;

	private Logger logger = null;
	private final static DecimalFormat fmt_i02 = new DecimalFormat("00");

	private String affected_OD_pairs = null;
	private String AMFRGT = null;
	private String assignedTraffic_dir = null;
	private String assignedTraffic_fnRadix = null;
	private String automationServerExe = null;
	private String dataDir = null;
	private String odMatrixDir = null;
	private String odMatrix_fnRadix = null;
	private String transcadStartScript = null;
	private String transcadKiller = null;
	/** Convention : link type number for connector between TAZ centroid and regular network node (virtual road network link). */
	private int connectorLinkType = -1;
	/** Name for file containing bijection between TAZ identifiers (one-based) and centroid node identifiers. */
	private String tazCentroidNodeId2tazIdMapping = null;
	/** Name for TransCAD macro to prepare networks for normal and disturbed cases. */
	private String networkPrepMacroName = null;
	/** Name for file containing TransCAD macro to prepare networks for normal and disturbed cases. */
	private String networkPrepMacroDbd = null;
	/** TransCAD scripts directory. */
	private String transcadWorkDir = null;
	/** Name for macro execution log file (path relative to dataDir). */
	private String macroExecutionLog = null;

	/** Time [msec] to sleep after TransCAD start and before script execution. This value is overridden by the value read from the config file. */
	private int transcadSleepAtStart = 2000;
	/** Time [msec] to sleep after script completion and before TransCAD process destruction. This value is overridden by the value read from the config file. */
	private int transcadSleepBeforeDestroy = 2000;
	/** Time [msec] to sleep after stopping TransCAD before continuing (to allow TransCAD to cleanup). This value is overridden by the value read from the config file. */
	private int transcadSleepAfterDestroy = 2000;
	/** Reduce capacity links specification csv file name. */
	private String reducedCapacityLinksFile = null;
	/** Source network directory relative path in <code>dataDir</code>. */
	private String sourceNetworkDir = null;
	/** Source network radix anme */
	private String sourceNetworkNameRadix = null;
	/** Networks directory. */
	private String networksDir = null;
	/** Normal network radix name */
	private String normalNetworkRadix = null;
	/** Disturbed network radix name */
	private String disturbedNetworkRadix  = null;

	/** Directory cleaner */
	private LexicoDirCleaner assignedTraffic_cleaner;

	/** Directory cleaner */
	private LexicoDirCleaner matrix_cleaner;

	/** Number ODbin files to keep */
	private int nFilesToKeep_assignedTraffic;

	/** Number OD matrices to keep */
	private int nFilesToKeep_odMatrix;

	// =========================================================================
	public TranscadService(Config config, ICommonsService commonsService) throws F2Exception
	{
		assert (config != null) : be.uhasselt.imob.feathers2.core.Constants.PRECOND_VIOLATION + "config == null";
		this.config = config;
		this.commonsService = commonsService;
		if (logger == null) 
			this.logger = Logger.getLogger(getClass().getName());

		configError = false;
		loadConfig(config);
		// replace '/' by '\\' for use in TransCAD macro
		macroExecutionLog = macroExecutionLog.replaceAll("/",Matcher.quoteReplacement("\\"));
		if (configError)
		{
			throw new F2Exception("Config error(s) : consult log.");
		}
		else
		{
			initTCADstats();
			assignedTraffic_cleaner = new LexicoDirCleaner(logger,new File(assignedTraffic_dir),(assignedTraffic_fnRadix+".+"),nFilesToKeep_assignedTraffic);
			matrix_cleaner = new LexicoDirCleaner(logger,new File(odMatrixDir),(odMatrix_fnRadix+".+"),nFilesToKeep_odMatrix);
			assignedTraffic_cleaner.cleanAll();
			matrix_cleaner.cleanAll();
		}
	}

	// =========================================================================
	@Override
	public void cleanupManagedDirs()
	{
		assignedTraffic_cleaner.cleanAll();
		matrix_cleaner.cleanAll();
	}

	// =========================================================================
	private boolean loadConfig(Config config)
	{
		assignedTraffic_dir = fetchStringConfigValue("trafficAssignment:assignedTraffic_dir");
		assignedTraffic_fnRadix = fetchStringConfigValue("trafficAssignment:assignedTraffic_fnRadix");
		automationServerExe = fetchStringConfigValue("transcad:AutomationServerExe");
		connectorLinkType = fetchIntConfigValue("transcad:connectorLinkType");
		dataDir = fetchStringConfigValue("rescheduler:dataDir");
		disturbedNetworkRadix = fetchStringConfigValue("transcad:disturbedNetwork");
		macroExecutionLog = fetchStringConfigValue("transcad:macroExecutionLog");
		networkPrepMacroDbd = fetchStringConfigValue("transcad:networkPrepMacroDbd");
		networkPrepMacroName = fetchStringConfigValue("transcad:networkPrepMacroName");
		networksDir = fetchStringConfigValue("transcad:networksDir");
		nFilesToKeep_assignedTraffic = fetchIntConfigValue("trafficAssignment:assignedTraffic_nFilesToKeep");
		nFilesToKeep_odMatrix = fetchIntConfigValue("trafficAssignment:odMatrix_nFilesToKeep");
		normalNetworkRadix = fetchStringConfigValue("transcad:normalNetwork");
		odMatrixDir = fetchStringConfigValue("trafficAssignment:odMatrixDir");
		odMatrix_fnRadix = fetchStringConfigValue("trafficAssignment:odMatrix_fnRadix");
		reducedCapacityLinksFile = fetchStringConfigValue("durationExtractor:reducedCapacityLinksFile");
		sourceNetworkDir = fetchStringConfigValue("transcad:sourceNetworkDir");
		sourceNetworkNameRadix = fetchStringConfigValue("transcad:sourceNetwork");
		tazCentroidNodeId2tazIdMapping = fetchStringConfigValue("transcad:tazCentroidNodeId2tazIdMapping");
		transcadKiller = fetchStringConfigValue("transcad:transcadKiller");
		transcadSleepAfterDestroy = fetchIntConfigValue("transcad:transcadSleepAfterDestroy");
		transcadSleepAtStart = fetchIntConfigValue("transcad:transcadSleepAtStart");
		transcadSleepBeforeDestroy = fetchIntConfigValue("transcad:transcadSleepBeforeDestroy");
		transcadStartScript = fetchStringConfigValue("transcad:transcadStartScript");
		transcadWorkDir = fetchStringConfigValue("transcad:transcadWorkDir");

		if (nFilesToKeep_assignedTraffic < 1)
		{
			logger.error("nFilesToKeep_assignedTraffic=["+nFilesToKeep_assignedTraffic+"] < 1");
			nFilesToKeep_assignedTraffic = 1;
		}
		if (nFilesToKeep_odMatrix < 1)
		{
			logger.error("nFilesToKeep_odMatrix=["+nFilesToKeep_odMatrix+"] < 1");
			nFilesToKeep_odMatrix = 1;
		}
		return !configError;
	}

	// =========================================================================
	private void sleepAndIgnoreInterruptions(int duration)
	{
		boolean done = false;
		{
			try
			{
				Thread.sleep(duration);
				done = true;
			}
			catch (InterruptedException ie) {}
		}
		while (!done);
	}

	// =========================================================================
	/** Sometimes (mostly using TransCAD6.0 on Windows7) unwanted tcw.exe processes continue to
       exist and prevent (license limitations) to start the required new one.
       This method is a hard TransCAD killer.
	 */
	private void kill_my_TCAD_processes()
	{
		// forcibly kill all 'tcw.exe' processes owned by the calling user.
		try
		{
			logger.debug("Executing TransCADKiller=["+transcadKiller+"]");
			Runtime.getRuntime().exec(transcadKiller).waitFor();
			logger.debug("Executed TransCADKiller=["+transcadKiller+"]");
		}
		catch (Exception e)
		{
			logger.error("Failed to exec=["+transcadKiller+"] exception=["+e.toString()+"]");
		}
	}

	// =========================================================================
	/** Execute TransCAD script.
	 */
	private void execTcadScript(String tcadStatsId,String cmd)
	{
		try
		{
			long tBefore;
			long tAfter;
			ExtActivStats eas = tcadStatistics.get(tcadStatsId);
			// start TransCAD process detached, give it some time to setup
			Process transCad = Runtime.getRuntime().exec(transcadStartScript);
			eas.accum_preActivSleepDelay(transcadSleepAtStart);
			sleepAndIgnoreInterruptions(transcadSleepAtStart);
			// Prepare command : swap slashes
			cmd = cmd.replace('/',File.separatorChar);
			String msg ="execTcadScript cmd=["+cmd+"]";
			System.out.println(msg);
			logger.debug(msg);
			// Execute command
			tBefore = (new Date()).getTime();
			Process automationServer = Runtime.getRuntime().exec(cmd);
			boolean automationServerRunning = true;
			do
			{
				try { automationServer.waitFor(); automationServerRunning = false; } catch (InterruptedException ie) {}
			}
			while (automationServerRunning);
			tAfter = (new Date()).getTime();
			eas.accum_effSubProcExeTime(tAfter - tBefore);
			// Stop TransCAD process
			sleepAndIgnoreInterruptions(transcadSleepBeforeDestroy);
			boolean processStillLiving = true;
			do
			{
				transCad.destroy();
				sleepAndIgnoreInterruptions(transcadSleepAfterDestroy);
				eas.accum_postActivSleepDelay(transcadSleepBeforeDestroy+transcadSleepAfterDestroy);
				try { int i = transCad.exitValue(); processStillLiving = false; } catch(IllegalThreadStateException itse) {}
			}
			while (processStillLiving);
			eas.incremNumActvations();
			// if at this point some of our TransCAD processes are running, something is wrong
			kill_my_TCAD_processes();
		}
		catch(IOException ioe)
		{
			logger.error(ioe.getMessage());
			logger.error("Failed=["+cmd+"]");
		}
	}

	// =========================================================================
	/** Initialize TCAD statistics keeper. */
	private void initTCADstats()
	{
		ExtActivStats eas;
		eas = new ExtActivStats(WidrsConstants.id_subProc_impedMatrixGen,WidrsConstants.desc_subProc_impedMatrixGen);
		tcadStatistics.put(WidrsConstants.id_subProc_impedMatrixGen,eas);

		eas = new ExtActivStats(WidrsConstants.id_subProc_prepareNetworks,WidrsConstants.desc_subProc_prepareNetworks);
		tcadStatistics.put(WidrsConstants.id_subProc_prepareNetworks,eas);

		eas = new ExtActivStats(WidrsConstants.id_subProc_createMatrixFromCsv,WidrsConstants.desc_subProc_createMatrixFromCsv);
		tcadStatistics.put(WidrsConstants.id_subProc_createMatrixFromCsv,eas);

		eas = new ExtActivStats(WidrsConstants.id_subProc_createMatrixFromCsv_prd,WidrsConstants.desc_subProc_createMatrixFromCsv_prd);
		tcadStatistics.put(WidrsConstants.id_subProc_createMatrixFromCsv_prd,eas);

		eas = new ExtActivStats(WidrsConstants.id_subProc_trafficAssignment,WidrsConstants.desc_subProc_trafficAssignment);
		tcadStatistics.put(WidrsConstants.id_subProc_trafficAssignment,eas);

		eas = new ExtActivStats(WidrsConstants.id_subProc_trafficAssignment_prd,WidrsConstants.desc_subProc_trafficAssignment_prd);
		tcadStatistics.put(WidrsConstants.id_subProc_trafficAssignment_prd,eas);
	}

	// =========================================================================
	/** Report on time used in external processes */
	public void reportTCADstats()
	{
		for (ExtActivStats eas : tcadStatistics.values())
		{
			logger.info(eas.toString());
		}
	}

	// =========================================================================
	private String fetchStringConfigValue(String name)
	{
		String value = config.stringValueForName(name);
		if (value == null)
		{
			logger.error("Config error : no value for name=["+name+"]");
			configError = true;
		}
		return value;
	}

	// =========================================================================
	private int fetchIntConfigValue(String name)
	{
		int value = 0;
		try
		{
			value = config.intValueForName(name);
		}
		catch (NumberFormatException nfe)
		{
			logger.error("Config error : no value for name=["+name+"] : Exception=["+nfe.getMessage()+"]");
			configError = true;
		}
		return value;
	}

	// =========================================================================
	/**
      Check whether the specified path identifies an existing regular file with non-zero length.
      @param path the path
      @precond. <code>path != null</code>
	 */
	private boolean reqdFileIsPresent(String path)
	{
		assert (path != null) : Constants.PRECOND_VIOLATION + " path == null";
		boolean ok = false;
		try
		{
			File file = new File(path);
			if (file != null)
			{
				ok = file.exists() && file.isFile() && (file.length() > 0);
			}
			else
			{
				logger.error("Could not create File object for path=["+path+"]");
			}
		}
		catch (NullPointerException npe)
		{
			// skip : only occurs for null fileName passed to File constructor
		}
		return ok;
	}

	// =========================================================================
	/* inherit from interface */
	public boolean genImpedMatrix(
			String impedMtxGenMacroName, String impedMtxGenMacroDbd,
			String mapFile, String taResultsRadix, String subZ2nodeIdMapping,
			String impedMatrixFile, String sep)
	{

		File f = new File(impedMatrixFile);
		if(!f.getParentFile().exists()) commonsService.createParentDir(f);
		boolean ok = false;
		try
		{
			long startTime = (new Date()).getTime();
			StringBuffer sb = new StringBuffer()
			.append(automationServerExe)
			.append(" ")
			.append(impedMtxGenMacroName)
			.append(" ")
			.append(impedMtxGenMacroDbd)
			.append(" ")
			.append(macroExecutionLog)
			.append(" ")
			.append(mapFile)
			.append(" ")
			.append(taResultsRadix)
			.append(" ")
			.append(subZ2nodeIdMapping)
			.append(" ")
			.append(sep)
			.append(" ")
			.append(impedMatrixFile);
			do
			{
				execTcadScript(WidrsConstants.id_subProc_impedMatrixGen,sb.toString());
			}
			while (!reqdFileIsPresent(impedMatrixFile));
			ok = true;
			long stopTime = (new Date()).getTime();
			String msg = "genImpedMatrix(impedMatrixFile=["+impedMatrixFile+"]) runtime=["+(stopTime - startTime)+"][msec] = ["+((stopTime - startTime)/1000)+"][sec]";
			logger.info(msg);
			System.out.println(msg);
		}
		catch (Exception e)
		{
			String msg = "genImpedMatrix(impedMatrixFile=["+impedMatrixFile+"]) failed. Exception=["+e.getMessage()+"]";
			logger.error(msg);
		}
		return ok;
	}

	// =========================================================================
	/* inherit from interface */
	public boolean assignTraffic(
			String trafficAssignMacroName,
			String trafficAssignMacroDbd,
			String networkDbdFileName,
			String networkMapFileName,
			String networkNetFileName,
			String TTime,
			String Capacity,
			String Alpha,
			String Beta,
			int period,
			String AMFRGT,
			String PMFRGT,
			String OPFRGT,
			String odMatrix_mtx,
			String assignedTrafficFn)
	{
		boolean ok = false;
		System.out.println("assignTraffic(period=["+period+"])");
		int hourOfDay = commonsService.conventions().hourOfDayFromPrd(period);
		try
		{
			File file=new File(assignedTraffic_dir);
			if(!file.exists()) file.mkdir();
			StringBuffer sb = new StringBuffer()
			.append(automationServerExe)
			.append(" ")
			.append(trafficAssignMacroName)
			.append(" ")
			.append(trafficAssignMacroDbd)
			.append(" ")
			.append(macroExecutionLog)
			.append(" ")
			.append(networkMapFileName)
			.append(" ")
			.append(networkDbdFileName)
			.append(" ")
			.append(networkNetFileName)
			.append(" ")
			.append(TTime)
			.append(" ")
			.append(Capacity)
			.append(" ")
			.append(Alpha)
			.append(" ")
			.append(Beta)
			.append(" ")
			.append(hourOfDay)
			.append(" ")
			.append(AMFRGT)
			.append(" ")
			.append(PMFRGT)
			.append(" ")
			.append(OPFRGT)
			.append(" ")
			.append(odMatrix_mtx)
			.append(" ")
			.append(assignedTrafficFn);
			do
			{
				execTcadScript(WidrsConstants.id_subProc_trafficAssignment_prd,sb.toString());
			}
			while (!reqdFileIsPresent(assignedTrafficFn));
			ok = true;
		}
		catch (Exception e)
		{
			String msg = "assignTraffic(period=["+period+"]) failed. Exception=["+e.getMessage()+"]";
			logger.error(msg);
		}
		assignedTraffic_cleaner.clean();
		System.out.println(" Traffic Assignment completed for period=["+period+"].");
		return ok;
	}

	// =========================================================================
	/* inherit from interface */
	public boolean createODmatrix_MtxFromCsv(String odMatrixDir,
			String csvToMtxMacroName, String csvToMtxMacroDbd,
			String tazId2nodeId_bijection, String baseIndexFieldId, String newIndexFieldId,
			String odMatrix_InputFile, String odMatrix_OutputFile)
	{
		boolean ok = false;
		System.out.println("createODmatrix_MtxFromCsv(odMatrix_InputFile=["+odMatrix_InputFile+"] odMatrix_OutputFile=["+odMatrix_OutputFile+"] tazId2nodeId_bijection=["+tazId2nodeId_bijection+"])");
		try
		{
			File dir=new File(odMatrixDir); 
			if(!dir.exists()) dir.mkdir();

			StringBuffer sb = new StringBuffer()
			.append(automationServerExe)
			.append(" ")
			.append(csvToMtxMacroName)
			.append(" ")
			.append(csvToMtxMacroDbd)
			.append(" ")
			.append(macroExecutionLog)
			.append(" ")
			.append(tazId2nodeId_bijection)
			.append(" ")
			.append(baseIndexFieldId)
			.append(" ")
			.append(newIndexFieldId)
			.append(" ")
			.append(odMatrix_InputFile)
			.append(" ")
			.append(odMatrix_OutputFile);
			do
			{
				execTcadScript(WidrsConstants.id_subProc_createMatrixFromCsv_prd,sb.toString());
			}
			while (!reqdFileIsPresent(odMatrix_OutputFile));
			ok = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		matrix_cleaner.clean();
		System.out.println(" Create Matrix from CSV module completed.");
		return ok;
	}

	// =========================================================================
	/** Prepare <em>normal</em> and <em>disturbed</em> networks for a <em>step</em> disturber.
      A <em>step</em> disturbance is modeled by just two networks
      <ol>
         <li>the <em>normal</em> state</li>
         <li>the <em>disturbed</em> state</li>
      </ol>
      The transition between those networks is discontinuous (<code>step</code> function).
	 */
	public boolean prepNetworks_StepDisturber()
	{
		boolean ok = commonsService.cleanupDir(networksDir);
		String sourceNetworkDbdPath = sourceNetworkDir+sourceNetworkNameRadix+".dbd";
		try
		{
			StringBuffer sb = new StringBuffer()
			.append(automationServerExe)                 // automation server executable
			.append(" ")
			.append(networkPrepMacroName)                // macro name (defined in TransCAD script file)
			.append(" ")
			.append(networkPrepMacroDbd)                 // TransCAD script file
			.append(" ")
			.append(macroExecutionLog)
			.append(" ")
			.append(sourceNetworkDbdPath)                // pathname for dbd file describing network from which to derive the 'normal' and 'disturbed' cases
			.append(" ")
			.append(networksDir)                         // directory containing all network related files
			.append(" ")
			.append(normalNetworkRadix)                  // nameRadix for the 'normal' network
			.append(" ")
			.append(disturbedNetworkRadix)               // nameRadix for the 'disturbed' network
			.append(" ")
			.append(reducedCapacityLinksFile)            // bijection between TAZ identifier and centroid nodeId
			.append(" ")
			.append(tazCentroidNodeId2tazIdMapping)      // path for the csv file specifying reduced capacity links
			.append(" ")
			.append(connectorLinkType);                  // Type number for 'TAZ centroid to network node connector'
			String prepareStatement = sb.toString();
			do
			{
				execTcadScript(WidrsConstants.id_subProc_prepareNetworks,prepareStatement);
			}
			while (!(reqdFileIsPresent(networksDir+normalNetworkRadix+".dbd") &&
					reqdFileIsPresent(networksDir+normalNetworkRadix+".net") &&
					reqdFileIsPresent(networksDir+normalNetworkRadix+".map") &&
					reqdFileIsPresent(networksDir+disturbedNetworkRadix+".dbd") &&
					reqdFileIsPresent(networksDir+disturbedNetworkRadix+".net") &&
					reqdFileIsPresent(networksDir+disturbedNetworkRadix+".map")));
			ok = true;
		}
		catch (Exception e)
		{
			StringBuffer msg = new StringBuffer();
			msg.append("'networkPrep' execution failed: exception=[").append(e.getMessage()).append("]");
			logger.error(msg.toString());
			e.printStackTrace();
			System.out.println(msg.toString());
		}
		return ok;
	}

	// =========================================================================
	@Override
	public String refNetworkName()
	{
		return normalNetworkRadix;
	}

	// =========================================================================
	/* inherit from interface */
	public void cleanup()
	{
	}

}
