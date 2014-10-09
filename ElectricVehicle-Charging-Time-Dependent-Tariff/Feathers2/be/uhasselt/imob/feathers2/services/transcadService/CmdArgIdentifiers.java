package be.uhasselt.imob.feathers2.services.transcadService;

/**
   Command and argument identifiers associated with bundle services.
   <ol>
      <li>Each command and each argument name has an identifier.</li>
      <li>Identifiers are used internally in the software only (for passing data between modules).</li>
      <li>Command and argument names are used in communication with the user.</li>
      <li>Distinction between names and identifiers uncouples human user interfaces from modules intefaces (which enhances flexibility).</li>
      <li>An argument can get associated with more than one command. The implementation
      <ol>
         <li>provides argument type checking</li>
         <li>indicates whether an command-argument association is optional or mandatory</li>
         <li>indicates whether or not an argument takes a value</li>
      </ol>
      </li>
      <li>The idea is to keep the number of argument names minimal (for ease of maintenance).</li>
   </ol>

 * @author IMOB/Luk Knapen
 * @version $Id: CmdArgIdentifiers.java 578 2013-01-12 19:16:43Z LukKnapen $
 *
 */
public interface CmdArgIdentifiers
{
	// Command (names)
	final String CMD_NETWORK_FOR_PERIOD = "networkForPeriod";
	final String CMD_NETWORK_PATHNAME = "networkPathName";

	// command function descriptors
	final String FUN_NETWORK_FOR_PERIOD = "Get network for given period";
	final String FUN_NETWORK_PATHNAME = "Get full file pathname for given network and extension";

   // Argument names
   final String ARG_PERIOD         = "period";
   final String ARG_NETWORKNAME    = "network";
   final String ARG_EXTENSION      = "extension";
   
}
