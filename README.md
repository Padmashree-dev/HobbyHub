# 🎯 HobbyConnect – A Platform for Connecting Hobbyists

A full-stack Java Spring Boot web application where users can connect with others who share similar hobbies.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java Spring Boot 4.1.2 |
| Frontend | HTML, CSS, JavaScript, Bootstrap 5 |
| Database | MySQL / XAMPP MariaDB |
| Template Engine | Thymeleaf |
| Build Tool | Maven |

---

## 📋 Prerequisites (Install These First)

1. **Java JDK 17 or higher** → https://adoptium.net
2. **XAMPP** (for MySQL) → https://www.apachefriends.org
3. **VS Code** → https://code.visualstudio.com

---

## ⚙️ Database Setup

1. Open **XAMPP Control Panel**
2. Click **Start** next to **MySQL**
3. Open browser → go to `http://localhost/phpmyadmin`
4. Click **New** → Create database named: `hobbyconnect_db`
5. Click **Create**

> ✅ The app will automatically create all tables when it starts!

---

## 🚀 How to Run

### Step 1 — Clone or Download the Project
```bash
git clone https://github.com/YOUR_USERNAME/hobbyconnect.git
cd hobbyconnect
```

### Step 2 — Start MySQL (XAMPP)
- Open XAMPP Control Panel
- Start MySQL

### Step 3 — Run the Application
```bash
# On Windows:
.\mvnw.cmd spring-boot:run

# On Mac/Linux:
./mvnw spring-boot:run
```

### Step 4 — Open in Browser
```
http://localhost:8080
```

---

## 👤 Demo Accounts

| Username | Password | Hobbies |
|----------|----------|---------|
| `alexr` | `password123` | Coding, Gaming |
| `maya_art` | `password123` | Drawing, Photography |
| `codingpro` | `password123` | Coding, Music |
| `dancequeen` | `password123` | Dancing, Music |
| `sportsfan` | `password123` | Sports, Gaming |
| `photomaster` | `password123` | Photography, Travel |

---

## 📄 Pages

| URL | Page |
|-----|------|
| `http://localhost:8080` | Home |
| `http://localhost:8080/register` | Register |
| `http://localhost:8080/login` | Login |
| `http://localhost:8080/hobbies` | Hobby Selection |
| `http://localhost:8080/friends` | Find Friends |
| `http://localhost:8080/communities` | Communities |
| `http://localhost:8080/profile` | My Profile |
| `http://localhost:8080/messages` | Messages |

---

## 🗂️ Project Structure

```
hobbyconnect/
├── src/main/java/com/hobbyconnect/
│   ├── HobbyConnectApplication.java   ← Main entry point
│   ├── config/DataInitializer.java    ← Seeds demo data
│   ├── controller/                    ← Handles web requests
│   ├── model/                         ← Database entities
│   ├── repository/                    ← Database queries
│   ├── service/                       ← Business logic
│   ├── dto/                           ← Data transfer objects
│   └── util/                          ← Helper classes
├── src/main/resources/
│   ├── templates/                     ← HTML pages (Thymeleaf)
│   ├── static/css/style.css           ← Purple/pink theme
│   ├── static/js/main.js              ← Frontend JavaScript
│   └── application.properties         ← App configuration
└── pom.xml                            ← Maven dependencies
```

---

## ❗ Troubleshooting

| Problem | Fix |
|---------|-----|
| `Java not found` | Install JDK 17+ and set JAVA_HOME |
| `Port 3306 error` | Start MySQL in XAMPP |
| `Database not found` | Create `hobbyconnect_db` in phpMyAdmin |
| `Port 8080 in use` | Stop other apps using port 8080 |
| `Build error` | Run `.\mvnw.cmd clean install` first |

---

## 🎨 Features

- ✅ User Registration & Login
- ✅ Select Multiple Hobbies
- ✅ Find Friends with Common Hobbies
- ✅ Create & Join Communities
- ✅ Community Discussion Board
- ✅ User Profile with Edit
- ✅ Simple Messaging System
- ✅ Responsive Design (Mobile Friendly)
- ✅ Purple/Pink Modern Theme
