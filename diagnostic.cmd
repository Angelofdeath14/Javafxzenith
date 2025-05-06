@echo off
echo Diagnostic pour Artphoria...

echo ====================================================
echo INFORMATIONS SYSTÈME
echo ====================================================
echo Version de Java:
java -version
echo.

echo ====================================================
echo VÉRIFICATION DES DÉPENDANCES
echo ====================================================
echo Nettoyage et création du dossier lib...
if exist lib rmdir /S /Q lib
mkdir lib

echo Téléchargement des dépendances...
call mvn dependency:copy-dependencies -DoutputDirectory=lib -DincludeScope=runtime

echo Vérification des modules JavaFX...
echo Modules disponibles:
dir lib\javafx*.jar 2>NUL
if %ERRORLEVEL% NEQ 0 echo ERREUR: Aucun module JavaFX trouvé!

echo.
echo ====================================================
echo TESTS DE COMPILATION
echo ====================================================
echo Nettoyage et compilation du projet...
call mvn clean compile

echo.
echo ====================================================
echo TESTS D'EXÉCUTION
echo ====================================================
echo Test avec HelloWorld:
echo public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } } > HelloWorld.java
javac HelloWorld.java
java HelloWorld
del HelloWorld.java HelloWorld.class

echo.
echo ====================================================
echo TESTS SWING (sans JavaFX)
echo ====================================================
echo Compilation et lancement de SwingApp...
java -cp target\classes com.artphoria.SwingApp

echo.
echo ====================================================
echo TESTS JAVAFX DE BASE
echo ====================================================
echo Compilation et lancement de UltraBasicApp...
java --module-path lib --add-modules javafx.controls -cp target\classes com.artphoria.UltraBasicApp

echo.
echo ====================================================
echo FIN DU DIAGNOSTIC
echo ====================================================
echo Si certains tests ont échoué, consultez les messages d'erreur ci-dessus.

pause 