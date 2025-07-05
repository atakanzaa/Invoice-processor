# Invoice Processing API

A Spring Boot application that processes XML invoices, validates them against XSD schema, extracts specific data fields, and saves them to a PostgreSQL database. The application implements multiple design patterns including Strategy, Factory, Observer, Template Method, and Chain of Responsibility patterns for extensible and maintainable architecture.

## 🏗️ Architecture Overview

The application supports two types of XML invoices:
- **Faktura** - Polish VAT invoice with complex structure
- **Invoice** - Simplified invoice format

### Design Patterns Implemented

- **Strategy Pattern** - Different XML processing strategies for each invoice type
- **Factory Pattern** - Automatic processor selection based on XML content
- **Observer Pattern** - Event-driven monitoring of processing stages
- **Template Method Pattern** - Standardized processing workflow
- **Chain of Responsibility** - Flexible validation pipeline
- **Global Exception Handling** - Centralized error management

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Git**

### Environment Setup

1. **Clone the repository:**
```bash
git clone <repository-url>
cd Invoice-processor
```

2. **Setup PostgreSQL Database:**
```sql
-- Create database
CREATE DATABASE invoice_db;

-- Create user (optional)
CREATE USER invoice_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE invoice_db TO invoice_user;
```

3. **Configure Database Connection:**
Update `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/invoice_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server Configuration
server.port=8080

# Swagger Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🔨 Building the Project

### Using Maven Wrapper (Recommended)

```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package the application
./mvnw clean package

# Skip tests during packaging (if needed)
./mvnw clean package -DskipTests
```

### Using System Maven

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn clean package
```

### JAXB Code Generation

The project uses JAXB to generate Java classes from XSD schema. To regenerate classes:

```bash
# Generate JAXB classes from XSD
./mvnw clean compile -Pjaxb

# Or manually trigger JAXB generation
./mvnw jaxb2:generate -Pjaxb
```

Generated classes will be created in `src/main/java/com/example/generated/` package.

## 🏃‍♂️ Running the Application

### Development Mode

```bash
# Run with Maven (recommended for development)
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run with custom port
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Production Mode

```bash
# Build JAR file
./mvnw clean package

# Run the JAR
java -jar target/case-0.0.1-SNAPSHOT.jar

# Run with specific profile
java -jar target/case-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Run with custom configuration
java -jar target/case-0.0.1-SNAPSHOT.jar --server.port=8081 --spring.datasource.url=jdbc:postgresql://prod-db:5432/invoice_db
```

### Docker (Optional)

```dockerfile
# Dockerfile example
FROM openjdk:21-jdk-slim
COPY target/case-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Build Docker image
docker build -t invoice-processor .

# Run with Docker
docker run -p 8080:8080 invoice-processor
```

## 📡 API Documentation

### Base URL
```
http://localhost:8080
```

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Specification
```
http://localhost:8080/api-docs
```

## 🔌 API Endpoints

### Process Invoice

**Endpoint:** `POST /api/invoices`

**Content-Type:** `application/json`

**Request Body:**
```json
{
    "base64xml": "base64-encoded-xml-string"
}
```

**Success Response (201 Created):**
```json
{
    {
    "message": "Invoice saved successfully"
    } 
}
```

**Error Response (400 Bad Request):**
```json
{
    "error": "XML_VALIDATION_ERROR",
    "message": "XSD schema validation failed: Invalid content was found...",
    "path": "/api/invoices",
    "timestamp": "2025-07-05 12:06:01"
}
```

## 📝 Example Requests

### 1. Faktura (Polish VAT Invoice)

**XML Structure:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Faktura xmlns="http://crd.gov.pl/wzor/2023/06/29/12648/">
    <Naglowek>
        <KodFormularza>FA</KodFormularza>
        <WariantFormularza>1</WariantFormularza>
        <DataWytworzeniaFa>2023-12-01T10:30:00</DataWytworzeniaFa>
        <SystemInfo>Test System v1.0</SystemInfo>
    </Naglowek>
    <Podmiot1>
        <DaneIdentyfikacyjne>
            <NIP>1234567890</NIP>
            <Nazwa>Test Company</Nazwa>
        </DaneIdentyfikacyjne>
        <Adres>
            <KodKraju>PL</KodKraju>
            <AdresL1>Test Address</AdresL1>
        </Adres>
    </Podmiot1>
    <Podmiot2>
        <DaneIdentyfikacyjne>
            <NIP>9876543210</NIP>
            <Nazwa>Client Company</Nazwa>
        </DaneIdentyfikacyjne>
    </Podmiot2>
    <Fa>
        <KodWaluty>PLN</KodWaluty>
        <P_1>2023-01-15</P_1>
        <P_2>INV-001</P_2>
        <P_15>1000.00</P_15>
    </Fa>
</Faktura>
```

