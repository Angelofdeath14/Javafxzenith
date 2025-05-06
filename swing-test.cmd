@echo off
echo Test avec Swing (sans JavaFX)...

REM Compiler le projet
call mvn clean compile

REM Lancer l'application Swing directement
echo Lancement de l'application Swing...
java -cp target\classes com.artphoria.SwingApp

echo Code de retour: %ERRORLEVEL%
pause 