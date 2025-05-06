@echo off
echo Lancement d'Artphoria...

REM Supprimer les fichiers temporaires Java
echo Nettoyage des fichiers temporaires...
del /F /Q hs_err_* 2>nul
rmdir /S /Q target\classes\META-INF 2>nul

REM Compiler le projet
echo Compilation du projet...
call mvn clean compile

REM Lancer l'application avec JavaFX
echo Lancement de l'application...
call mvn javafx:run -Djavafx.mainClass=com.artphoria.Main

echo.
if %ERRORLEVEL% NEQ 0 (
    echo Une erreur s'est produite lors du lancement. Essai avec une méthode alternative...
    echo.
    REM Méthode alternative
    call java --module-path "%JAVA_HOME%\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web -cp target\classes;target\dependency\* com.artphoria.Main
)

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Échec du lancement. Essai avec FxMain...
    echo.
    call java --module-path "%JAVA_HOME%\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web -cp target\classes;target\dependency\* test.FxMain
)

pause 