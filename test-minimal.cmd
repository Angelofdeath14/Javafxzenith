@echo off
echo Test de JavaFX avec application minimale...

REM Nettoyer les fichiers d'erreurs
del /F /Q hs_err_*.log 2>nul

REM Compiler le projet
call mvn clean compile

REM Télécharger les dépendances
call mvn dependency:copy-dependencies -DoutputDirectory=lib

REM Lancer l'application minimale
echo Lancement de l'application minimale...
java --module-path lib --add-modules javafx.controls -cp "target\classes;lib\*" com.artphoria.MinimalApp

echo Code de retour: %ERRORLEVEL%
pause 