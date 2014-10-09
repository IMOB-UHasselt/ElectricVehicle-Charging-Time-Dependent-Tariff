package be.uhasselt.imob.feathers2.services.impl.abstractService;

import be.uhasselt.imob.library.base.Constants;
import be.uhasselt.imob.library.base.LogReporter;
import be.uhasselt.imob.library.cmdParser.ArgSpec;
import be.uhasselt.imob.library.cmdParser.ArgSpec.ArgType;
import be.uhasselt.imob.library.cmdParser.CmdSpec;
import be.uhasselt.imob.library.cmdParser.ParsedArgument;
import be.uhasselt.imob.library.cmdParser.ParsedCmd;
import be.uhasselt.imob.library.cmdParser.Parser;
import be.uhasselt.imob.library.config.Config;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
   Reference implementation for abstract service.
   @author IMOB/lk
   @version $Id: CmdProvider.java 350 2012-10-10 16:29:36Z sboshort $
*/
abstract public class CmdProvider implements CommandProvider
{
   /** Logger */
   protected Logger logger = null;

   /** LogReporter */
   protected LogReporter logReporter = new LogReporter();

   /** Parser */
   protected Parser parser = null;

   /** Configuration */
   protected Config config = null;

   /** Output buffer to print to CommandInterpreter. */
   protected ByteArrayOutputStream baos = new ByteArrayOutputStream();

   /** ByteArray outputStream to print to CommandInterpreter. */
   protected PrintStream ciStream = new PrintStream(baos);

   /** Bundle name used in <em>help</em> text. */
   protected String bundleName = null;

   // =====================================================================
   /** Instantiate
      @param config where to fetch settings
      @precond. <code>(config != null) and config.valid() and sevice != null</code>
   */
   public CmdProvider(Config config)
   {
      assert (config != null) && config.valid()  : Constants.SOFTWARE_ERROR + "config == null";
      if (logger == null)
      {
         logger = Logger.getLogger(getClass().getName());
      }
      this.config = config;
      // prepare command parser for processing the arguments lists delivered
      // by the Equinox CommandInterpreter via the _*() methods
      parser = new be.uhasselt.imob.library.cmdParser.imp.Parser();
      declareCommands(parser);
   }

   // =====================================================================
   /** Factory method : create CmdSpec.
      @param name Command name
      @param function Intended effect of the command
      @precond. <code>(name != null) and (function != null)</code>
   */
   protected CmdSpec newCmdSpec(String name, String function)
   {
      assert (name != null) : Constants.SOFTWARE_ERROR + "name == null";
      assert (function != null) : Constants.SOFTWARE_ERROR + "function == null";
      return new be.uhasselt.imob.library.cmdParser.imp.CmdSpec(name,function);
   }

   // =====================================================================
   /** Factory method : create ArgSpec with value.
      @precond. <code>(name != null) and (function != null) argType != null</code>
   */
   protected ArgSpec newArgSpec(String name, boolean optional, String function, ArgSpec.ArgType argType)
   {
      assert (name != null) : Constants.SOFTWARE_ERROR + "name == null";
      assert (function != null) : Constants.SOFTWARE_ERROR + "function == null";
      assert (argType != null) : Constants.SOFTWARE_ERROR + "argType == null";
      return new be.uhasselt.imob.library.cmdParser.imp.ArgSpec(name,optional,function,argType);
   }

   // =====================================================================
   /** Return value for a string typed argument (if any).
      @param argName Name for argument supplied at command line.
      @param parsedCmd Successfully parsed command.
      @precond. <code>(argName != null)</code>
   */
   private String argValue(String argName, ParsedCmd parsedCmd)
   {
      assert (argName != null) : Constants.SOFTWARE_ERROR + "argName == null";
      String value = null;
      ParsedArgument arg = null;
      if (parsedCmd.arguments() != null)
      {
         arg = parsedCmd.arguments().get(argName);
      }
      if (arg != null) value = arg.valueS();
      return value;
   }

   // =====================================================================
   /** Returns fully qualified fileName (on data directory) designated by named argument.
      @param argName Name for argument that is supposed to contain a relative pathname.
      @param parsedCmd Successfully parsed command.
      @precond. <code>(argName != null)</code>
   */
   private String dataFileName (String argName, ParsedCmd parsedCmd)
   {
      assert (argName != null) : Constants.SOFTWARE_ERROR + "argName == null";

      String fileName = null;
      String value = argValue(argName,parsedCmd);
      if (value != null)
      {
         fileName = config.stringValueForName("f2:data")+value;
      }
      return fileName;
   }

   // =====================================================================
   /** Factory method : create ArgSpec without value.
      @param name Argument name.
      @param optional Indicates whether optional or mandatory.
      @param function Decription of intended effect (meaning).
      @precond. <code>(name != null) and (function != null)</code>
   */
   private ArgSpec newArgSpec(String name, boolean optional, String function)
   {
      assert (name != null) : Constants.SOFTWARE_ERROR + "name == null";
      assert (function != null) : Constants.SOFTWARE_ERROR + "function == null";
      return new be.uhasselt.imob.library.cmdParser.imp.ArgSpec(name,optional,function);
   }

