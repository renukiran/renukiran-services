# 🔐 Admin Authentication API Documentation

This document describes the authentication flow for **Admin users** in the system.

---

## 📌 Overview

* The application uses an **in-memory (runtime) database**.
* All registered users are **lost after server restart**.
* Authentication token support is **not yet implemented** (`token` is currently `null`).

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

> ⚠️ Since the application uses a runtime database, you must register the admin before attempting login.

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
