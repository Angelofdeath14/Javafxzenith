#!/bin/bash
echo "Lancement direct d'Artphoria (mode alternatif)..."

# Nettoyage des fichiers d'erreur
rm -f hs_err_pid*.log 2>/dev/null

# Télécharger les dépendances si nécessaire
mvn dependency:copy-dependencies -DoutputDirectory=lib -DincludeScope=runtime

# Compiler le projet
mvn clean compile

# Configurer les options Java pour éviter les problèmes de thread UI
JAVA_OPTS="-Dprism.order=sw -Dglass.win.uiScale=100% -Djavafx.animation.fullspeed=false -Djavafx.preloader=false -Djavafx.concurrent.daemon=true"

# Lancer directement avec java (contourne Maven qui peut causer des problèmes)
echo "Lancement avec la méthode directe..."
java $JAVA_OPTS \
  --module-path "lib" \
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web,javafx.base \
  --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
  --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED \
  -cp "target/classes:lib/*" \
  com.artphoria.Main

if [ $? -ne 0 ]; then
  echo "Premier lancement échoué, tentative avec mode de compatibilité..."
  java $JAVA_OPTS \
    --module-path "lib" \
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web,javafx.base \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
    --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED \
    --add-opens javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED \
    -Djava.awt.headless=false \
    -cp "target/classes:lib/*" \
    test.FxMain
fi

echo "Appuyez sur Entrée pour quitter..."
read 