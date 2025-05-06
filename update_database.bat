@echo off
echo Mise a jour de la structure de la base de donnees Artyphoria
echo ----------------------------------------------------------

set MYSQL_USER=root
set MYSQL_PASSWORD=
set MYSQL_HOST=localhost
set MYSQL_DB=artyphoria

echo Verification de MySQL...
where mysql >nul 2>nul
if %errorlevel% neq 0 (
    echo MySQL n'est pas disponible dans le PATH. Verification des emplacements courants...
    
    if exist "C:\xampp\mysql\bin\mysql.exe" (
        echo MySQL trouve dans XAMPP
        set MYSQL_PATH=C:\xampp\mysql\bin\mysql.exe
    ) else if exist "C:\wamp\bin\mysql\mysql5.7.26\bin\mysql.exe" (
        echo MySQL trouve dans WAMP
        set MYSQL_PATH=C:\wamp\bin\mysql\mysql5.7.26\bin\mysql.exe
    ) else if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
        echo MySQL trouve dans MySQL Server 8.0
        set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
    ) else (
        echo MySQL n'a pas été trouvé. Veuillez installer MySQL ou l'ajouter au PATH.
        pause
        exit /b 1
    )
) else (
    set MYSQL_PATH=mysql
)

echo Connexion a la base de donnees %MYSQL_DB%...

echo -- Ajout de la colonne prix_total dans la table reservation...
%MYSQL_PATH% -u%MYSQL_USER% -h%MYSQL_HOST% %MYSQL_DB% -e "ALTER TABLE reservation ADD COLUMN IF NOT EXISTS prix_total DOUBLE DEFAULT 0.0;"
if %errorlevel% equ 0 (
    echo Colonne prix_total ajoutee avec succes ou deja existante
) else (
    echo Erreur lors de l'ajout de la colonne prix_total
)

echo -- Ajout de la colonne prix dans la table evenement...
%MYSQL_PATH% -u%MYSQL_USER% -h%MYSQL_HOST% %MYSQL_DB% -e "ALTER TABLE evenement ADD COLUMN IF NOT EXISTS prix DOUBLE DEFAULT 0.0;"
if %errorlevel% equ 0 (
    echo Colonne prix ajoutee avec succes ou deja existante
) else (
    echo Erreur lors de l'ajout de la colonne prix
)

echo -- Ajout de la colonne capacity dans la table session...
%MYSQL_PATH% -u%MYSQL_USER% -h%MYSQL_HOST% %MYSQL_DB% -e "ALTER TABLE session ADD COLUMN IF NOT EXISTS capacity INT DEFAULT 0;"
if %errorlevel% equ 0 (
    echo Colonne capacity ajoutee avec succes ou deja existante
) else (
    echo Erreur lors de l'ajout de la colonne capacity
)

echo -- Ajout de la colonne available_seats dans la table session...
%MYSQL_PATH% -u%MYSQL_USER% -h%MYSQL_HOST% %MYSQL_DB% -e "ALTER TABLE session ADD COLUMN IF NOT EXISTS available_seats INT DEFAULT 0;"
if %errorlevel% equ 0 (
    echo Colonne available_seats ajoutee avec succes ou deja existante
) else (
    echo Erreur lors de l'ajout de la colonne available_seats
)

echo -- Mise a jour des capacites des sessions...
%MYSQL_PATH% -u%MYSQL_USER% -h%MYSQL_HOST% %MYSQL_DB% -e "UPDATE session s JOIN evenement e ON s.evenement_id = e.id SET s.capacity = e.nbPlace WHERE s.capacity = 0 AND e.nbPlace > 0;"
if %errorlevel% equ 0 (
    echo Capacite des sessions mise a jour avec succes
) else (
    echo Erreur lors de la mise a jour des capacites
)

echo.
echo Mise a jour terminee
echo Si le script a echoue, veuillez executer le fichier SQL manuellement avec phpMyAdmin.
echo Le fichier SQL se trouve dans: src\main\resources\db_update.sql
echo.

pause 