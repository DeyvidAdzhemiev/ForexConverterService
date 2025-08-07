# Forex Converter Service

A Spring Boot application for foreign exchange conversion that provides RESTful APIs for retrieving exchange rates and performing currency conversions.

## Features

- **Exchange Rate API**: Get the current exchange rate for a currency pair
- **Conversion API**: Convert an amount from one currency to another
- **Conversion History API**: Retrieve conversion history by transaction ID or date range with pagination
- **External Rate Provider**: Uses external service provider (Fixer.io) to retrieve real-time exchange rates
- **Mock Exchange Rate Service**: Includes a mock service for development and testing without external API dependency
- **Configurable Provider**: Easy switching between real and mock exchange rate providers
- **Error Handling**: Comprehensive error handling with specific error codes and messages
- **API Documentation**: Swagger UI for API documentation and testing

## Technical Stack

- Java 21
- Spring Boot 3.5.4
- Spring Data JPA
- H2 Database
- WebClient for HTTP requests
- Lombok for boilerplate code reduction
- Springfox for API documentation

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### API Documentation

Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui/
```

## API Endpoints

### Exchange Rate API

```
GET /api/v1/exchange-rates?sourceCurrency={source}&targetCurrency={target}
```

### Conversion API

```
POST /api/v1/conversions
```

Request Body:
```json
{
  "sourceAmount": 100.00,
  "sourceCurrency": "USD",
  "targetCurrency": "EUR"
}
```

### Conversion History API

```
GET /api/v1/conversions?transactionId={id}
```

or

```
GET /api/v1/conversions?startDate={start}&endDate={end}&page=0&size=10
```

## Database

The application uses an in-memory H2 database. You can access the H2 console at:

```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:forexdb`
- Username: `sa`

## External Rate Provider

The application is configured to use Fixer.io as the exchange rate provider. The free tier of Fixer.io is used by default with the "demo" API key. For production use, you should replace it with your own API key in the `application.properties` file.

### Mock Exchange Rate Service

For development and testing purposes, the application includes a mock exchange rate service that provides simulated exchange rates without requiring an internet connection or API key. This is especially useful when:

- Working in environments with limited internet connectivity
- Avoiding rate limits of free-tier API services
- Running automated tests that should not depend on external services

### Configuration Options

You can easily switch between the real and mock exchange rate providers by modifying the `application.properties` file:

```properties
# Use mock provider (true) or real Fixer.io provider (false)
forex.provider.use-mock=true

# Fixer.io API configuration (used when forex.provider.use-mock=false)
forex.provider.fixer.api-key=0cd7e665a307f0fb02d4b65a3bb6f746
forex.provider.fixer.base-url=http://data.fixer.io/api
```

## Testing

Run the tests using Maven:

```bash
mvn test
```
