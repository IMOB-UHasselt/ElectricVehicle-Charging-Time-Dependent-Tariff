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
	
	for root, dirs, files in os.walk("Data/evcwidrs/generated/battSocVisual"):
		for name in files:
			if name.endswith(".plt"):
				bashCommandPlot = "gnuplot " + root + "/" + name
				processPlot= subprocess.Popen(bashCommandPlot, shell=True)
				outputPlot = processPlot.communicate()[0]
	
	
else:
	print ("Usage: python plot.py")
