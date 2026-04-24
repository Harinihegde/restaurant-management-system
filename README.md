# 🍽️ Restaurant Management System

A comprehensive web-based Restaurant Management System built with **Java Spring Boot** implementing **MVC Architecture** and multiple **Design Patterns** as part of the OOAD (Object-Oriented Analysis and Design) course project.

---

## 📋 Project Overview

The Restaurant Management System streamlines restaurant operations by managing:
- Order processing with multiple discount strategies
- Multi-channel payment processing (Cash, Card)
- Table reservation system with automated notifications
- Customer feedback and loyalty management
- Admin dashboard for menu and inventory control

---

## 🛠️ Technology Stack

- **Backend:** Java 17, Spring Boot 3.2.0
- **Frontend:** Thymeleaf, HTML5, Bootstrap 5
- **Build Tool:** Maven
- **Architecture:** MVC (Model-View-Controller)

---

## 🎯 Design Patterns Implemented

### 1. **Singleton Pattern** (Creational) - `AdminController`
- **Implementation:** `DatabaseConnection.getInstance()`
- **Purpose:** Ensures single instance of database connection
- **Benefit:** Prevents resource wastage and ensures consistent state

### 2. **Factory Pattern** (Creational) - `OrderController`
- **Implementation:** `PaymentFactory.createPayment()`
- **Purpose:** Creates payment objects (Cash/Card) without exposing creation logic
- **Benefit:** Easy to add new payment types without modifying existing code

### 3. **Command Pattern** (Behavioral) - `ReservationController`
- **Implementation:** `ReservationCommand` interface with `EmailNotificationCommand` and `SMSNotificationCommand`
- **Purpose:** Encapsulates reservation notification requests as objects
- **Benefit:** Decouples sender from receiver, supports queuing and logging

### 4. **Facade Pattern** (Structural) - `CustomerController`
- **Implementation:** `CustomerFacade` provides unified interface
- **Purpose:** Simplifies complex subsystem interactions
- **Benefit:** Reduces coupling and provides clean API

---

## 📐 SOLID Principles Applied

- **Single Responsibility Principle (SRP):** Each controller handles one specific module
- **Open/Closed Principle (OCP):** Discount system extensible without modification
- **Dependency Inversion Principle (DIP):** Depends on abstractions, not concrete classes
- **Interface Segregation Principle (ISP):** Focused interfaces for specific needs

---

## 🚀 How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Steps

1. **Clone the repository:**
```bash
   git clone https://github.com/Harinihegde/restaurant-management-system.git
   cd restaurant-management-system
```

2. **Build the project:**
```bash
   mvn clean install
```

3. **Run the application:**
```bash
   mvn spring-boot:run
```

4. **Access the application:**
   Open browser and navigate to: `http://localhost:8080`

---

## 📂 Project Structure

```
restaurant-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── restaurant/
│   │   │           ├── RestaurantApplication.java       # Main Spring Boot entry point
│   │   │           └── controller/
│   │   │               ├── AdminController.java         # Admin & Menu (Singleton Pattern)
│   │   │               ├── OrderController.java         # Orders & Payments (Factory Pattern)
│   │   │               ├── ReservationController.java   # Reservations (Command Pattern)
│   │   │               └── CustomerController.java      # Customer Management (Facade Pattern)
│   │   └── resources/
│   │       ├── application.properties                   # Spring Boot configuration
│   │       └── templates/
│   │           ├── index.html                           # Home page
│   │           ├── admin-menu.html                      # Admin dashboard
│   │           ├── orders.html                          # Order management
│   │           ├── reservations.html                    # Reservation system
│   │           └── customer.html                        # Customer profiles
│   └── test/
│       └── java/                                        # Test files (if any)
├── pom.xml                                              # Maven dependencies & build config
├── .gitignore                                           # Git ignore rules
└── README.md                                            # Project documentation
```

---

## 👥 Team Members

| Name | Module | Pattern | Principle |
|------|--------|---------|-----------|
| **Padmaa KB** | Admin & Menu Management | Singleton | SRP |
| **Ishanvee Amit Sinha** | Table Reservations | Command | DIP |
| **Kaparthy Reddy** | Customer Management | Facade | ISP |
| **Harini Hegde** | Order & Payment Processing | Factory | OCP |

---

## ✨ Features

### 🔐 Admin Module
- Menu item management (Add, Edit, View)
- Inventory tracking
- Staff management
- Database connection singleton

### 🛒 Order Module
- Create orders with item summary
- Apply discounts (Percentage, Flat)
- Search orders by status
- Factory-based payment processing

### 💳 Payment Processing
- Multiple payment types (Cash, Card)
- Factory Pattern for payment creation
- Payment status tracking

### 📅 Reservation Module
- Table booking system
- Guest management
- Automated notifications (Email, SMS)
- Command Pattern for notifications

### 👤 Customer Module
- Customer profile management
- Loyalty points tracking
- Membership tiers (Basic, Loyalty)
- Facade Pattern for simplified operations

---

## 🎓 Course Information

- **Course:** UE23CS352B - Object Oriented Analysis & Design
- **Semester:** 6th Semester, Section D
- **Institution:** PES University, Bangalore
- **Faculty:** Prof Sowmya
- **Academic Year:** January - May 2026

---

## 📜 License

This project is developed as part of academic coursework at PES University.

---

## 🤝 Contributing

This is an academic project. Contributions are not accepted.

---

## 📧 Contact

For queries related to this project, please contact the team members through PES University.

---

**⭐ If you found this project helpful, please give it a star!**
