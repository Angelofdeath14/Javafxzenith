#!/bin/bash
echo "Lancement d'Artphoria..."

# Supprimer les fichiers temporaires Java
echo "Nettoyage des fichiers temporaires..."
rm -f hs_err_* 2>/dev/null
rm -rf target/classes/META-INF 2>/dev/null

# Compiler le projet
echo "Compilation du projet..."
mvn clean compile

# Lancer l'application avec JavaFX
echo "Lancement de l'application..."
mvn javafx:run -Djavafx.mainClass=com.artphoria.Main

if [ $? -ne 0 ]; then
    echo
    echo "Une erreur s'est produite lors du lancement. Essai avec une méthode alternative..."
    echo
    # Méthode alternative
    java --module-path "${JAVA_HOME}/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web -cp target/classes:target/dependency/* com.artphoria.Main
fi

if [ $? -ne 0 ]; then
    echo
    echo "Échec du lancement. Essai avec FxMain..."
    echo
    java --module-path "${JAVA_HOME}/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web -cp target/classes:target/dependency/* test.FxMain
fi

echo "Appuyez sur Entrée pour quitter..."
read 