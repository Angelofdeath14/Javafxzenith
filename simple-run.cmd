@echo off
echo Lancement simplifie d'Artphoria...

REM Nettoyer les fichiers d'erreurs
del /F /Q hs_err_*.log 2>nul

REM Configuration des modules JavaFX
set PATH_TO_FX=lib
if not exist "%PATH_TO_FX%" mkdir %PATH_TO_FX%

REM Télécharger toutes les dépendances nécessaires
call mvn dependency:copy-dependencies -DoutputDirectory=%PATH_TO_FX%

REM Compiler le projet
call mvn compile

REM Désactiver certaines fonctionnalités problématiques
set JAVA_OPTS=-Djavafx.verbose=true -Djavafx.preloader=false -Dprism.order=sw -Djavafx.debug=true

REM Méthode de lancement alternative - lancer directement depuis java avec modules explicites
echo Lancement de l'application avec modules explicites...
java %JAVA_OPTS% ^
  --module-path %PATH_TO_FX% ^
  --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.web ^
  --add-exports javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED ^
  --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED ^
  --add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED ^
  --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED ^
  --add-exports javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED ^
  -cp "target\classes;%PATH_TO_FX%\*" ^
  test.FxMain

if %ERRORLEVEL% NEQ 0 (
  echo Echec avec test.FxMain, tentative avec com.artphoria.Main...
  java %JAVA_OPTS% ^
    --module-path %PATH_TO_FX% ^
    --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.web ^
    --add-exports javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED ^
    --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED ^
    --add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED ^
    --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED ^
    --add-exports javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED ^
    -cp "target\classes;%PATH_TO_FX%\*" ^
    com.artphoria.Main
)

pause 