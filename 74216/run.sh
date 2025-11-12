#!/bin/bash

# Set classpath with all JAR files and bin directory including ML libraries
CLASSPATH="jar files/cloudanalyst.jar:jar files/gridsim.jar:jar files/iText-2.1.5.jar:jar files/jcommon-1.0.23.jar:jar files/jfreechart-1.0.19.jar:jar files/simjava2.jar:jar files/AbsoluteLayout.jar:jar files/weka.jar:jar files/smile-core.jar:jar files/smile-math.jar:jar files/dl4j-core.jar:jar files/nd4j-api.jar:jar files/nd4j-native-platform.jar:bin"

# Set environment variables
export DISPLAY=${DISPLAY:-:0}
export JAVA_FONTS=/usr/share/fonts

# Run the GUI application - Using Prefs API workaround for fontconfig issue
echo "Starting NetBeans 8.1 Simulation GUI Application..."
echo "================================================================"
java -Djava.awt.headless=false \
     -Djava.util.prefs.systemRoot=/tmp/.java \
     -Djava.util.prefs.userRoot=/tmp/.java/.userPrefs \
     -Dsun.java2d.xrender=false \
     -Djava.awt.fonts=/usr/share/fonts \
     -Duser.home=/home/runner/workspace \
     -cp "$CLASSPATH" Code.GUI 2>&1 | tee app.log

EXIT_CODE=$?
echo ""
echo "================================================================"
if [ $EXIT_CODE -eq 0 ]; then
    echo "Application completed successfully"
else
    echo "Application exited with code: $EXIT_CODE"
    echo "Check app.log for details"
fi
