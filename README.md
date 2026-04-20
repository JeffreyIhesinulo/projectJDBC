# FoodApp

A Java Swing desktop application for managing food orders.  
Built for the Object Oriented Software Development CA3 — South East Technological University.

---

## Description

FoodApp simulates a food delivery platform where customers can browse restaurants in their area, view menus, place orders and manage their account. Admin users can manage restaurants and their menus.

---

## Features

- User registration and login with BCrypt password hashing
- Role-based access — regular users and admins
- Browse open restaurants filtered by location
- View restaurant menus loaded from the database
- Add items to cart and place orders (saved to DB)
- View and delete order history
- Update profile — email, location, password
- Delete account with password confirmation
- Admin: add, edit, delete restaurants and dishes

---

## Tech Stack

| Layer      | Technology              |
|------------|-------------------------|
| Language   | Java 17                 |
| GUI        | Java Swing              |
| Database   | MySQL 8                 |
| Connector  | MySQL Connector/J 9.0.0 |
| Security   | BCrypt (jbcrypt 0.4)    |
| Build tool | Maven 3.8+              |

---

## Database Schema

```
user         — stores customer and admin accounts
restaurant   — restaurants owned by admin users
menu         — dishes belonging to a restaurant
orders       — customer orders (linked to user + restaurant)
order_items  — line items within an order (linked to orders + menu)
```

**Key relationships:**
- `user` 1 → N `restaurant` (admin manages restaurants)
- `restaurant` 1 → N `menu` (restaurant has many dishes)
- `user` 1 → N `orders` (user places many orders)
- `orders` 1 → N `order_items` (order has many line items)
- `menu` 1 → N `order_items` (dish appears in many orders)

---

## Prerequisites

- Java 17 or later
- MySQL 8.x running locally
- Maven 3.8+

---

## Setup

**1. Clone the repository**
```bash
git clone <your-repo-url>
cd projectJDBC
```

**2. Import the database**
```bash
mysql -u root -p < foodApp.sql
```

**3. Update database credentials**

Edit `src/main/java/util/DB.java`:
```java
private static final String URL  = "jdbc:mysql://localhost:3306/foodApp";
private static final String USER = "root";
private static final String PASS = "your_password";
```

**4. Build the project**
```bash
mvn package
```

**5. Run the application**
```bash
java -jar target/FoodApp.jar
```

---

## Running the Tests

```bash
# Run the test suite directly from your IDE
# or via Maven:
mvn exec:java -Dexec.mainClass=app.test
```

The test suite covers all DAO operations (UserDAO, RestaurantDAO, MenuDAO, OrderDAO) and error handling cases. Each test creates its own data, verifies the result and cleans up afterwards.

---

## Project Structure

```
src/main/java/
├── app/
│   ├── LoginPage.java          — Login and Register screens
│   ├── MainScreen.java         — Main screen with restaurant list
│   ├── RestaurantMenuScreen.java — Menu viewer + cart + order placement
│   ├── MyOrders.java           — Order history (Read + Delete)
│   ├── MyRestaurants.java      — Admin restaurant management (CRUD)
│   ├── UpdateProfile.java      — Profile editor + Delete account
│   ├── BackgroundPanel.java    — Custom background image panel
│   ├── Session.java            — Stores the currently logged-in user
│   └── test.java               — Manual test suite
├── dao/
│   ├── UserDAO.java            — CRUD for users
│   ├── RestaurantDAO.java      — CRUD for restaurants
│   ├── MenuDAO.java            — CRUD for menu items (INNER JOIN)
│   └── OrderDAO.java           — CRUD for orders (transaction + INNER JOIN)
├── model/
│   ├── User.java
│   ├── Restaurant.java
│   ├── MenuItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Cart.java
│   └── CartItem.java
└── util/
    └── DB.java                 — Database connection utility
```

---

## Default Admin Account

After importing the database dump you can log in with:

```
Username: admin
Password: 1234
```

Or register a new account and set `role = 1` in the database to make it an admin.

---

## CRUD Operations

| Operation | Entity       | Screen                    |
|-----------|-------------|---------------------------|
| Create    | User         | Login → Register tab      |
| Create    | Restaurant   | My Restaurants → Add      |
| Create    | Menu item    | Restaurant Menu → Add Dish |
| Create    | Order        | Restaurant Menu → Place Order |
| Read      | Restaurants  | Main Screen → Restaurants Near Me |
| Read      | Menu items   | Restaurant Menu Screen    |
| Read      | Orders       | My Orders                 |
| Update    | Profile      | Update Profile → Save Changes |
| Update    | Restaurant   | My Restaurants → Edit     |
| Update    | Menu item    | Restaurant Menu → Edit Dish |
| Update    | Status       | My Restaurants → Toggle Status |
| Delete    | User         | Update Profile → Delete Account |
| Delete    | Restaurant   | My Restaurants → Delete   |
| Delete    | Menu item    | Restaurant Menu → Delete Dish |
| Delete    | Order        | My Orders → Delete Order  |

---

## License

This project was developed for academic purposes at SETU.
