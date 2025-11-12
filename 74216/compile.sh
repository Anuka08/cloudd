#!/bin/bash

# Create bin directory if not exists
mkdir -p bin

# Set classpath with all JAR files including ML libraries
CLASSPATH="jar files/cloudanalyst.jar:jar files/gridsim.jar:jar files/iText-2.1.5.jar:jar files/jcommon-1.0.23.jar:jar files/jfreechart-1.0.19.jar:jar files/simjava2.jar:jar files/AbsoluteLayout.jar:jar files/weka.jar:jar files/smile-core.jar:jar files/smile-math.jar:jar files/dl4j-core.jar:jar files/nd4j-api.jar:jar files/nd4j-native-platform.jar:bin"

# Compile all Java files
echo "Compiling Java files..."
find src -name "*.java" -print0 | xargs -0 javac -cp "$CLASSPATH" -d bin -Xlint:deprecation -source 8 -target 8

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
    exit 1
fi
