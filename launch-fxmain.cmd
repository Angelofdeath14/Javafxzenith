@echo off
echo Lancement d'Artphoria avec FxMain (corrigé)...

REM Nettoyer les fichiers d'erreurs
del /F /Q hs_err_*.log 2>nul

REM Compiler le projet
call mvn clean compile

REM Télécharger les dépendances
call mvn dependency:copy-dependencies -DoutputDirectory=lib

REM Configuration JavaFX
set JAVA_OPTS=-Djavafx.verbose=true -Dprism.verbose=true

REM Lancer l'application avec la classe FxMain qui avait l'erreur corrigée
echo Lancement avec FxMain (problème corrigé)...
java %JAVA_OPTS% ^
  --module-path lib ^
  --add-modules=javafx.controls,javafx.fxml,javafx.graphics ^
  --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED ^
  --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED ^
  -cp "target\classes;lib\*" ^
  test.FxMain

echo Code de retour: %ERRORLEVEL%
pause 