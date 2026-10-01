#!/bin/bash

TOMCAT_PATH="/opt/tomcat"

echo "Construction et installation du framework dans le dépôt local..."
mvn -f . clean install

FRAMEWORK_JAR=$(find ./target -maxdepth 1 -type f -name "*.jar" \
    ! -name "*sources*" \
    ! -name "*javadoc*" \
    ! -name "original-*" | head -n 1)

if [ -n "$FRAMEWORK_JAR" ]; then
    echo "Copie du JAR du framework vers Tomcat..."
    cp "$FRAMEWORK_JAR" "$TOMCAT_PATH/lib/"
    echo "JAR deployé : $FRAMEWORK_JAR"
else
    echo "Aucun JAR framework trouvé dans ./target."
fi

# Déployer aussi le WAR de demo-app (si présent)
cd demo-app
./deploy.sh
