#! /usr/bin/python

import os
import sys

if len(sys.argv) == 2 and (sys.argv[1].lower() == "windows" or sys.argv[1].lower() == "linux"):
	os.system("python manifest.py")
	os.system("python f2Builder.py " + sys.argv[1])
else:
	print ("Usage: python install.py (Windows|Linux)")
