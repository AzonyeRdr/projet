#!/bin/bash

TOMCAT_PATH="/opt/tomcat"

echo "Construction de demo-app..."
mvn -f pom.xml clean package || echo "Build demo-app a échoué (continuer)."

WAR_FILE=$(find target -maxdepth 2 -type f -name "*.war" | head -n 1)
if [ -n "$WAR_FILE" ]; then
    echo "Copie du WAR demo-app vers Tomcat webapps..."
    cp "$WAR_FILE" "$TOMCAT_PATH/webapps/"
    echo "WAR deployé : $WAR_FILE"
else
    echo "Aucun WAR demo-app trouvé."
fi
