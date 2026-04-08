package model;

public class Restaurant {
    private int    restaurantId;
    private String restaurantName;
    private String restaurantLocation;
    private boolean restaurantStatus;
    private String restaurantType;
    private int    adminId;

    public Restaurant(String restaurantName, String restaurantLocation,
                      boolean restaurantStatus, String restaurantType,
                      int adminId, int restaurantId) {
        this.restaurantId       = restaurantId;
        this.restaurantName     = restaurantName;
        this.restaurantLocation = restaurantLocation;
        this.restaurantStatus   = restaurantStatus;
        this.restaurantType     = restaurantType;
        this.adminId            = adminId;
    }

    public Restaurant(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Restaurant() {}

    // Setters
    public void setRestaurantId(int restaurantId)           { this.restaurantId = restaurantId; }
    public void setRestaurantName(String restaurantName)    { this.restaurantName = restaurantName; }
    public void setRestaurantLocation(String loc)           { this.restaurantLocation = loc; }
    public void setRestaurantStatus(boolean status)         { this.restaurantStatus = status; }
    public void setRestaurantType(String type)              { this.restaurantType = type; }
    public void setAdminId(int adminId)                     { this.adminId = adminId; }

    // Getters
    public int     getRestaurantId()       { return restaurantId; }
    public String  getRestaurantName()     { return restaurantName; }
    public String  getRestaurantLocation() { return restaurantLocation; }
    public boolean isRestaurantStatus()    { return restaurantStatus; }
    public String  getRestaurantType()     { return restaurantType; }
    public int     getAdminId()            { return adminId; }

    @Override
    public String toString() {
        return restaurantName + "  |  " + restaurantType
             + "  |  " + restaurantLocation
             + "  |  " + (restaurantStatus ? "Open" : "Closed");
    }
}
