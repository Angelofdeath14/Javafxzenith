@echo off
echo Lancement basique d'Artphoria...

REM Nettoyer les fichiers d'erreurs
del /F /Q hs_err_*.log 2>nul

REM Compiler le projet
call mvn clean compile

REM Télécharger les dépendances
call mvn dependency:copy-dependencies -DoutputDirectory=lib

REM Configuration minimale pour JavaFX
set JAVA_FX_ARGS=--module-path lib --add-modules javafx.controls,javafx.fxml

REM Lancer l'application de la façon la plus simple possible
echo Lancement de l'application...
java %JAVA_FX_ARGS% -cp "target\classes;lib\*" com.artphoria.SimpleLauncher

if %ERRORLEVEL% NEQ 0 (
  echo Premier essai échoué, seconde tentative...
  java %JAVA_FX_ARGS% -cp "target\classes;lib\*" test.FxMain
)

pause 