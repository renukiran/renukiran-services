# Trainee Sign-In API Service

## Overview
This is a Spring Boot REST API service that provides a secure HTTPS endpoint for trainee sign-in with username and password validation.

## Features
- **HTTPS-only**: Enforces secure HTTPS connections on port 8443
- **Request Validation**: Validates username and password using Jakarta Validation annotations
- **Error Handling**: Returns appropriate HTTP status codes for different error scenarios
- **Authentication Service**: Placeholder service for credential verification
- **Token Generation**: Generates bearer tokens upon successful authentication

## Endpoint

### Sign-In Endpoint
- **URL**: `/user/trainee/sign-in`
- **Method**: `POST`
- **Protocol**: HTTPS only
- **Port**: 8443

### Request Body
```json
{
  "username": "trainee",
  "password": "password123"
}
```

### Validation Rules
- **Username**: 
  - Required (non-blank)
  - Length: 3-50 characters
  - Allowed characters: letters, numbers, dots, underscores, and hyphens

- **Password**: 
  - Required (non-blank)
  - Length: 6-100 characters

### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Authentication successful",
  "token": "Bearer <uuid-token>",
  "statusCode": 200
}
```

### Error Responses

#### Invalid Credentials (HTTP 401)
```json
{
  "success": false,
  "message": "Invalid username or password",
  "token": null,
  "statusCode": 401
}
```

#### Validation Error (HTTP 400)
```json
{
  "success": false,
  "message": "Validation failed: username - Username must be between 3 and 50 characters; ",
  "token": null,
  "statusCode": 400
}
```

#### Internal Server Error (HTTP 500)
```json
{
  "success": false,
  "message": "An error occurred: <error details>",
  "token": null,
  "statusCode": 500
}
```

## Setup Instructions

### 1. Generate Self-Signed Certificate (for development)

On Windows PowerShell, run:
```powershell
cd src/main/resources

# Generate PKCS12 keystore with self-signed certificate
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 `
  -keystore keystore.p12 -storetype PKCS12 -storepass changeit `
  -validity 365 -dname "CN=localhost,OU=Development,O=RenuKiran,L=Location,ST=State,C=IN"
```

The keystore will be created at `src/main/resources/keystore.p12`

### 2. Build the Application

```bash
./gradlew clean build
```

### 3. Run the Application

```bash
./gradlew bootRun
```

The application will start on: `https://localhost:8443`

## Testing the API

### Using cURL
```bash
curl -k -X POST https://localhost:8443/user/trainee/sign-in \
  -H "Content-Type: application/json" \
  -d '{"username":"trainee","password":"password123"}'
```

### Using Postman
1. Create a POST request to `https://localhost:8443/user/trainee/sign-in`
2. Set the body to JSON with username and password
3. In SSL verification settings, disable "SSL certificate verification" (for development)
4. Send the request

### Using Java HttpClient
```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://localhost:8443/user/trainee/sign-in"))
    .POST(HttpRequest.BodyPublishers.ofString(
        "{\"username\":\"trainee\",\"password\":\"password123\"}"))
    .header("Content-Type", "application/json")
    .build();
HttpResponse<String> response = client.send(request, 
    HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
```

## HTTP Status Codes

| Status Code | Scenario |
|-------------|----------|
| 200 OK | Authentication successful |
| 400 Bad Request | Validation failed (invalid input format) |
| 401 Unauthorized | Invalid credentials |
| 500 Internal Server Error | Server error |

## Configuration

The HTTPS configuration is in `src/main/resources/application.properties`:

```properties
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=changeit
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
server.http2.enabled=true
```

## File Structure

```
src/main/java/com/renukiran/
├── controllers/
│   └── TraineeSignInController.java    # API endpoint controller
├── dto/
│   ├── TraineeSignInRequest.java       # Request DTO with validation
│   └── TraineeSignInResponse.java      # Response DTO
├── service/
│   └── TraineeAuthService.java         # Authentication service
├── exception/
│   ├── AuthenticationException.java    # Custom authentication exception
│   └── GlobalExceptionHandler.java     # Global error handler
├── config/
│   └── WebConfig.java                  # Web configuration
└── RenukiranServicesApplication.java   # Main application class

src/main/resources/
├── application.properties               # Application configuration
└── keystore.p12                        # SSL certificate (generated)
```

## Security Considerations

1. **Development vs Production**:
   - The self-signed certificate is suitable for development only
   - For production, use a valid SSL certificate from a trusted CA

2. **Credential Validation**:
   - The `TraineeAuthService` currently uses placeholder credentials
   - Replace the `isValidCredentials()` method with actual database lookup or authentication service

3. **Token Generation**:
   - The current token generation uses UUID
   - For production, implement proper JWT token generation with expiration

4. **HTTPS Enforcement**:
   - All connections are forced to use HTTPS on port 8443
   - HTTP connections are not accepted

## Future Enhancements

1. Implement JWT token generation with expiration
2. Add JWT token validation middleware
3. Integrate with database for credential verification
4. Add rate limiting for sign-in attempts
5. Implement refresh token mechanism
6. Add logging and audit trail
7. Support for LDAP/Active Directory integration
8. Two-factor authentication support

## Dependencies

- Spring Boot 3.2.0
- Spring Web
- Spring Validation
- Jakarta Validation API

## Notes

- The default test credentials are: username: `trainee`, password: `password123`
- These should be changed for production use
- The keystore password is set to `changeit` by default - change this in production

