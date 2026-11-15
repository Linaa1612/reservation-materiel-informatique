# Réservation de matériel informatique pour les étudiants

## Description
Ce projet consiste à développer une application Java permettant aux étudiants de réserver du matériel informatique proposé par le service informatique d'un établissement.

Le système permet de réserver des ordinateurs portables, des vidéoprojecteurs et des kits électroniques pour une période déterminée.

## Fonctionnalités
- Gestion des étudiants et des produits.
- Réservation d'un produit pour une période donnée.
- Vérification de la disponibilité du matériel.
- Contrôle de la durée maximale de réservation (7 jours).
- Gestion du quota de réservations (2 commandes maximum par étudiant).
- Calcul automatique du montant de location et de la caution.
- Génération d'une référence unique pour chaque commande.
- Gestion des erreurs à travers des exceptions personnalisées.

## Technologies utilisées
- Java
- Maven
- JUnit 5
- Mockito
- JaCoCo

## Architecture du projet
```text
src/
├── main/
│   └── java/
│       └── ma/ehei/reservation/
│           ├── model/
│           ├── service/
│           └── exception/
│
└── test/
    └── java/
        └── ma/ehei/reservation/
            ├── model/
            └── service/
```

## Méthodologie
Le développement est réalisé selon l'approche **Test-Driven Development (TDD)**, en suivant le cycle :

1. Red : écrire un test qui échoue.
2. Green : développer le code nécessaire pour réussir le test.
3. Refactor : améliorer le code tout en conservant les tests réussis.

## Tests et couverture
Les tests unitaires sont réalisés avec JUnit 5 et Mockito.

JaCoCo est utilisé pour mesurer la couverture du code par les tests.

## Stockage des données
Les étudiants, les produits et les commandes sont conservés en mémoire dans des listes Java. Aucune base de données n'est utilisée.

## Objectif pédagogique
Mettre en pratique la programmation orientée objet, la gestion des exceptions, les tests unitaires et la méthodologie TDD dans un projet Java avec Maven.
