ADMIN AUTHENTICATION API DOCUMENTATION

1. ADMIN LOGIN (USER NOT REGISTERED)

Endpoint: POST /auth/admin

Request Body: { “username”: "JohnDoe@WF”, “password”:
“admin123” }

Response: { “success”: false, “message”: “User is not Registered!”,
“token”: null, “statusCode”: 404 }

Description: If the admin user does not exist in the database, the
system returns a 404 response indicating that the user is not
registered.

2. REGISTER ADMIN (USING TRAINER SIGNUP ENDPOINT)

Since the application uses a runtime database, you must first register
the admin user.

Endpoint: POST /api/v1/trainers

Request Body: { “userName”: “Admin1”, “password”: “EncryptedPWD@”,
“email”: “marunakiran@gmail.com”, “phone”: “+918008763441”, “firstName”:
“Arunakiran”, “lastName”: “Batman”, “skills”: [“Coding”], “userType”:
“ADMIN” }

Response: Signup successful! Your account has been created.

Description: This request creates a new admin account in the system.

3. ADMIN LOGIN (SUCCESSFUL CASE)

After the admin is registered, login can be performed.

Endpoint: POST /auth/admin

Request Body: { “userName”: “Admin1”, “password”: “EncryptedPWD@” }

Response: { “success”: true, “message”: “Admin login successful”,
“token”: null, “statusCode”: 200 }

Description: If the credentials are valid, the system authenticates the
admin and returns a successful response.

NOTES

-   The application currently uses a runtime (in-memory) database.
-   Registered users will be lost after server restart.
-   The ‘token’ field is currently null and reserved for future use
    (e.g., JWT authentication).