   // =====================================================================
   /** Declare commands and register them. Declaration consists of :
   <ol>
      <li>command name (word)</li>
      <li>argument declarations</li>
      <ol>
         <li>argument name</li>
         <li>indicator : optional (or mandatory)</li>
         <li>value type (if any)</li>
      </ol>
      <li>function : short expected effect description</li>
   </ol>
   @param parser The parser to be configured.
   @precond. <code>parser != null</code>
   */
   protected abstract void declareCommands(Parser parser);

   // =====================================================================
   /** Get arguments from OSGi command interpreter.
      @param ci The comamnd interpreter in use.
      @precond. <code>ci != null</code>
   */

   protected String[] args(CommandInterpreter ci)
   {
      assert (ci != null) : Constants.SOFTWARE_ERROR + "ci == null";
      ArrayList<String> arguments = new ArrayList<String>();
      String argument;
      String[] args = null;

      while ((argument = ci.nextArgument()) != null)
      {
         System.err.println("Arg=["+argument+"]");
         arguments.add(argument);
      }
      if (arguments.size() > 0)
      {
         args = new String[arguments.size()];
         int i = 0;
         for (String a : arguments)
         {
            args[i++] = a;
         }
      }
      return args;
   }

   // =====================================================================
   /** Parse command, write eventual complaints and return ParsedCmd only if valid
      @param cmd command (first word on command line)
      @param ci OSGi command interpreter
      @precond. <code>(cmd != null) and (ci != null)</code>
   */
   protected ParsedCmd validParsedCmd(String cmd, CommandInterpreter ci)
   {
      assert (cmd != null) : Constants.SOFTWARE_ERROR + "cmd == null";
      assert (ci != null) : Constants.SOFTWARE_ERROR + "ci == null";
      ParsedCmd parsedCmd = parser.parseCmd(cmd,args(ci));
      // cmd shall always be a command registered in CmdSpecs;
      // parseCmd() thus shall not return null
      assert (parsedCmd != null) : Constants.SOFTWARE_ERROR + "No ParsedCmd=["+cmd+"]";
      if (!parsedCmd.valid())
      {
         for (String err : parsedCmd.errors())
         {
            ci.println(err);
         }
         parsedCmd = null;
      }
      return parsedCmd;
   }


   // =====================================================================
   /**
      Get the PrintStream associated with this command interpreter.
      @param ci Command interpreter.
      @precond. <code>ci != null</code>
   */
   protected PrintStream ciStream(CommandInterpreter ci)
   {
      assert (ci != null) : Constants.SOFTWARE_ERROR + "ci == null";
      return new PrintStream(baos);
   }

   // =====================================================================
   /**
      Close the print stream iff it is the one associated with the command interpreter.
      @param ps PrintStream to be closed
      @param ciStream PrintStream associated with command interpreter
      @param ci Command interpreter
      @precond. <code>(ci != null) and (ciStream != null)</code>
   */
   protected void closePrintStream(PrintStream ps, PrintStream ciStream, CommandInterpreter ci)
   {
      assert (ci != null) : Constants.SOFTWARE_ERROR + "ci == null";
      assert (ciStream != null) : Constants.SOFTWARE_ERROR + "ciStream == null";
      if (ps == ciStream)
      {
         // output was dumped in 'baos'
         ci.println(baos.toString());
         baos.reset();
      }
   }

   // =====================================================================
   /** Get stream for character output.
      @param name fileName for printStream
      @param optional indicates that the default can be used if no printStream can be created for specified name
      @param defaultPs to use iff optional and name == null or printSteram cannot be created
      @return printStream identified by name if it can be opened, defaultPs if optional and no printStream can be created for name : otherwise null
   */
   protected PrintStream getPrintStream(String name, boolean optional, PrintStream defaultPs)
   {
      PrintStream ps = optional ? defaultPs : null;
      if (name != null)
      {
         try
         {
            ps = new PrintStream(name);
         }
         catch (IOException ioe)
         {
            logReporter.reportThrowable(logger,"getPrintStream",ioe);
         }
         catch (SecurityException se)
         {
            logReporter.reportThrowable(logger,"getPrintStream",se);
         }
      }
      return ps;
   }

   // =====================================================================
   /** Show <em>usage</em>. Called from somehere in Equinox framework. */
   public String getHelp()
   {
      StringBuilder sb = new StringBuilder(2048);
      sb.append("---IMOB ").append(bundleName).append("---\n");
      for (String cmdName : parser.sortedCmdNames())
      {
         CmdSpec cmdSpec = parser.cmdSpecForName(cmdName);
         assert (cmdSpec != null) : Constants.SOFTWARE_ERROR;
         sb.append("\t")
         .append(cmdSpec.name())
         .append(" :: ")
         .append(cmdSpec.function())
         .append("\n");
         if (cmdSpec.argSpecs() != null)
         {
            for (ArgSpec argSpec : cmdSpec.argSpecs())
            {
               sb.append("\t\t")
               .append((argSpec.optional() ? "[" : ""))
               .append("--").append(argSpec.name())
               .append(" ").append((argSpec.requiresValue() ? argSpec.argType() : ""))
               .append((argSpec.optional() ? "]" : ""))
               .append(" :: ").append(argSpec.function())
               .append("\n");
            }
         }
      }
      return sb.toString();
   }
}
