@echo off
echo Lancement sans modules JavaFX...

REM Compiler le projet
call mvn clean compile

REM Télécharger les dépendances
call mvn dependency:copy-dependencies -DoutputDirectory=lib

REM Configuration optimisée pour JavaFX
set JAVA_OPTS=-Djava.awt.headless=false -Djavafx.verbose=true -Dprism.verbose=true

REM Lancer avec tous les JAR dans le classpath au lieu d'utiliser les modules
echo Lancement avec classpath uniquement...
java %JAVA_OPTS% -cp "target\classes;lib\*" com.artphoria.UltraBasicApp

echo Code de retour: %ERRORLEVEL%
pause 