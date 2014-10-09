#! /usr/bin/python

import subprocess
import sys

if len(sys.argv) == 1:
	bashCommandManifestCSV = "jar ufm Libraries/external/csv/csvreader.jar Config/csvreader.manifest"
	bashCommandManifestLog4j = "jar ufm Libraries/external/apache/log4j.jar Config/log4j.manifest"
	bashCommandManifestGNU = "jar ufm Libraries/external/gnu/gnu-getopt.jar Config/gnu-getopt.manifest"

	processManifestCSV = subprocess.Popen(bashCommandManifestCSV, shell=True)
	outputManifestCSV = processManifestCSV.communicate()[0]

	processManifestLog4j = subprocess.Popen(bashCommandManifestLog4j, shell=True)
	outputManifestLog4j = processManifestLog4j.communicate()[0]

	processManifestGNU = subprocess.Popen(bashCommandManifestGNU, shell=True)
	outputManifestGNU = processManifestGNU.communicate()[0]
else:
	print ("Usage: python manifest.py")
