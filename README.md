# Übungsprotokoll: Verteilte Warehouse-Architektur mit Kafka

## 1. Systemübersicht

In dieser Übung wurde eine Message-Oriented-Middleware (MOM) implementiert, um die Kommunikation zwischen verschiedenen Warehouse-Standorten und einer Zentrale zu realisieren. Als technologische Basis dient **Apache Kafka** als Message Broker.

Das System ist in drei Hauptkomponenten unterteilt:

1. **Zentrale (Consumer):** Läuft auf Port `8081` und empfängt Daten.
2. **Warehouse-Standorte (Producer):** Laufen standardmäßig auf Port `8080` (skalierbar auf weitere Ports) und generieren Daten.
3. **Message Broker:** Apache Kafka Container auf Port `9092`.

## 2. Kommunikationsablauf

Die Architektur folgt einem losen Kopplungsprinzip.

* **Senden:** Ein Standort sammelt Lagerdaten und sendet diese an das Kafka-Topic `warehouse-data-{ID}` (z.B. `warehouse-data-W001`).
* **Empfangen:** Die Zentrale abonniert diese Topics (Pattern `warehouse-data-.*`), verarbeitet die Daten und speichert sie.
* **Bestätigung:** Nach erfolgreicher Verarbeitung sendet die Zentrale eine Bestätigung ("SUCCESS") an das Topic `warehouse-response-{ID}` zurück, worauf der Standort lauscht.

## 3. Implementierungsdetails

### Zentrale (Backend)

Die Zentrale fungiert primär als Kafka Consumer. Sie ist Teil der Consumer Group `warehouse-zentrale-group`.

* **Features:** REST-Schnittstellen zur Datenabfrage (`/api/warehouse/data` für alle Daten oder spezifisch per ID).
* **Services:** Ein Aggregation Service sammelt die eingehenden Datenströme.
* **Logging:** Alle Vorgänge werden sowohl in der Konsole als auch in `logs/zentrale.log` protokolliert.

### Warehouse-Standort (Client)

Der Client simuliert einen Lagerstandort.

* **Datengenerierung:** Ein Generator erstellt automatisch Datensätze mit 5 zufälligen Produkten und Mengen.
* **Intervall:** Die Datenübertragung erfolgt automatisch alle 60 Sekunden (`60000ms`).
* **Steuerung:** Über `POST /warehouse/send` kann ein Sendevorgang manuell getriggert werden.

### Datenstruktur

Die übertragenen JSON-Objekte basieren auf folgenden Modellen:

* **WarehouseData:** Enthält Metadaten (ID, Name, Adresse, PLZ, Stadt, Land), einen Timestamp und eine Liste der Produkte.
* **ProductData:** Detaillierte Infos pro Artikel (ID, Name, Kategorie, Menge, Einheit).

## 4. Inbetriebnahme & Tests

Das System wird über Gradle und Docker gestartet.

**Schritt 1: Infrastruktur**
Starten des Kafka-Containers:

```bash
docker run -d --name kafka -p 9092:9092 apache/kafka:latest

```

**Schritt 2: Start der Zentrale**

```bash
cd zentrale && ./gradlew bootRun

```

**Schritt 3: Start der Standorte**
Erster Standort (Standard):

```bash
cd warehouse-standort && ./gradlew bootRun

```

Zweiter Standort (Simuliert auf neuem Port):

```bash
./gradlew bootRun --args='--server.port=8082 --warehouse.id=W002'

```

**Logging & Monitoring:**
Die Logs werden in separate Dateien geschrieben (`warehouse-data.log`, `warehouse-sent-data.log` etc.). Der Log-Level für die Anwendung ist auf `INFO` gesetzt, für Frameworks auf `WARN`.

---

## 5. Beantwortung der Fragestellungen

### 1. Welche Eigenschaften zeichnen eine Message Oriented Middleware (MOM) aus?

Eine MOM bietet eine Infrastruktur für den Datenaustausch in verteilten Systemen. Die Kerneigenschaften sind:

