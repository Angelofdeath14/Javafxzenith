@echo off
echo Lancement ultra-basique...

REM Compiler le projet
call mvn clean compile

REM Télécharger les dépendances JavaFX
call mvn dependency:copy-dependencies -DoutputDirectory=lib

REM Lancer l'application avec un minimum d'options
echo Lancement de l'application UltraBasicApp...
java --module-path lib --add-modules=javafx.controls -cp "target\classes" com.artphoria.UltraBasicApp

echo Code de retour: %ERRORLEVEL%
pause 