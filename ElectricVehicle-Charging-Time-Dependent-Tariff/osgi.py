#! /usr/bin/python

import os
import subprocess
import sys
 
if len(sys.argv) == 2 and (sys.argv[1].lower() == "windows" or sys.argv[1].lower() == "linux"):
	inputFile = open("./Config/osgiTemplate.config", "r")
	outputFile = open("./Config/osgi.config", "w")

	path = os.path.dirname(os.path.abspath(__file__))

	for line in inputFile:
		if sys.argv[1].lower() == "windows":
			line1 = line.replace("__ANCHORDIR__", path)
			line2 = line1.replace("\\", "/")
			line3 = line2.replace("C:", "")
		elif sys.argv[1].lower() == "linux":
			line3 = line.replace("__ANCHORDIR__", path)
		outputFile.write(line3)

	inputFile.close()
	outputFile.close()
	
	bashCommandStartOSGiServer = ""
	bashCommandSleep = ""
	bashCommandProcessBundels = ""
	bashCommandConnectToServer = ""
	
	if sys.argv[1].lower() == "windows":
		pathWithoutC = path.replace("C:", "")
		pathWithoutCAndBackslash = pathWithoutC.replace("\\", "/")
		 
		bashCommandStartOSGiServer = ("START \"\" java"
										" -Df2=" + path +
										" -Dosgi.console.enable.builtin=true"
										" -Dlog4j.configuration=file://" + pathWithoutCAndBackslash + "/Config/log4j.properties"
										" -DcfgDir=Config\\"
										" -DcfgSources=config.sources"
										" -enableassertions"
										" -jar Libraries\external\equinox\org.eclipse.osgi.jar"
										" -console localhost:4446")
						
		bashCommandSleep = "timeout 5"
		bashCommandProcessBundels = "type Config\osgi.config | nc -w 5 localhost 4446"
		bashCommandConnectToServer = "nc localhost 4446"
	elif sys.argv[1].lower() == "linux":
		bashCommandStartOSGiServer = ("java"
										" -Df2=" + path +
										" -Dosgi.console.enable.builtin=true"
										" -Dlog4j.configuration=file://" + path + "/Config/log4j.properties"
										" -DcfgDir=./Config/"
										" -DcfgSources=config.sources"
										" -Xmx2300M"
										" -Xss8M"
										" -enableassertions"
										" -jar ./Libraries/external/equinox/org.eclipse.osgi.jar"
										" -console localhost:4446 &")

		bashCommandSleep = "sleep 5"
		bashCommandProcessBundels = "cat ./Config/osgi.config | sed -e \"s|__ANCHORDIR__|" + path + "|\" | netcat -q 1 localhost 4446"
		bashCommandConnectToServer = "netcat -q 1 localhost 4446"
		
	processStartOSGiServer = subprocess.Popen(bashCommandStartOSGiServer, shell=True)
	outputStartOSGiServer = processStartOSGiServer.communicate()[0]

	processSleep = subprocess.Popen(bashCommandSleep, shell=True)
	outputSleep = processSleep.communicate()[0]

	processProcessBundels = subprocess.Popen(bashCommandProcessBundels, shell=True)
	outputProcessBundels = processProcessBundels.communicate()[0]

	processConnectToServer = subprocess.Popen(bashCommandConnectToServer, shell=True)
	outputConnectToServer = processConnectToServer.communicate()[0]
else:
	print ("Usage: python osgi.py (Windows|Linux)")