**Request:**
```bash
curl -X POST http://localhost:8080/api/invoices \
  -H "Content-Type: application/json" \
  -d '{
    "base64xml": "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEZha3R1cmEgeG1sbnM9Imh0dHA6Ly9jcmQuZ292LnBsL3d6b3IvMjAyMy8wNi8yOS8xMjY0OC8iPgogICAgPE5hZ2xvd2VrPgogICAgICAgIDxLb2RGb3JtdWxhcnphPkZBPC9Lb2RGb3JtdWxhcnphPgogICAgICAgIDxXYXJpYW50Rm9ybXVsYXJ6YT4xPC9XYXJpYW50Rm9ybXVsYXJ6YT4KICAgICAgICA8RGF0YVd5dHdvcnplbmlhRmE+MjAyMy0xMi0wMVQxMDozMDowMDwvRGF0YVd5dHdvcnplbmlhRmE+CiAgICAgICAgPFN5c3RlbUluZm8+VGVzdCBTeXN0ZW0gdjEuMDwvU3lzdGVtSW5mbz4KICAgIDwvTmFnbG93ZWs+CiAgICA8UG9kbWlvdDE+CiAgICAgICAgPERhbmVJZGVudHlmaWthY3lqbmU+CiAgICAgICAgICAgIDxOSVA+MTIzNDU2Nzg5MDwvTklQPgogICAgICAgICAgICA8TmF6d2E+VGVzdCBDb21wYW55PC9OYXp3YT4KICAgICAgICA8L0RhbmVJZGVudHlmaWthY3lqbmU+CiAgICAgICAgPEFkcmVzPgogICAgICAgICAgICA8S29kS3JhanU+UEw8L0tvZEtyYWp1PgogICAgICAgICAgICA8QWRyZXNMMT5UZXN0IEFkZHJlc3M8L0FkcmVzTDE+CiAgICAgICAgPC9BZHJlcz4KICAgIDwvUG9kbWlvdDE+CiAgICA8UG9kbWlvdDI+CiAgICAgICAgPERhbmVJZGVudHlmaWthY3lqbmU+CiAgICAgICAgICAgIDxOSVA+OTg3NjU0MzIxMDwvTklQPgogICAgICAgICAgICA8TmF6d2E+Q2xpZW50IENvbXBhbnk8L05hendhPgogICAgICAgIDwvRGFuZUlkZW50eWZpa2FjeWpuZT4KICAgIDwvUG9kbWlvdDI+CiAgICA8RmE+CiAgICAgICAgPEtvZFdhbHV0eT5QTE48L0tvZFdhbHV0eT4KICAgICAgICA8UF8xPjIwMjMtMDEtMTU8L1BfMT4KICAgICAgICA8UF8yPklOVi0wMDE8L1BfMj4KICAgICAgICA8UF8xNT4xMDAwLjAwPC9QXzE1PgogICAgPC9GYT4KPC9GYWt0dXJhPg=="
  }'
```

**Response:**
```json
{
    {
    "message": "Invoice saved successfully"
}
}
```

### 2. Invoice (Simplified Format)

**XML Structure:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Invoice xmlns="http://crd.gov.pl/wzor/2023/06/29/12648/">
    <InvoiceNumber>SIMPLE-INV-123</InvoiceNumber>
</Invoice>
```

**Request:**
```bash
curl -X POST http://localhost:8080/api/invoices \
  -H "Content-Type: application/json" \
  -d '{
    "base64xml": "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEludm9pY2UgeG1sbnM9Imh0dHA6Ly9jcmQuZ292LnBsL3d6b3IvMjAyMy8wNi8yOS8xMjY0OC8iPgogICAgPEludm9pY2VOdW1iZXI+U0lNUExFLUlOVi0xMjM8L0ludm9pY2VOdW1iZXI+CjwvSW52b2ljZT4="
  }'
```

**Response:**
```json
{
    {
    "message": "Invoice saved successfully"
}
}
```

## 🔧 XSD Schema and JAXB Usage

### XSD Schema Location
The XSD schema is located at `src/main/resources/xsd/schemat.xsd` and defines the structure for both Faktura and Invoice XML types.

### JAXB Configuration

**Maven Plugin Configuration (pom.xml):**
```xml
<profiles>
    <profile>
        <id>jaxb</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.codehaus.mojo</groupId>
                    <artifactId>jaxb2-maven-plugin</artifactId>
                    <version>3.1.0</version>
                    <configuration>
                        <sources>
                            <source>src/main/resources/xsd/schemat.xsd</source>
                        </sources>
                        <packageName>com.example.generated</packageName>
                    </configuration>
                    <executions>
                        <execution>
                            <id>xjc</id>
                            <goals>
                                <goal>xjc</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

### Generated Classes
JAXB generates the following main classes:
- `FakturaComplexType` - For Polish VAT invoices
- `InvoiceComplexType` - For simplified invoices
- `ObjectFactory` - Factory for creating JAXB objects
- Various supporting classes for complex types

### Data Extraction Logic

**Faktura Processing:**
- **NIP**: Extracted from `Podmiot1 → DaneIdentyfikacyjne → NIP`
- **P1**: Extracted from `Fa → P_1` (date field)
- **P2**: Extracted from `Fa → P_2` (invoice number)

