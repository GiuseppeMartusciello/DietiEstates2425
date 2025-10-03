# DietiEstates25

Piattaforma per la gestione di annunci immobiliari sviluppata come progetto di Ingegneria del Software (A.A. 2024/2025, Università degli Studi di Napoli Federico II).  
Il sistema consente a clienti e agenzie di pubblicare, ricercare e gestire immobili e offerte, con interfaccia mobile moderna e backend scalabile.

## ✨ Funzionalità principali
- Gestione annunci (creazione, modifica, eliminazione).
- Ricerca avanzata con filtri e mappa interattiva.
- Invio e gestione di offerte (incluse offerte esterne).
- Gestione utenti e ruoli (cliente, agente, gestore, admin di supporto).
- Sistema di notifiche personalizzabili.
- Autenticazione tramite email/password e Google OAuth.
- Informazioni contestuali sugli immobili (API Geoapify: scuole, trasporti, aree verdi vicine).

## 🏗 Architettura
Architettura a tre livelli:
- **Frontend**: app Android sviluppata in Kotlin + Jetpack Compose, pattern MVVM.
- **Backend**: API REST con NestJS, pattern Controller–Service–Repository.
- **Database**: PostgreSQL.
- **DevOps**: backend containerizzato con Docker e deploy su server Linux con dominio DuckDNS.

## 📐 Design
- Interfaccia utente basata su Material Design 3.
- Mock-up realizzati con Figma.
- Testing automatico con Jest (unit test e integrazione).
- Analisi qualità del codice con SonarQube.

## 🔧 Tecnologie utilizzate
- **Frontend**: Kotlin, Jetpack Compose, Gradle  
- **Backend**: NestJS, TypeORM, Node.js, npm  
- **Database**: PostgreSQL  
- **Autenticazione**: Google OAuth, JWT  
- **Geolocalizzazione**: API Geoapify, Google Maps  
- **DevOps**: Docker, Docker Compose, DuckDNS  
- **Design & Tooling**: Figma, GitHub, SonarQube  

## 👥 Team
Progetto sviluppato in team da 3 studenti:  
- Giuseppe Martusciello  
- Riccardo Vincenzo  
- Giuseppe Sindoni  

Ogni membro ha contribuito alla progettazione, allo sviluppo e alla documentazione secondo le linee guida del corso.

## 📄 Documentazione
L’intera documentazione tecnica (requisiti, design, diagrammi, testing e valutazioni di usabilità) è disponibile nel PDF incluso.  
