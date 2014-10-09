#! /usr/bin/python

import subprocess
import sys

if len(sys.argv) == 1:
	bashCommandAnt = "ant -buildfile Build/build.xml"

	processAnt = subprocess.Popen(bashCommandAnt, shell=True)
	outputAnt = processAnt.communicate()[0]
else:
	print ("Usage: python antBuild.py")
