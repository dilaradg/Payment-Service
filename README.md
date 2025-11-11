# Payment Service - Shop AG Integration

## 📋 Übersicht

Der Payment Service ist Teil der AVG Integrationslösung und verarbeitet Zahlungen für Bestellungen.

## 🚀 Quick Start

### Voraussetzungen
- Java 17 oder höher
- Maven 3.6+

### Service starten

```bash
# Mit Maven
mvn spring-boot:run

# Oder mit Maven Wrapper
./mvnw spring-boot:run
```

Der Service läuft dann auf: **http://localhost:8082**

### Swagger UI

Öffne im Browser: **http://localhost:8082/swagger-ui.html**

Hier könnt ihr die API direkt testen!

## 📡 API Endpoints

### 1. Zahlung verarbeiten
```
POST http://localhost:8082/api/payment/process
Content-Type: application/json

{
  "orderId": "ORD-2025-10-16-7891",
  "customerId": "CUST-45823",
  "amount": 209.97,
  "currency": "EUR",
  "paymentMethod": "CREDIT_CARD",
  "customerName": "Andreas Heberle"
}
```

**Antwort bei Erfolg (200):**
```json
{
  "paymentId": "PAY-2025-11-09-a1b2c3d4",
  "orderId": "ORD-2025-10-16-7891",
  "status": "SUCCESS",
  "amount": 209.97,
  "currency": "EUR",
  "transactionId": "TXN-xyz123abc456",
  "message": "Payment processed successfully",
  "timestamp": "2025-11-09T14:30:00"
}
```

**Antwort bei Fehler (402):**
```json
{
  "paymentId": "PAY-2025-11-09-e5f6g7h8",
  "orderId": "ORD-2025-10-16-7891",
  "status": "FAILED",
  "amount": 209.97,
  "currency": "EUR",
  "errorCode": "INSUFFICIENT_FUNDS",
  "message": "Insufficient funds on customer account",
  "timestamp": "2025-11-09T14:30:00"
}
```

### 2. Zahlungsstatus abfragen
```
GET http://localhost:8082/api/payment/{paymentId}
```

### 3. Health Check
```
GET http://localhost:8082/api/payment/health
```

## 🎯 Test-Szenarien

Der Service simuliert verschiedene Zahlungsszenarien:

### Normaler Modus (zufällig):
- **80%** Erfolgreiche Zahlungen
- **15%** Insufficient Funds
- **5%** Technical Error

### Force Success (für Tests):
Verwende `TEST-SUCCESS` in der `orderId`:
```json
{
  "orderId": "TEST-SUCCESS-12345",
  ...
}
```
→ Garantiert erfolgreiche Zahlung

### Force Failure (für Tests):
Verwende `TEST-FAIL` in der `orderId`:
```json
{
  "orderId": "TEST-FAIL-12345",
  ...
}
```
→ Garantiert fehlgeschlagene Zahlung (Insufficient Funds)

## 🔗 Integration mit anderen Services

### Von OMS erwartet:
Der OMS Service ruft diesen Endpoint auf:
```
POST http://localhost:8082/api/payment/process
```

Mit folgenden Daten:
- `orderId` - Order ID aus OMS
- `customerId` - Customer ID
- `amount` - Gesamtbetrag der Bestellung
- `currency` - Währung (z.B. "EUR")
- `paymentMethod` - Zahlungsmethode
- `customerName` - Name des Kunden

### An OMS zurückgeben:
- Bei **Erfolg (Status 200)**: Payment erfolgreich, OMS kann fortfahren
- Bei **Fehler (Status 402)**: Payment fehlgeschlagen, OMS muss Bestellung stornieren

## 📊 Logs

Alle Zahlungen werden geloggt:
- ✅ Erfolgreiche Zahlungen
- ❌ Fehlgeschlagene Zahlungen
- 📨 Eingehende Requests

## 🧪 Testen mit Postman/Curl

### Erfolgreiche Zahlung testen:
```bash
curl -X POST http://localhost:8082/api/payment/process \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "TEST-SUCCESS-001",
    "customerId": "CUST-12345",
    "amount": 99.99,
    "currency": "EUR",
    "paymentMethod": "CREDIT_CARD",
    "customerName": "Test User"
  }'
```

### Fehlgeschlagene Zahlung testen:
```bash
curl -X POST http://localhost:8082/api/payment/process \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "TEST-FAIL-001",
    "customerId": "CUST-12345",
    "amount": 99.99,
    "currency": "EUR",
    "paymentMethod": "CREDIT_CARD",
    "customerName": "Test User"
  }'
```

## 🏗️ Projekt-Struktur

```
payment-service/
├── src/
│   ├── main/
│   │   ├── java/de/hka/avg/payment/
│   │   │   ├── PaymentServiceApplication.java    # Main Class
│   │   │   ├── controller/
│   │   │   │   └── PaymentController.java        # REST Endpoints
│   │   │   ├── service/
│   │   │   │   └── PaymentService.java           # Business Logic
│   │   │   └── dto/
│   │   │       ├── PaymentRequest.java           # Request DTO
│   │   │       ├── PaymentResponse.java          # Response DTO
│   │   │       └── ErrorResponse.java            # Error DTO
│   │   └── resources/
│   │       └── application.properties            # Konfiguration
├── pom.xml                                       # Maven Dependencies
└── payment-service-api.yaml                      # OpenAPI Spec
```

## 👥 Für eure Gruppe

### Was die anderen Services wissen müssen:

1. **Port**: 8082
2. **Endpoint**: `POST /api/payment/process`
3. **Request Format**: Siehe oben
4. **Response Format**: 
   - Status 200 = Erfolg
   - Status 402 = Zahlung fehlgeschlagen
   - Status 400 = Ungültige Anfrage
   - Status 500 = Server Error

### Was ihr von OMS braucht:
- Der OMS muss euch nach erfolgreicher Inventory-Prüfung aufrufen
- Ihr bekommt die Order-Daten inkl. totalAmount
- Ihr gebt SUCCESS/FAILED zurück

### Integration Flow:
```
OMS → IS (check inventory)
    ↓ (if available)
OMS → PAYMENT SERVICE ← IHR SEID HIER!
    ↓ (if success)
OMS → WMS (via RabbitMQ)
```

## 🐛 Troubleshooting

**Port 8082 schon belegt?**
```bash
# Anderen Port in application.properties setzen:
server.port=8083
```

**Maven Fehler?**
```bash
# Dependencies neu laden:
mvn clean install
```

**Service startet nicht?**
- Java Version prüfen: `java -version` (muss 17+ sein)
- Lombok Plugin in IDE installiert?

## 📝 Für die Abgabe

Vergesst nicht:
1. ✅ OpenAPI Spec (payment-service-api.yaml)
2. ✅ Code mit Kommentaren
3. ✅ README mit Erklärung
4. ✅ Mindestens 3 Test-Szenarien dokumentieren

---

**Viel Erfolg! 🚀**

Bei Fragen: Fragt Claude oder eure Gruppenmitglieder!