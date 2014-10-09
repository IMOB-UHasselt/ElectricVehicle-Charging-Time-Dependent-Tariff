/**
@author IMOB/Luk Knapen
@version $Id: package-info.java 964 2013-11-09 22:29:59Z LukKnapen $

Gateway to execute macros by means of the TransCAD AutomationServer facility.
<ol>
   <li>All calls to execute TransCAD macros shall be executed via this package.</li>
   <li>The interface explicitly lists all facilities instead of supplying a generic mechanism where a macro name and arbitrary parameter list is used. This is done in order to keep all design decisions about TransCAD macros usage isolated within a single package. When a generic interface is provided, all kinds design decisions related to TransCAD macros would spread out (again) all over the application code. Those design decisions can be simple things that require well-thought organisational decisions like files location, naming, macro parameters, ... </li>
</ol>
*/
package be.uhasselt.imob.feathers2.services.transcadService;
