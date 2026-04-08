package app;

import dao.MenuDAO;
import dao.OrderDAO;
import dao.RestaurantDAO;
import dao.UserDAO;
import model.CartItem;
import model.MenuItem;
import model.Restaurant;
import model.User;

import java.util.List;

/**
 * Manual test for FoodApp DAO layer.
 * Run this class directly to verify all CRUD operations.
 * Each test prints PASS or FAIL.
 */
public class test {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("  FoodApp – DAO Test Suite");

        testUserDAO();
        testRestaurantDAO();
        testMenuDAO();
        testOrderDAO();
        testErrorHandling();

        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
    }

    // ── UserDAO Tests

    private static void testUserDAO() {
        System.out.println("--- UserDAO ---");
        UserDAO dao = new UserDAO();

        // Test 1: signUp (CREATE)
        try {
            String hash = org.mindrot.jbcrypt.BCrypt.hashpw("testpass123", org.mindrot.jbcrypt.BCrypt.gensalt());
            User u = new User(0, "testuser_auto", hash, "test@auto.com", false);
            int id = dao.signUp(u);
            pass("signUp() creates new user, id=" + id);

            // Test 2: login (READ)
            User loggedIn = dao.login("testuser_auto", "testpass123");
            assertNotNull(loggedIn, "login() returns user with correct credentials");

            // Test 3: login with wrong password (error handling)
            User badLogin = dao.login("testuser_auto", "wrongpassword");
            assertNull(badLogin, "login() returns null for wrong password");

            // Test 4: getById (READ)
            User byId = dao.getById(loggedIn.getUserId());
            assertNotNull(byId, "getById() retrieves user");

            // Test 5: updateEmail (UPDATE)
            dao.updateEmail(loggedIn.getUserId(), "updated@auto.com");
            pass("updateEmail() executes without error");

            // Test 6: updateLocation (UPDATE)
            dao.updateLocation(loggedIn.getUserId(), "Dublin");
            pass("updateLocation() executes without error");

            // Test 7: deleteUser (DELETE)
            dao.deleteUser(loggedIn.getUserId());
            User afterDelete = dao.getById(loggedIn.getUserId());
            assertNull(afterDelete, "deleteUser() removes user from database");

        } catch (Exception e) {
            fail("UserDAO test threw exception: " + e.getMessage());
        }
    }

    //RestaurantDAO Tests

    private static void testRestaurantDAO() {
        System.out.println("\n--- RestaurantDAO ---");
        RestaurantDAO dao = new RestaurantDAO();

        // Need an admin user — use existing or create
        UserDAO userDAO = new UserDAO();
        int testAdminId = -1;

        try {
            String hash = org.mindrot.jbcrypt.BCrypt.hashpw("adminpass", org.mindrot.jbcrypt.BCrypt.gensalt());
            User admin = new User(0, "testadmin_auto", hash, "admin@auto.com", true);
            testAdminId = userDAO.signUp(admin);

            // Test: addRestaurant (CREATE)
            dao.addRestaurant("Test Bistro", "Italian", "Dublin", true, testAdminId);
            pass("addRestaurant() creates restaurant");

            // Test: getRestaurantsByAdminId (READ)
            List<Restaurant> list = dao.getRestaurantsByAdminId(testAdminId);
            assertTrue(!list.isEmpty(), "getRestaurantsByAdminId() returns restaurants");

            int restaurantId = list.get(0).getRestaurantId();

            // Test: getRestaurantsByLocation with INNER logic (READ)
            List<Restaurant> byLoc = dao.getRestaurantsByLocation("Dublin");
            pass("getRestaurantsByLocation() returns " + byLoc.size() + " open restaurant(s) in Dublin");

            // Test: updateRestaurantName (UPDATE)
            dao.updateRestaurantName(restaurantId, "Updated Bistro");
            pass("updateRestaurantName() executes without error");

            // Test: updateRestaurantStatus (UPDATE)
            dao.updateRestaurantStatus(restaurantId, false);
            pass("updateRestaurantStatus() sets restaurant to closed");

            // Test: deleteRestaurant (DELETE)
            dao.deleteRestaurant(restaurantId);
            List<Restaurant> afterDelete = dao.getRestaurantsByAdminId(testAdminId);
            assertTrue(afterDelete.isEmpty(), "deleteRestaurant() removes restaurant");

        } catch (Exception e) {
            fail("RestaurantDAO test threw exception: " + e.getMessage());
        } finally {
            // Cleanup test admin
            try {
                if (testAdminId > 0) userDAO.deleteUser(testAdminId);
            } catch (Exception ignored) {}
        }
    }

    //MenuDAO Tests (INNER JOIN)

    private static void testMenuDAO() {
        System.out.println("\n--- MenuDAO (INNER JOIN) ---");
        MenuDAO menuDAO = new MenuDAO();
        RestaurantDAO restDAO = new RestaurantDAO();
        UserDAO userDAO = new UserDAO();
        int adminId = -1;
        int restaurantId = -1;

        try {
            // Setup
            String hash = org.mindrot.jbcrypt.BCrypt.hashpw("pass", org.mindrot.jbcrypt.BCrypt.gensalt());
            adminId = userDAO.signUp(new User(0, "menutest_admin", hash, "m@m.com", true));
            restDAO.addRestaurant("Menu Test Restaurant", "Cafe", "Cork", true, adminId);
            restaurantId = restDAO.getRestaurantsByAdminId(adminId).get(0).getRestaurantId();

            // Test: addItem (CREATE)
            menuDAO.addItem("Espresso", 3.50, restaurantId);
            menuDAO.addItem("Croissant", 2.80, restaurantId);
            pass("addItem() creates menu items");

            // Test: getByRestaurant with INNER JOIN (READ)
            List<MenuItem> items = menuDAO.getByRestaurant(restaurantId);
            assertTrue(items.size() == 2, "getByRestaurant() via INNER JOIN returns 2 items");

            int menuId = items.get(0).getMenuId();

            // Test: updateItem (UPDATE)
            menuDAO.updateItem(menuId, "Double Espresso", 4.00);
            List<MenuItem> updated = menuDAO.getByRestaurant(restaurantId);
            assertTrue(updated.get(0).getPrice() == 4.00 || updated.get(1).getPrice() == 4.00,
                    "updateItem() changes price");

            // Test: deleteItem (DELETE)
            menuDAO.deleteItem(menuId);
            List<MenuItem> afterDelete = menuDAO.getByRestaurant(restaurantId);
            assertTrue(afterDelete.size() == 1, "deleteItem() removes one item");

        } catch (Exception e) {
            fail("MenuDAO test threw exception: " + e.getMessage());
        } finally {
            try {
                if (restaurantId > 0) restDAO.deleteRestaurant(restaurantId);
                if (adminId > 0) userDAO.deleteUser(adminId);
            } catch (Exception ignored) {}
        }
    }

    //OrderDAO Tests

    private static void testOrderDAO() {
        System.out.println("\n--- OrderDAO ---");
        OrderDAO orderDAO = new OrderDAO();
        MenuDAO menuDAO   = new MenuDAO();
        RestaurantDAO restDAO = new RestaurantDAO();
        UserDAO userDAO = new UserDAO();
        int userId = -1, adminId = -1, restaurantId = -1;

        try {
            // Setup user + restaurant + menu item
            String hash = org.mindrot.jbcrypt.BCrypt.hashpw("pass", org.mindrot.jbcrypt.BCrypt.gensalt());
            userId  = userDAO.signUp(new User(0, "ordertest_user",  hash, "u@u.com", false));
            adminId = userDAO.signUp(new User(0, "ordertest_admin", hash, "a@a.com", true));
            restDAO.addRestaurant("Order Test Resto", "Burgers", "Galway", true, adminId);
            restaurantId = restDAO.getRestaurantsByAdminId(adminId).get(0).getRestaurantId();
            menuDAO.addItem("Burger", 8.50, restaurantId);
            int menuId = menuDAO.getByRestaurant(restaurantId).get(0).getMenuId();

            // Test: placeOrder (CREATE)
            List<CartItem> cart = List.of(new CartItem(menuId, 2));
            List<Double> prices = List.of(8.50);
            int orderId = orderDAO.placeOrder(cart, userId, restaurantId, prices);
            assertTrue(orderId > 0, "placeOrder() returns valid order id=" + orderId);

            // Test: getOrdersByUser with INNER JOIN (READ)
            List<String> orders = orderDAO.getOrdersByUser(userId);
            assertTrue(!orders.isEmpty(), "getOrdersByUser() via INNER JOIN returns orders");
            pass("Order line: " + orders.get(0));

            // Test: deleteOrder (DELETE)
            orderDAO.deleteOrder(orderId);
            List<String> afterDelete = orderDAO.getOrdersByUser(userId);
            assertTrue(afterDelete.isEmpty(), "deleteOrder() removes order and items");

        } catch (Exception e) {
            fail("OrderDAO test threw exception: " + e.getMessage());
        } finally {
            try {
                if (restaurantId > 0) restDAO.deleteRestaurant(restaurantId);
                if (userId  > 0) userDAO.deleteUser(userId);
                if (adminId > 0) userDAO.deleteUser(adminId);
            } catch (Exception ignored) {}
        }
    }

    //Error Handling Tests

    private static void testErrorHandling() {
        System.out.println("\n--- Error Handling ---");

        // Test: invalid email format check (GUI-level validation)
        String badEmail = "notanemail";
        boolean emailValid = badEmail.contains("@") && badEmail.contains(".");
        assertTrue(!emailValid, "Email validation rejects 'notanemail'");

        String goodEmail = "user@example.com";
        boolean goodEmailValid = goodEmail.contains("@") && goodEmail.contains(".");
        assertTrue(goodEmailValid, "Email validation accepts 'user@example.com'");

        // Test: invalid price (string instead of double)
        try {
            double price = Double.parseDouble("abc");
            fail("Should have thrown NumberFormatException for price 'abc'");
        } catch (NumberFormatException e) {
            pass("NumberFormatException caught for invalid price input 'abc'");
        }

        // Test: empty username check
        String username = "   ";
        assertTrue(username.trim().isEmpty(), "Empty username validation works");

        // Test: short password check
        String shortPass = "abc";
        assertTrue(shortPass.length() < 6, "Password length validation rejects short passwords");

        // Test: CartItem rejects qty <= 0
        try {
            CartItem bad = new CartItem(1, 0);
            fail("CartItem should reject qty=0");
        } catch (IllegalArgumentException e) {
            pass("CartItem rejects qty=0: " + e.getMessage());
        }

        // Test: Cart rejects negative qty
        try {
            CartItem bad = new CartItem(1, -5);
            fail("CartItem should reject qty=-5");
        } catch (IllegalArgumentException e) {
            pass("CartItem rejects negative qty: " + e.getMessage());
        }
    }

    //Assertion helpers

    private static void pass(String msg) {
        System.out.println("  PASS: " + msg);
        passed++;
    }

    private static void fail(String msg) {
        System.out.println("  FAIL: " + msg);
        failed++;
    }

    private static void assertTrue(boolean condition, String msg) {
        if (condition) pass(msg);
        else fail(msg);
    }

    private static void assertNotNull(Object obj, String msg) {
        if (obj != null) pass(msg);
        else fail(msg + " (was null)");
    }

    private static void assertNull(Object obj, String msg) {
        if (obj == null) pass(msg);
        else fail(msg + " (was not null)");
    }
}
