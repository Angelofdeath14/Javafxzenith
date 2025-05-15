# 🎨🛠️ Plateforme Web et Java : Art & Artisanat

Ce projet est une plateforme complète (web et application Java) dédiée à la promotion de l'art et de l'artisanat. Il permet aux utilisateurs de découvrir des produits artisanaux, de participer à des événements culturels, et d'interagir avec des artisans. L'application intègre un système de gestion des utilisateurs, des produits, des commandes, des événements et des réclamations.

## 🌐 Technologies utilisées

- **Frontend Web** : HTML/CSS/JavaScript
- **Backend Web** : PHP 
- **Application Java** : JavaFX 
- **Base de données** : MySQL 
- **Système de mailing** : SMTP / API Emailing

---

## 🔐 Fonctionnalités Utilisateurs

### Authentification
- Création de compte
- Connexion
- Récupération de mot de passe via email
- Activation/Désactivation du compte par l’administrateur

### Gestion des Produits
- Ajout de produits par l’utilisateur
- Validation ou rejet des produits par l’admin

### Commandes
- Création de commande par l’utilisateur
- Validation ou rejet des commandes par l’admin
- Si le paiement est effectué par carte, la commande est **automatiquement acceptée**

### Événements & Sessions
- L’admin peut ajouter des **événements** et des **sessions**
- L’utilisateur peut :
  - Consulter les événements
  - Consulter les sessions associées
  - Réserver une place à une session

### Réclamations
- L’utilisateur peut soumettre une réclamation
- L’admin peut traiter les réclamations
- Les réclamations contenant des **mots non polis** sont **automatiquement supprimées**

### Avis & Notation
- L’utilisateur peut donner une **note** et un **avis** sur un événement auquel il a participé

---

## 👩‍💼 Fonctionnalités Administrateur

- Gestion des comptes utilisateurs (activation/désactivation)
- Validation/Rejet des produits et commandes
- Ajout et gestion des événements et sessions
- Traitement des réclamations

---

## 📂 Structure du Projet


Vous avez dit :
# 🎨🛠️ Plateforme Web et Java : Art & Artisanat Ce projet est une plateforme complète (web et application Java) dédiée à la promotion de l'art et de l'artisanat. Il permet aux utilisateurs de découvrir des produits artisanaux, de participer à des événements culturels, et d'interagir avec des artisans. L'application intègre un système de gestion des utilisateurs, des produits, des commandes, des événements et des réclamations. ## 🌐 Technologies utilisées - **Frontend Web** : HTML/CSS/JavaScript - **Backend Web** : PHP - **Application Java** : JavaFX - **Base de données** : MySQL - **Système de mailing** : SMTP / API Emailing --- ## 🔐 Fonctionnalités Utilisateurs ### Authentification - Création de compte - Connexion - Récupération de mot de passe via email - Activation/Désactivation du compte par l’administrateur ### Gestion des Produits - Ajout de produits par l’utilisateur - Validation ou rejet des produits par l’admin ### Commandes - Création de commande par l’utilisateur - Validation ou rejet des commandes par l’admin - Si le paiement est effectué par carte, la commande est **automatiquement acceptée** ### Événements & Sessions - L’admin peut ajouter des **événements** et des **sessions** - L’utilisateur peut : - Consulter les événements - Consulter les sessions associées - Réserver une place à une session ### Réclamations - L’utilisateur peut soumettre une réclamation - L’admin peut traiter les réclamations - Les réclamations contenant des **mots non polis** sont **automatiquement supprimées** ### Avis & Notation - L’utilisateur peut donner une **note** et un **avis** sur un événement auquel il a participé --- ## 👩‍💼 Fonctionnalités Administrateur - Gestion des comptes utilisateurs (activation/désactivation) - Validation/Rejet des produits et commandes - Ajout et gestion des événements et sessions - Traitement des réclamations --- ## 📂 Structure du Projet 
ChatGPT a dit :
Voici une suggestion pour compléter et structurer la section 📂 Structure du Projet de manière claire et professionnelle :
📂 Structure du Projet
1. Frontend Web (HTML/CSS/JavaScript)
• index.html : page d’accueil
• login.html / register.html : pages d’authentification
• dashboard.html : interface utilisateur après connexion
• products.html : affichage des produits
• events.html / sessions.html : consultation des événements et réservations
• complaints.html : formulaire de réclamations
• admin/ : tableau de bord administrateur
2. Backend Web (PHP)
• api/ :
• auth.php : gestion de l’authentification
• products.php : CRUD des produits
• orders.php : gestion des commandes
• events.php : gestion des événements et sessions
• complaints.php : traitement des réclamations
• users.php : gestion des comptes utilisateurs
• config/ : fichiers de configuration (base de données, mailing)
• utils/ : fonctions utilitaires (filtrage des mots non polis, envoi d’email)
3. Application Java (JavaFX)
• MainApp.java : point d’entrée de l’application
• controllers/ :
• LoginController.java
• ProductController.java
• OrderController.java
• EventController.java
• ComplaintController.java
• views/ : fichiers FXML pour l’interface graphique
• models/ : classes représentant les entités (Utilisateur, Produit, Commande, etc.)
• services/ : communication avec l’API PHP via HTTP (POST/GET)
4. Base de Données (MySQL)
• Tables principales :
• users
• products
• orders
• events
• sessions
• complaints
• ratings
• Relations entre utilisateurs, produits, événements et sessions bien définies (clefs étrangères)
5. Système de Mailing (SMTP/API)
• Envoi automatique :
• d’emails de confirmation d’inscription
• de mails de récupération de mot de passe
• de notifications liées à la validation de commande ou de produit