**Invoice Processing:**
- **NIP**: Set to `"DEFAULT_NIP"`
- **P1**: Extracted from `InvoiceNumber`
- **P2**: Generated as `"INV-" + InvoiceNumber`

## 🗄️ Database Schema

The application automatically creates the following table structure:

```sql
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    nip VARCHAR(255),
    p1 VARCHAR(255),
    p2 VARCHAR(255),
    
);
```

**Fields:**
- `id` - Auto-generated primary key
- `nip` - Tax identification number
- `p1` - Date or invoice number (depending on type)
- `p2` - Invoice number or generated identifier


## 🧪 Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=InvoiceServiceTest

# Run tests with coverage
./mvnw test jacoco:report

# Run integration tests only
./mvnw test -Dtest=*IntegrationTest
```

### Test Coverage

The project includes comprehensive tests:
- **Unit Tests**: Service layer, exception handling, design patterns
- **Integration Tests**: Complete API flow testing
- **Mock Tests**: External dependencies and database operations

### Test Categories

1. **InvoiceServiceTest** - Service layer unit tests
2. **GlobalExceptionHandlerTest** - Exception handling tests
3. **CaseApplicationTests** - Spring Boot context tests

## 🔍 Monitoring and Logging

### Application Logs

The application uses SLF4J with Logback for comprehensive logging:

```bash
# View logs in real-time
tail -f logs/application.log

# Search for specific patterns
grep "ERROR" logs/application.log
grep "Processing completed" logs/application.log
```

### Log Levels

- **INFO** - General processing information
- **DEBUG** - Detailed debugging information
- **WARN** - Warning messages
- **ERROR** - Error conditions with stack traces

### Observer Pattern Logging

The application implements Observer pattern for monitoring:
- Processing start/completion events
- Validation completion events
- Data extraction events
- Error events

## 🚨 Error Handling

### Global Exception Handler

The application includes a comprehensive global exception handler that provides structured error responses:

**Exception Types:**
- `Base64DecodingException` → HTTP 400
- `XmlValidationException` → HTTP 400  
- `XmlUnmarshallingException` → HTTP 400
- `DataExtractionException` → HTTP 400
- `DataAccessException` → HTTP 500
- Generic exceptions → HTTP 500

**Error Response Format:**
```json
{
    "error": "ERROR_TYPE",
    "message": "Detailed error description",
    "path": "/api/invoices",
    "timestamp": "2025-07-05 12:06:01"
}
```

## 🔧 Configuration Options

### Application Properties

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/invoice_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.open-in-view=false

# Logging Configuration
logging.level.com.invoiceprocessor=INFO
logging.level.org.springframework=WARN
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
```

### Environment Variables

```bash
# Database Configuration
export DB_URL=jdbc:postgresql://localhost:5432/invoice_db
export DB_USERNAME=postgres
export DB_PASSWORD=password

# Server Configuration
export SERVER_PORT=8080

# Logging Configuration
export LOG_LEVEL=INFO
```

### Profile-Specific Configuration

**Development Profile (application-dev.properties):**
```properties
spring.jpa.show-sql=true
logging.level.com.invoiceprocessor=DEBUG
```

**Production Profile (application-prod.properties):**
```properties
spring.jpa.show-sql=false
logging.level.com.invoiceprocessor=WARN
```

## 🐛 Troubleshooting

### Common Issues

**1. Port Already in Use**
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process
taskkill /PID <process-id> /F

# Or use different port
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

**2. Database Connection Issues**
```bash
# Check PostgreSQL status
pg_ctl status

# Verify connection
psql -h localhost -p 5432 -U postgres -d invoice_db
```

**3. JAXB Generation Issues**
```bash
# Clean and regenerate
./mvnw clean
./mvnw compile -Pjaxb
```

**4. Maven Wrapper Issues**
```bash
# Make wrapper executable (Linux/Mac)
chmod +x mvnw

# Use system Maven if wrapper fails
mvn clean compile
```

### Debug Mode

```bash
# Run with debug logging
./mvnw spring-boot:run -Dspring-boot.run.arguments=--logging.level.com.invoiceprocessor=DEBUG

# Remote debugging
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## 📚 Additional Resources

### Design Patterns Documentation
- [Strategy Pattern Implementation](src/main/java/com/invoiceprocessor/strategy/)
- [Factory Pattern Implementation](src/main/java/com/invoiceprocessor/factory/)
- [Observer Pattern Implementation](src/main/java/com/invoiceprocessor/observer/)
- [Chain of Responsibility Implementation](src/main/java/com/invoiceprocessor/chain/)

### Dependencies
- **Spring Boot 3.5.3** - Main framework
- **Spring Data JPA** - Database abstraction
- **PostgreSQL Driver** - Database connectivity
- **JAXB** - XML binding
- **Lombok** - Code generation
- **SpringDoc OpenAPI** - API documentation
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework

### External Links
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [JAXB Documentation](https://javaee.github.io/jaxb-v2/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Maven Documentation](https://maven.apache.org/guides/)

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the API documentation at `/swagger-ui.html`

