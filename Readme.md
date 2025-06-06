# 📇 Contact Manager - Version 2

A secure cloud-based contact management system that allows users to safely store, manage, and retrieve their contacts from anywhere. This version introduces a modern tech stack, cloud storage, and a fully responsive design to enhance user experience and performance.

---

## 🔗 Previous Version (v1)

- **Repository:** [Contact Manager v1 - GitHub](https://github.com/AayushkumarPathak/Contact-Manager)  
  *(Replace with the actual link to your previous version's repo)*

---

## 🚀 What's New in Version 2

| Feature | Version 1 | Version 2 |
|--------|-----------|-----------|
| **Backend Framework** | Java Spring Boot | Java Spring Boot |
| **Frontend** | Thymeleaf | React.js (with Axios) |
| **Authentication** | Spring Security + Google OAuth2 | Spring Security (JWT-based or session-based) |
| **Cloud Storage** | ❌ | ✅ AWS S3 for profile images |
| **API Architecture** | REST Controller | Improved REST API for frontend communication |
| **UI Responsiveness** | Thymeleaf-based responsive design | Fully responsive SPA using React |
| **Profile Image Support** | ❌ | ✅ Upload and retrieve profile pictures |
| **Frontend-Backend Communication** | Direct rendering with Thymeleaf | API-based using Axios |
| **Security** | Basic | Enhanced data privacy with secure token/session-based access |

---

## 🛠️ Tech Stack

### 🔧 Backend:
- **Java Spring Boot**
- **Spring Security** (Authentication & Authorization)
- **MySQL** (Relational Database)
- **AWS S3** (Image Storage)
- **REST APIs**

### 🌐 Frontend:
- **React.js**
- **Axios** (For API communication)
- **Tailwind CSS / Bootstrap** *(Optional)*

---

## ✨ Features

- 🔐 **Secure Sign-Up and Login**
- 🧾 **Store and Manage Contacts Online**
- 🖼️ **Upload and Display Profile Images**
- ☁️ **AWS S3 Integration for Image Storage**
- 📲 **Responsive Frontend for All Devices**
- 🔁 **Real-Time Data Fetching using Axios**

---

## 🔧 Setup Instructions

### Prerequisites
- Node.js & npm
- Java 17+ and Maven
- MySQL
- AWS S3 account with access keys

### Backend (Spring Boot)
1. Clone the repo and navigate to the backend directory.
2. Configure `application.properties` with your database and AWS credentials.
3. Run the Spring Boot application using:
   ```bash
   mvn spring-boot:run