* **Asynchronität:** Sender und Empfänger agieren zeitunabhängig voneinander.
* **Lose Kopplung:** Die Komponenten müssen nichts über den technischen Aufbau der Gegenseite wissen.
* **Persistenz & Zuverlässigkeit:** Der Broker (hier Kafka) speichert Nachrichten zwischen ("Message Queuing"), wodurch eine garantierte Zustellung auch bei Ausfällen ermöglicht wird.
* **Skalierbarkeit:** Das System kann leicht um weitere Producer oder Consumer erweitert werden.

### 2. Was ist der Unterschied zwischen transienter und synchroner Kommunikation?

* **Synchrone Kommunikation:** Hier blockiert der Sender, bis er eine Antwort erhält (klassisches Request/Response-Muster, z.B. HTTP). Sender und Empfänger müssen gleichzeitig aktiv sein.
* **Transiente Kommunikation:** Die Nachricht wird nur dann übertragen, wenn Sender und Empfänger *zum Zeitpunkt des Sendens* aktiv sind. Ist der Empfänger offline, geht die Nachricht verloren (typisch für direkte Socket-Verbindungen oder UDP). Sie wird nicht zwischengespeichert.

### 3. Wie funktioniert eine JMS Queue?

Eine JMS Queue basiert auf dem **Point-to-Point (P2P)** Modell.
Dabei sendet ein Producer eine Nachricht in eine Warteschlange. Das entscheidende Merkmal ist, dass jede Nachricht von **genau einem** Consumer verarbeitet wird. Selbst wenn mehrere Consumer an der Queue hängen, wird die Nachricht nicht vervielfältigt, sondern an den nächsten freien Consumer verteilt (Lastverteilung/Load Balancing). Nach der Verarbeitung verschwindet die Nachricht aus der Queue.

### 4. Nennen und beschreiben Sie die wichtigsten JMS Klassen.

Der Ablauf in JMS wird durch folgende Hierarchie bestimmt:

1. **ConnectionFactory:** Fabrik-Objekt, um die Verbindung zum Provider (Broker) herzustellen.
2. **Connection:** Die aktive TCP/IP-Verbindung zum Broker.
3. **Session:** Ein Thread-Kontext innerhalb der Verbindung, in dem Nachrichten gesendet oder empfangen werden.
4. **Destination:** Das Ziel der Nachricht (entweder ein *Topic* oder eine *Queue*).
5. **MessageProducer / MessageConsumer:** Die Komponenten, die Nachrichten an die Destination senden bzw. von dort abholen.
6. **Message:** Das Datenpaket selbst (z.B. TextMessage).

### 5. Wie funktioniert ein JMS Topic und wie unterscheidet es sich von einer Queue?

Ein Topic implementiert das **Publish-Subscribe** Modell (Broadcast).
Im Gegensatz zur Queue (1:1 Kommunikation) gilt hier das **1:n Prinzip**. Ein Publisher sendet eine Nachricht an ein Topic, und **alle** aktiven Subscriber, die dieses Topic abonniert haben, erhalten eine Kopie der Nachricht. Dies eignet sich besonders für Events oder Benachrichtigungen, die für mehrere Systemteile relevant sind.

### 6. Erklären Sie den Begriff "lose gekoppeltes verteiltes System" am Beispiel dieser Übung.

"Lose Kopplung" bedeutet, dass die Abhängigkeiten zwischen den Systemkomponenten auf ein Minimum reduziert sind.
In unserem Warehouse-Beispiel zeigt sich das wie folgt:

* **Räumlich/Technisch:** Der Standort muss nicht wissen, wo die Zentrale läuft oder in welcher Sprache sie geschrieben ist. Er kennt nur die Adresse des Brokers (Kafka).
* **Zeitlich:** Wenn die Zentrale abstürzt oder gewartet wird, kann der Standort weiterhin Daten senden. Kafka puffert diese. Sobald die Zentrale wieder online ist, verarbeitet sie die aufgelaufenen Nachrichten. Der Betrieb des Standorts wird durch Probleme in der Zentrale nicht blockiert.