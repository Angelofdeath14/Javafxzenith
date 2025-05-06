@echo off
echo Lancement via SimpleLauncher...

REM Nettoyer les fichiers d'erreurs
del /F /Q hs_err_*.log 2>nul

REM Compiler le projet
call mvn clean compile

REM Télécharger les dépendances
call mvn dependency:copy-dependencies -DoutputDirectory=lib

REM Options Java supplémentaires
set JAVA_OPTS=-Djavafx.verbose=true -XX:+ShowCodeDetailsInExceptionMessages

REM Lancer avec SimpleLauncher
echo Lancement avec SimpleLauncher.java...
java %JAVA_OPTS% ^
  --module-path lib ^
  --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.web ^
  --add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED ^
  --add-opens=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED ^
  --add-opens=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED ^
  -cp "target\classes;lib\*" ^
  com.artphoria.SimpleLauncher

pause 