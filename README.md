# ContactIQ — Smart Cloud Contact Manager

**ContactIQ** is a full-stack cloud contact management web application built with **Spring Boot**, **Spring Security**, **OAuth2**, **Thymeleaf**, **Tailwind CSS**, and **SQL Server**. It enables users to securely store, organize, search, and export personal and professional contacts from a clean, responsive dashboard.

---

## Key Features

### Authentication & Security
- **Multi-Provider Sign-In:** Register with email & password (BCrypt encrypted) or sign in seamlessly via **Google** and **GitHub OAuth2**.
- **Email & OTP Verification:** Account verification and password recovery powered by Spring Mail (SMTP) OTP flows.
- **Role-Based Access Control:** Protected user routes (`/user/**`) managed via Spring Security.

### Contact Management
- **Full CRUD Operations:** Add, view, update, and delete contacts with phone numbers, email addresses, physical addresses, LinkedIn URLs, and notes.
- **Cloud Image Hosting:** Upload and preview contact profile avatars and user profile pictures via **Cloudinary**.
- **Instant Multi-Field Search & Pagination:** Filter contacts by **Name**, **Email**, or **Phone Number** with server-side pagination.
- **Favorite Contact Pinning:** Star important contacts to pin them directly to your dashboard for quick access.
- **One-Click Excel Export:** Export your entire contact directory to an Excel spreadsheet for offline backups.

### Dashboard, Profile & Feedback
- **Personal Analytics Dashboard:** View total contacts, favorite counts, LinkedIn connections, account verification status, and recently added contacts.
- **Profile Completeness Tracker:** Dynamic progress bar calculating profile completion percentage with inline profile and password updates.
- **User Feedback System:** Interactive 5-star rating and feedback submission module with submission history and delete support.
- **Light & Dark Theme Support:** Persistent Light and Dark mode toggle across both public and authenticated views.

---

## Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java, Spring Boot, Spring MVC, Spring Data JPA / Hibernate |
| **Security** | Spring Security, OAuth2 Client (Google & GitHub), BCrypt |
| **Frontend** | Thymeleaf, Tailwind CSS, Flowbite, Vanilla JavaScript, Inline SVGs |
| **Database** | SQL Server |
| **Cloud & Integrations** | Cloudinary (Image Upload), JavaMailSender / SMTP (OTP & Contact Emails) |
| **Build Tool** | Maven |

---

## Project Structure

```text
src/
├── main/
│   ├── java/com/scm/
│   │   ├── config/          # Security, OAuth2, and Cloudinary configurations
│   │   ├── controllers/     # Page, Auth, User, and Contact MVC controllers
│   │   ├── entities/        # JPA entities (User, Contact, Feedback, SocialLink)
│   │   ├── forms/           # Form DTOs with Jakarta Bean Validation
│   │   ├── helper/          # Authentication, OTP, and session message helpers
│   │   ├── repositories/    # Spring Data JPA repositories
│   │   └── services/        # Service interfaces & implementations
│   └── resources/
│       ├── static/          # Compiled CSS (output.css), JS, and SVG assets
│       ├── templates/       # Thymeleaf layouts & views (public + user/*)
│       └── application.properties
└── pom.xml
```

---

## Local Setup & Installation

### 1. Prerequisites
- **Java 17+** (or JDK 21)
- **Maven 3.8+**
- **SQL Server**

### 2. Clone the Repository
```bash
git clone https://github.com/AmaanQureshi01/ContactIQ.git
cd ContactIQ
```

### 3. Configure Environment Variables
Create a SQL Server/MySQL database named `scm` (or configure your own URL), then set the following environment variables in your OS or IDE Run Configuration:

```env
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=SmartContactManager;encrypt=true;trustServerCertificate=true
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password

CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
```

### 4. Run the Application
```bash
mvn spring-boot:run
```
Once started, open your browser and visit:
```text
http://localhost:8080/home
```

---

## Author

**Amaan Qureshi** — Full Stack Java Developer
- **GitHub:** [@AmaanQureshi01](https://github.com/AmaanQureshi01)
- **LinkedIn:** [Amaan Qureshi](https://www.linkedin.com/in/amaan-qureshi-01sep2003)
