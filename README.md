# 📝 Blog Post Website

A full-featured personal blogging platform built with **Spring Boot**, **Thymeleaf**, and **Bootstrap**. This project allows users to register, log in, create blog posts with images, and manage their profile—all through a clean and responsive UI.

---

## 🚀 Features

- 🔐 **Authentication**
  - User registration and secure login
  - Password reset using email/token
  - Password update in profile

- ✍️ **Post Management**
  - Create, edit, and delete blog posts
  - Upload and embed images in posts
  - Proper text formatting using `white-space: pre-wrap`

- 👤 **User Profile**
  - View and update profile info
  - Password change functionality

- 🎨 **Frontend**
  - Cream-colored modern UI
  - Responsive layout with Bootstrap
  - Custom CSS styling for card layouts and form design

---

## 🧑‍💻 Tech Stack

- **Backend:** Spring Boot, Spring MVC, Spring Security
- **Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap
- **Database:** JPA + Hibernate (MySQL or H2)
- **Build Tool:** Maven
- **IDE:** IntelliJ IDEA / VS Code

---

## 📁 Project Structure

src/
├── main/
│ ├── java/
│ │ └── org.sumit.blog/ # Controllers, Services, Models
│ ├── resources/
│ │ ├── templates/ # Thymeleaf HTML pages
│ │ ├── static/ # CSS, JS, images
│ │ └── application.properties
└── pom.xml
