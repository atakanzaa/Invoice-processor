# Invoice Processing API

This API processes XML invoices by validating them against an XSD schema, extracting specific fields, and saving them to a PostgreSQL database.

## Features

- ✅ Base64 XML decoding
- ✅ XML validation against XSD schema
- ✅ JAXB unmarshalling to Java objects
- ✅ Data extraction (NIP, P1, P2)
- ✅ Database persistence with Spring Data JPA
- ✅ Comprehensive error handling
- ✅ Swagger/OpenAPI documentation

## API Endpoint

### POST /api/invoices

Processes a Base64-encoded XML invoice and saves it to the database.

**Request Body:**
```json
{
  "base64xml": "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4NCjxGYWt0dXJhIHhtbG5zPSJodHRwOi8vaW52b2ljZXByb2Nlc3Nvc..."
}
```

**Success Response (HTTP 201):**
```json
{
  "message": "Invoice saved successfully"
}
```

**Error Response (HTTP 400):**
```json
{
  "error": "Bad Request", 
  "message": "XML validation failed: ..."
}
```

## XML Schema Structure

The XML must conform to the following structure:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Faktura xmlns="http://invoiceprocessor.com/schema">
    <Podmiot1>
        <DaneIdentyfikacyjne>
            <NIP>1234567890</NIP>
        </DaneIdentyfikacyjne>
    </Podmiot1>
    <Fa>
        <P1>1500.50</P1>
        <P2>300.25</P2>
    </Fa>
</Faktura>
```

## Data Extraction

The API extracts the following fields:

| XML Path | Database Field | Description |
|----------|----------------|-------------|
| `Faktura → Podmiot1 → DaneIdentyfikacyjne → NIP` | `nip` | Company Tax ID |
| `Faktura → Fa → P1` | `p1` | Invoice Field P1 (decimal) |
| `Faktura → Fa → P2` | `p2` | Invoice Field P2 (decimal) |

## Sample Request

**Step 1: Create sample XML**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Faktura xmlns="http://invoiceprocessor.com/schema">
    <Podmiot1>
        <DaneIdentyfikacyjne>
            <NIP>1234567890</NIP>
        </DaneIdentyfikacyjne>
    </Podmiot1>
    <Fa>
        <P1>1500.50</P1>
        <P2>300.25</P2>
    </Fa>
</Faktura>
```

**Step 2: Encode to Base64**
```
PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4NCjxGYWt0dXJhIHhtbG5zPSJodHRwOi8vaW52b2ljZXByb2Nlc3Nvc
```

**Step 3: Make API call**
```bash
curl -X POST http://localhost:8080/api/invoices \
  -H "Content-Type: application/json" \
  -d '{
    "base64xml": "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4NCjxGYWt0dXJhIHhtbG5zPSJodHRwOi8vaW52b2ljZXByb2Nlc3Nvc..."
  }'
```

## Running the Application

1. **Prerequisites:**
   - Java 21
   - PostgreSQL database
   - Maven

2. **Configure Database:**
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/invoice_db
   spring.datasource.username=postgres
   spring.datasource.password=123456
   ```

3. **Build and Run:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access Swagger UI:**
   - Open browser: http://localhost:8080/swagger-ui.html

## Database Schema

The `invoices` table structure:
```sql
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    nip VARCHAR(255) NOT NULL,
    p1 DECIMAL(19,2) NOT NULL,
    p2 DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

## Technology Stack

- **Framework:** Spring Boot 3.5.3
- **Java:** 21
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA
- **XML Processing:** JAXB
- **Validation:** Bean Validation
- **Documentation:** SpringDoc OpenAPI
- **Build Tool:** Maven 