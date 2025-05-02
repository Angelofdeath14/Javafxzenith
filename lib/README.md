# Bibliothèques externes pour Artphoria

Ce répertoire contient les bibliothèques externes nécessaires pour le projet Artphoria.

## Bibliothèques requises

### Pour la fonctionnalité Text-to-Speech (TTS)

Aucune bibliothèque externe n'est nécessaire car l'application utilise le moteur TTS de Windows via PowerShell.

Si vous souhaitez utiliser FreeTTS comme alternative, vous devez télécharger et ajouter les bibliothèques suivantes :

1. [FreeTTS 1.2.2](https://sourceforge.net/projects/freetts/files/FreeTTS/FreeTTS%201.2.2/)
   - `freetts.jar` - Bibliothèque principale
   - `freetts-jsapi10.jar` - Interface JSAPI
   - `en_us.jar` - Voix en anglais (si nécessaire)

2. [JSAPI 1.0](https://sourceforge.net/projects/freetts/files/JSAPI/JSAPI%201.0/)
   - `jsapi.jar` - Interface de synthèse vocale Java

### Comment installer les bibliothèques

1. Téléchargez les fichiers JAR mentionnés ci-dessus
2. Placez-les dans ce répertoire (`lib/`)
3. Ajoutez-les au classpath du projet dans votre IDE

## Notes d'implémentation

L'application utilise actuellement une approche simplifiée qui ne nécessite aucune bibliothèque externe :
- Pour Windows : utilise les commandes PowerShell avec `System.Speech.Synthesis.SpeechSynthesizer`
- Cette approche fonctionne uniquement sur Windows et nécessite PowerShell

Si vous avez besoin d'une solution multiplateforme, envisagez d'utiliser FreeTTS ou une autre bibliothèque TTS Java. 