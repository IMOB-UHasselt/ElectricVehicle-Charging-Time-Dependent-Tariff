package be.uhasselt.imob.feathers2.services.impl.evcPriceMinimisingService;

import java.io.IOException;

import be.uhasselt.imob.feathers2.services.evcPriceMinimisingService.CmdArgIdentifiers;
import be.uhasselt.imob.feathers2.services.evcPriceMinimisingService.IEVCPriceMinimisingService;
import be.uhasselt.imob.feathers2.services.impl.abstractService.CmdProvider;
import be.uhasselt.imob.library.cmdParser.ArgSpec;
import be.uhasselt.imob.library.cmdParser.CmdSpec;
import be.uhasselt.imob.library.cmdParser.ParsedCmd;
import be.uhasselt.imob.library.cmdParser.Parser;
import be.uhasselt.imob.library.cmdParser.ArgSpec.ArgType;
import be.uhasselt.imob.library.config.Config;
import org.apache.log4j.Logger;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
   Reference implementation for EVCPriceMinimisingCommandProvider.
   <ol>
      <li>This one serves as a template for more useful services.</li>
      <li>Note that you only need to write application specific stuff here. Generic code has been gathered in {@link be.uhasselt.imob.feathers2.services.impl.abstractService.CmdProvider}</li>
   </ol>
   @author MuhammadUsman
   @version $Id: EVCPriceMinimisingCommandProvider.java 
 */
public class EVCPriceMinimisingCommandProvider extends CmdProvider implements CmdArgIdentifiers
{




	// =====================================================================
	/** Service for which commands are provided. */
	private EVCPriceMinimisingService evcPriceMinimisingService = null;
	private EVC_VitoIntegrationService evintegSrv = null;
	private EVCAverageCostService evcAvgCost = null;
	private EVC_WIDRSCostCompService evcwidrs = null;

	// =====================================================================
	/** Instantiate
      @param config where to fetch settings
      @param evcPriceMinimisingService the service this command provider operates on
	 * @param evIntegMdl 
	 * @param evcAvgCost 
	 * @param evcwidrs 
	 */

	public EVCPriceMinimisingCommandProvider(Config config, EVCPriceMinimisingService evcPriceMinimisingService, EVC_VitoIntegrationService evIntegMdl, EVCAverageCostService evcAvgCost, EVC_WIDRSCostCompService evcwidrs) 
	{
		super(config);
		this.evcPriceMinimisingService = evcPriceMinimisingService;
		this.evintegSrv = evIntegMdl;
		this.evcAvgCost = evcAvgCost;
		this.evcwidrs = evcwidrs;
		this.logger = Logger.getLogger(getClass().getName());
		this.bundleName = "EVCPriceMinimising";
	}

	@Override
	protected void declareCommands(Parser parser) 
	{
		CmdSpec cmdSpec;
		ArgSpec argSpec;
		// _startEVC
		cmdSpec = newCmdSpec(CMD_START,FUN_START);
		parser.addCmdSpec(cmdSpec);

		// _startavgEVC
		cmdSpec = newCmdSpec(CMD_START_AVG,FUN_START_AVG);
		parser.addCmdSpec(cmdSpec);
		
		// _analyse cost
		cmdSpec = newCmdSpec(CMD_COST_ANALYSE,FUN_COST_ANALYSE);
		parser.addCmdSpec(cmdSpec);

		// _startInteg
		cmdSpec = newCmdSpec(CMD_START_INTEGRATION_MODEL,FUN_START_INTEGRATION_MODEL);
		parser.addCmdSpec(cmdSpec);
		
		// _EVCWIDRS
		cmdSpec = newCmdSpec(CMD_START_EVC_WIDRS_MODEL,FUN_START_EVC_WIDRS_MODEL);
		parser.addCmdSpec(cmdSpec);

	}


	public synchronized void _startEVC (CommandInterpreter ci)
	{
		ParsedCmd parsedCmd = validParsedCmd(CMD_START,ci);
		if (parsedCmd != null)
		{
			ci.println(this.evcPriceMinimisingService.startEVC());
		}
	}

	public synchronized void _startavgEVC (CommandInterpreter ci)
	{
		ParsedCmd parsedCmd = validParsedCmd(CMD_START_AVG,ci);
		if (parsedCmd != null)
		{
			ci.println(this.evcAvgCost.startEVCAvgCost());
		}
	}
	

	public synchronized void _analyseCost (CommandInterpreter ci)
	{
		ParsedCmd parsedCmd = validParsedCmd(CMD_COST_ANALYSE,ci);
		if (parsedCmd != null)
		{
			ci.println(this.evcAvgCost.AnalyzeCostArray());
		}
	}


	public synchronized void _startInteg (CommandInterpreter ci)
	{
		ParsedCmd parsedCmd = validParsedCmd(CMD_START,ci);
		if (parsedCmd != null)
		{
//			ci.println(this.evintegSrv.startEVIntegVit());
			ci.println(this.evintegSrv.startEVIntegVito());
		}
	}
	
	public synchronized void _EVCWIDRS (CommandInterpreter ci) throws IOException
	{
		ParsedCmd parsedCmd = validParsedCmd(CMD_START_EVC_WIDRS_MODEL,ci);
		if (parsedCmd != null)
		{
			ci.println(this.evcwidrs.startEVCWIDRS());
		}
	}
}
