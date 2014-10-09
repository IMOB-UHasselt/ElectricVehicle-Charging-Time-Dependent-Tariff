#! /usr/bin/python

import os
import subprocess
import sys

if len(sys.argv) == 1:
	bashCommandPlot = "gnuplot Data/evcwidrs/generated/electricStatVisual/pwrStat.plt"
	processPlot= subprocess.Popen(bashCommandPlot, shell=True)
	outputPlot = processPlot.communicate()[0]
	
	bashCommandPlot = "gnuplot Data/evcwidrs/generated/electricStatVisual/pwrStat_0.plt"
	processPlot= subprocess.Popen(bashCommandPlot, shell=True)
	outputPlot = processPlot.communicate()[0]
	
	dirsCounter = 0
	dirsLength = -1
	for root, dirs, files in os.walk("Data/evcwidrs/generated/battSocVisual"):
		dirsCounter = dirsCounter + 1
		if dirsLength == -1:
			dirsLength = len(dirs)
		sys.stdout.write("\r%d%%" % (dirsCounter*100/dirsLength))
		sys.stdout.flush()
		for name in files:
			if name.endswith(".plt"):
				bashCommandPlot = "gnuplot " + root + "/" + name
				processPlot= subprocess.Popen(bashCommandPlot, shell=True)
				outputPlot = processPlot.communicate()[0]
	print ("")
	
else:
	print ("Usage: python plot.py")
