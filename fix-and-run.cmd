@echo off
echo Correction et lancement d'Artphoria...

REM Configuration du chemin vers JavaFX
SET JAVAFX_PATH=lib
if not exist "%JAVAFX_PATH%" (
    echo Le dossier JavaFX n'existe pas! Téléchargement automatique...
    REM Créer le dossier lib s'il n'existe pas
    mkdir lib
    
    REM Télécharger les dépendances avec Maven
    call mvn dependency:copy-dependencies -DoutputDirectory=lib
    
    if %ERRORLEVEL% NEQ 0 (
        echo Échec du téléchargement des dépendances. Tentative d'utilisation du classpath Maven...
    )
)

REM Nettoyage des fichiers de crash
del /F /Q hs_err_pid*.log 2>nul
del /F /Q *.dmp 2>nul

REM Compilation
echo Compilation du projet...
call mvn clean compile

REM Préparation des modules JavaFX
echo Configuration des modules JavaFX...
set JAVAFX_MODULES=javafx.controls,javafx.fxml,javafx.graphics,javafx.web,javafx.base

REM Lancement avec le plugin JavaFX
echo Lancement de l'application via le plugin JavaFX...
call mvn javafx:run -Djavafx.mainClass=com.artphoria.Main

REM Si le lancement via Maven échoue, essayer en ligne de commande
if %ERRORLEVEL% NEQ 0 (
    echo Le lancement via Maven a échoué. Essai direct en ligne de commande...
    
    REM Générer le classpath
    echo Génération du classpath...
    for /F "usebackq tokens=*" %%A in (`mvn dependency:build-classpath -Dmdep.outputFile=.\\target\\classpath.txt`) do (
        echo %%A > nul
    )
    
    set /p CP=<.\\target\\classpath.txt
    set FULL_CP=target\classes;%CP%
    
    echo Lancement avec les paramètres de module...
    java --module-path "%JAVAFX_PATH%" --add-modules %JAVAFX_MODULES% -cp "%FULL_CP%" com.artphoria.Main
    
    if %ERRORLEVEL% NEQ 0 (
        echo Tentative avec FxMain...
        java --module-path "%JAVAFX_PATH%" --add-modules %JAVAFX_MODULES% -cp "%FULL_CP%" test.FxMain
    )
)

echo.
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: L'application n'a pas pu être lancée.
    echo Vérifiez que JavaFX est correctement installé et que JAVA_HOME est configuré.
) else (
    echo Application terminée.
)

pause 