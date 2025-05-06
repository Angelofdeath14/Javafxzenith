@echo off
echo Lancement direct d'Artphoria (mode alternatif)...

REM Nettoyage des fichiers d'erreur
del /F /Q hs_err_pid*.log 2>nul

REM Télécharger les dépendances si nécessaire
call mvn dependency:copy-dependencies -DoutputDirectory=lib -DincludeScope=runtime

REM Compiler le projet
call mvn clean compile

REM Configurer les options Java pour éviter les problèmes de thread UI
set JAVA_OPTS=-Dprism.order=sw -Dglass.win.uiScale=100%% -Djavafx.animation.fullspeed=false -Djavafx.preloader=false -Djavafx.concurrent.daemon=true

REM Lancer directement avec java (contourne Maven qui peut causer des problèmes)
echo Lancement avec la méthode directe...
java %JAVA_OPTS% ^
  --module-path "lib" ^
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web,javafx.base ^
  --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED ^
  --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED ^
  -cp "target\classes;lib\*" ^
  com.artphoria.Main

if %ERRORLEVEL% NEQ 0 (
  echo Premier lancement échoué, tentative avec mode de compatibilité...
  java %JAVA_OPTS% ^
    --module-path "lib" ^
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web,javafx.base ^
    --add-opens java.base/java.lang=ALL-UNNAMED ^
    --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED ^
    --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED ^
    --add-opens javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED ^
    -Djava.awt.headless=false ^
    -cp "target\classes;lib\*" ^
    test.FxMain
)

pause 