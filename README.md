# Projet Microservices - Gestion de Bibliothèque avec Kafka

## Description du Projet

Application de gestion de bibliothèque utilisant une architecture microservices avec communication asynchrone via Kafka.

---

## Objectifs Réalisés

Ce projet étend une architecture microservices existante en ajoutant :

- **Persistance MySQL** - Chaque service métier possède sa propre base de données
- **Communication Kafka** - Communication asynchrone entre les services
- **Notification Service** - Service découplé consommant les événements Kafka
- **Déploiement Docker** - Orchestration complète via Docker Compose

---

##  Architecture du Projet

### Services de Base (Architecture conservée)

**1. Eureka Server (Port 8761)**
- Rôle : Découverte des services
- Fonction : Registre central où tous les microservices s'enregistrent

**2. Gateway (Port 9999)**
- Rôle : Point d'entrée unique
- Fonction : Routage dynamique vers les microservices

**3. User Service (Port 8082)**
- Rôle : Gestion des utilisateurs
- Base de données : MySQL (userdb)

**4. Book Service (Port 8081)**
- Rôle : Gestion des livres
- Base de données : MySQL (bookdb)

**5. Emprunter Service (Port 8083)**
- Rôle : Gestion des emprunts
- Base de données : MySQL (empruntdb)
- Extension : Producteur Kafka

### Nouveau Service Ajouté

**6. Notification Service (Port 8084)**
- Rôle : Gestion des notifications asynchrones
- Base de données : MySQL (notificationdb)
- Fonction : Consommateur Kafka uniquement

### Infrastructure

- **Zookeeper (Port 2181)** - Coordination pour Kafka
- **Kafka (Port 9092)** - Message Broker pour la communication asynchrone
- **MySQL** - 4 instances sur les ports 3307, 3308, 3309, 3310

---

## 1️⃣ Base de données MySQL

### Principe : Database per Service

Chaque microservice possède sa propre base de données, garantissant l'isolation et l'indépendance des données.

### Configuration des bases de données

**User Service → userdb**
- Conteneur : `mysql-user`
- Port : `3307`
- Credentials : `root / root`

**Book Service → bookdb**
- Conteneur : `mysql-book`
- Port : `3308`
- Credentials : `root / root`

**Emprunter Service → empruntdb**
- Conteneur : `mysql-emprunt`
- Port : `3309`
- Credentials : `root / root`

**Notification Service → notificationdb**
- Conteneur : `mysql-notification`
- Port : `3310`
- Credentials : `root / root`

---

## 2️⃣ Kafka – Communication asynchrone

### Objectif

Découpler la logique métier de la logique de notification. Quand un emprunt est créé, le service emprunter publie simplement un événement sur Kafka sans appeler directement le service de notification.

### Configuration

- **Bootstrap Servers** : `kafka:29092` (interne) ou `localhost:9092` (externe)
- **Topic créé** : `emprunt-created`
- **Producteur** : `emprunter-service`
- **Consommateur** : `notification-service`

### Flux de Communication
```
1. Utilisateur crée un emprunt via API REST
        ↓
2. Emprunter Service sauvegarde l'emprunt en base MySQL
        ↓
3. Emprunter Service publie un événement sur Kafka
        ↓
4. Kafka stocke l'événement dans le topic "emprunt-created"
        ↓
5. Notification Service écoute et reçoit l'événement
        ↓
6. Notification Service crée une notification en base
```

---

## 3️⃣ Notification Service

### Rôle

- Consommer les événements Kafka
- Gérer les notifications de manière asynchrone

### Fonctionnement

- **Aucun appel REST entrant** - Ce service n'expose aucun endpoint HTTP
- **Kafka Consumer uniquement** - Fonctionne en arrière-plan
- **Sauvegarde en base MySQL** - Chaque événement crée une notification

---

## 4️⃣ Kafka – Détails techniques

### Topic : emprunt-created

**Producteur** : emprunter-service  
**Consommateur** : notification-service



## 🚀 Déploiement via Docker Compose

### Conteneurs Déployés

Le projet déploie **12 conteneurs Docker** :

**Services Spring Boot (6)** : eureka-service, gateway-service, user-service, book-service, emprunt-service, notification-service

**Bases MySQL (4)** : mysql-user, mysql-book, mysql-emprunt, mysql-notification

**Infrastructure Kafka (2)** : zookeeper, kafka

### Commandes Docker

**Démarrer tous les services** :
```bash
docker compose up -d
```

**Vérifier l'état** :
```bash
docker ps
```

**Voir les logs** :
```bash
docker logs notification-service -f
docker logs emprunt-service -f
```

**Arrêter** :
```bash
docker compose down
```

---

## 🧪 Tests et Validation

### Test 1 : Créer un utilisateur
```bash
POST http://localhost:8082/users
Content-Type: application/json

{
  "name": "noura",
  "email": "noura@test.com"
}
```

### Test 2 : Créer un livre
```bash
POST http://localhost:8081/books
Content-Type: application/json

{
  "titre": "Le Monde de Sophie",
  "auteur": "Jostein Gaarder",
  "isbn": "12345",
  "disponible": true
}
```

### Test 3 : Créer un emprunt
```bash
POST http://localhost:8083/emprunts/1/1
```

**Réponse attendue** :
```json
{
  "id": 1,
  "userId": 1,
  "bookId": 1,
  "empruntDate": "2026-01-11"
}
```

### Test 4 : Vérifier la notification
```bash
docker exec -it mysql-notification mysql -uroot -proot -e "SELECT * FROM notificationdb.notifications;"
```

---



---


---

