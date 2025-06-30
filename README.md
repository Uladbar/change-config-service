# Config Change Service

A service for tracking and managing configuration changes across applications and environments.

## Technical Details

### Architecture

The service is built using:
- Java 21
- Spring Boot 3.5.3
- In-memory storage (for simplicity)
- REST API for interaction

### Data Model

- **ConfigChange**: Represents a configuration change with:
  - Unique ID
  - Type (ADD, UPDATE, DELETE)
  - Key and value
  - Description
  - Critical flag
  - Timestamp

### API Endpoints

The service provides REST endpoints for:
- Creating new configuration changes (add/update/delete)
- Filtering changes by time range and type
- Retrieving changes by ID

## Running the Service

### Using Docker Compose

1. Clone the repository
2. Navigate to the project directory
3. Run the service using Docker Compose:

```bash
docker-compose up -d
```

4. The service will be available at http://localhost:8080

### Local Development

1. Clone the repository
2. Navigate to the project directory
3. Build the project:

```bash
./gradlew build
```

4. Run the application:

```bash
./gradlew bootRun
```

5. The service will be available at http://localhost:8080

## Testing

Run the tests using:

```bash
./gradlew test
```

This will run all unit tests and generate a test coverage report.

## Using the Postman Collection

The project includes a Postman collection (`Config-change.postman_collection.json`) that you can use to interact with the API.

### Importing the Collection

1. Open Postman
2. Click on "Import" in the top left corner
3. Drag and drop the `Config-change.postman_collection.json` file or browse to select it
4. Click "Import" to add the collection to your workspace

### Available Requests

The collection includes the following requests:

1. **Create**: Creates a new configuration change
   - Automatically extracts the ID from the response and saves it as `configChangeId` variable

2. **Get all**: Retrieves all configuration changes

3. **Get all with filters**: Retrieves configuration changes with filters
   - Includes optional query parameters for filtering by time range, type, and sort order

4. **Get by id**: Retrieves a specific configuration change by ID
   - Uses the `configChangeId` variable set by the Create request

### Using the Collection

1. Start the Config Change Service
2. In Postman, send the "Create" request to create a new configuration change
3. The ID will be automatically saved as a variable
4. Use "Get by id" to retrieve the created configuration
5. Use "Get all" or "Get all with filters" to retrieve multiple configurations

