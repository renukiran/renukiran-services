# renukiran-services

## Local Run

The backend runs on `http://localhost:8080` by default.

### Default profile

Without extra arguments, the backend uses the datasource configured in `src/main/resources/application.properties`.

### Local H2 profile

For local UI smoke tests and development, run the backend with the `local` profile:

```powershell
.\gradlew.bat bootRun --args='--spring.profiles.active=local'
```

The `local` profile uses a file-backed H2 database at `renukiran-services/data/renukiran-local`, so data now persists across backend restarts. Delete the `data/` folder if you want a clean reset.

This profile also seeds baseline development data only when records are missing, including the smoke-demo trainer account `Sanjay Malik`, the course `Beauty Intensive 264179`, and the batch `Beauty Morning 264179`.

### Prerequisites

- Java 17+

### Start

```powershell
.\gradlew.bat bootRun
```

### API docs

After startup, OpenAPI docs are available at `http://localhost:8080/v3/api-docs`.

# 🔐 Admin Authentication API Documentation

This document describes the authentication flow for **Admin users** in the system.

---

## 📌 Overview

* The `local` profile uses a **file-backed H2 database**.
* Local data is **preserved across server restarts** until you delete `renukiran-services/data/`.
* Authentication token support is **not yet implemented** (`token` is currently `null`).
* Development seed data is inserted **only when missing** (see below).

---

## 🧪 Temporary Admin Login (Auto-Seeded)

> ⚠️ **FOR DEVELOPMENT / TESTING ONLY.** These credentials are inserted automatically by `DataInitializer` when missing. Remove `DataInitializer.java` before deploying to production.

| Field    | Value                      |
| -------- | -------------------------- |
| Username | `TempAdmin`                |
| Password | `Admin@1234`               |
| Email    | `tempAdmin@renukiran.com`  |
| UserType | `ADMIN`                    |

### Use these credentials directly with the login endpoint

**Endpoint**

```
POST /auth/admin
```

**Request Body**

```json
{
  "userName": "TempAdmin",
  "password": "Admin@1234"
}
```

**Response**

```json
{
  "success": true,
  "message": "Admin login successful",
  "token": null,
  "statusCode": 200
}
```

You can also inspect the seeded data via the H2 console at **`http://localhost:8080/h2-console`** (JDBC URL: `jdbc:h2:file:./data/renukiran-local`, username: `sa`, password: blank).

---

## 1️⃣ Admin Login (User Not Registered)

### **Endpoint**

```
POST /auth/admin
```

### **Request Body**

```json
{
  "username": "JohnDoe@WF",
  "password": "admin123"
}
```

### **Response**

```json
{
  "success": false,
  "message": "User is not Registered!",
  "token": null,
  "statusCode": 404
}
```

### **Description**

If the admin user does not exist in the database, the system returns a **404 Not Found** response indicating that the user is not registered.

---

## 2️⃣ Register Admin

> ⚠️ This only applies when you are not using the seeded local H2 profile data.

### **Endpoint**

```
POST /api/v1/trainers
```

### **Request Body**

```json
{
  "userName": "Admin1",
  "password": "EncryptedPWD@",
  "email": "marunakiran@gmail.com",
  "phone": "+918008763441",
  "firstName": "Arunakiran",
  "lastName": "Batman",
  "skills": ["Coding"],
  "userType": "ADMIN"
}
```

### **Response**

```
Signup successful! Your account has been created.
```

### **Description**

This endpoint creates a new **Admin account** in the system.

---

## 3️⃣ Admin Login (Successful Case)

### **Endpoint**

```
POST /auth/admin
```

### **Request Body**

```json
{
  "userName": "Admin1",
  "password": "EncryptedPWD@"
}
```

### **Response**

```json
{
  "success": true,
  "message": "Admin login successful",
  "token": null,
  "statusCode": 200
}
```

### **Description**

If the credentials are valid, the system authenticates the admin and returns a successful response.

---

## 📎 Status Codes Summary

| Status Code | Meaning             |
| ----------- | ------------------- |
| 200         | Success             |
| 404         | User Not Registered |

---

# 📚 Courses API Documentation

This section covers all APIs related to **Course Management**.

---

## 1️⃣ Add Course

### **Endpoint**

```
POST /courses/add
```

### **Request Body**

```json
{
  "courseName": "Spring Boot",
  "instructor": "John Doe",
  "duration": 40
}
```

### **Response**

```json
{
  "id": 1,
  "courseName": "Spring Boot",
  "instructor": "John Doe",
  "duration": 40
}
```

### **Description**

Creates a new course in the repository.

---

## 2️⃣ Validation Error While Adding Course

### **Endpoint**

```
POST /courses/add
```

### **Request Body (Invalid)**

```json
{
  "coursename": "Spring Boot",
  "duration": 40
}
```

### **Response**

```json
{
  "status": 400,
  "message": "VALIDATION_FAILED",
  "errors": [
    "courseName: Course name is required"
  ]
}
```

### **Description**

Returned when required fields are missing or invalid.

---

## 3️⃣ Get All Courses

### **Endpoint**

```
GET /courses
```

### **Response**

```json
[
  {
    "id": 1,
    "courseName": "Spring Boot",
    "instructor": "John Doe",
    "duration": 40
  }
]
```

### **Description**

Fetches all courses from the repository.

---

## 4️⃣ Update Course

### **Endpoint**

```
PUT /courses/{id}
```

### **Request Body**

```json
{
  "courseName": "Spring Boot Advanced",
  "instructor": "John Doe",
  "duration": 60
}
```

### **Response**

```json
{
  "id": 1,
  "courseName": "Spring Boot Advanced",
  "instructor": "John Doe",
  "duration": 60
}
```

### **Description**

Updates an existing course using its ID.

---
