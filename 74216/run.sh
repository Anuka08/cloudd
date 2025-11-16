#!/bin/bash

# Set classpath with all JAR files and bin directory including ML libraries
CLASSPATH="jar files/cloudanalyst.jar:jar files/gridsim.jar:jar files/iText-2.1.5.jar:jar files/jcommon-1.0.23.jar:jar files/jfreechart-1.0.19.jar:jar files/simjava2.jar:jar files/AbsoluteLayout.jar:jar files/weka.jar:jar files/smile-core.jar:jar files/smile-math.jar:jar files/dl4j-core.jar:jar files/nd4j-api.jar:jar files/nd4j-native-platform.jar:bin"

# Set environment variables for font support
export DISPLAY=${DISPLAY:-:0}
export JAVA_FONTS=/usr/share/fonts
export FONTCONFIG_PATH=/etc/fonts
export FC_DEBUG=0

# Create font cache directory
mkdir -p /tmp/.fontconfig

# Run the GUI application with comprehensive font configuration
echo "Starting Intrusion Detection System GUI..."
echo "================================================================"
java -Djava.awt.headless=false \
     -Djava.util.prefs.systemRoot=/tmp/.java \
     -Djava.util.prefs.userRoot=/tmp/.java/.userPrefs \
     -Dsun.java2d.xrender=false \
     -Dsun.java2d.pmoffscreen=false \
     -Djava.awt.fonts=/usr/share/fonts \
     -Duser.home=/home/runner/workspace \
     -Dawt.useSystemAAFontSettings=on \
     -Dswing.aatext=true \
     -Dsun.java2d.opengl=false \
     -Dsun.awt.disablegrab=true \
     -Dsun.font.fontmanager=sun.awt.X11FontManager \
     -Djava.awt.font.config.file=fontconfig.properties \
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
