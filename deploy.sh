#!/bin/bash

TOMCAT_PATH="/opt/tomcat"

echo "Construction de demo-app..."
if ! mvn -f pom.xml clean package; then
    echo "Échec de compilation de demo-app; arrêt du déploiement."
    exit 1
fi

WAR_FILE=$(find target -maxdepth 2 -type f -name "*.war" | head -n 1)
if [ -n "$WAR_FILE" ]; then
    echo "Copie du WAR demo-app vers Tomcat webapps..."
    cp "$WAR_FILE" "$TOMCAT_PATH/webapps/"
    echo "WAR deployé : $WAR_FILE"
else
    echo "Aucun WAR demo-app trouvé."
fi
