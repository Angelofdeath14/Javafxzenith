@echo off
echo Lancement securise d'Artphoria avec SafeLauncher...

REM Nettoyer l'environnement et les fichiers d'erreurs
del /F /Q hs_err_*.log 2>nul

REM Assurer que les répertoires existent
if not exist "lib" mkdir lib

REM Télécharger les dépendances
call mvn dependency:copy-dependencies -DoutputDirectory=lib -DincludeScope=runtime

REM Compiler le projet
call mvn clean compile

REM Options JVM pour résoudre les problèmes de thread JavaFX
set JAVA_OPTS=-XX:+UseG1GC -XX:MaxGCPauseMillis=100 -Dsun.awt.disablegrab=true

REM Lancer avec SafeLauncher pour maximiser les chances de succès
echo Lancement avec SafeLauncher...
java %JAVA_OPTS% ^
  --module-path lib ^
  --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
  --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED ^
  --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED ^
  --add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED ^
  -cp "target\classes;lib\*" ^
  com.artphoria.SafeLauncher

echo Code de retour: %ERRORLEVEL%
pause 