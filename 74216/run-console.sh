#!/bin/bash

# Set classpath with all JAR files and bin directory
CLASSPATH="jar files/cloudanalyst.jar:jar files/gridsim.jar:jar files/iText-2.1.5.jar:jar files/jcommon-1.0.23.jar:jar files/jfreechart-1.0.19.jar:jar files/simjava2.jar:jar files/AbsoluteLayout.jar:jar files/weka.jar:jar files/smile-core.jar:jar files/smile-math.jar:jar files/dl4j-core.jar:jar files/nd4j-api.jar:jar files/nd4j-native-platform.jar:bin"

# Run the console version
echo "========================================================"
echo "Cloud Intrusion Detection System - Console Mode"
echo "========================================================"
echo ""
echo "This version runs without GUI and displays results in console."
echo "Perfect for VNC/headless environments!"
echo ""

java -cp "$CLASSPATH" Code.ConsoleRunner
