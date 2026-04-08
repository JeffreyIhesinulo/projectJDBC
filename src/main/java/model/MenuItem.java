package model;

public class MenuItem {
    private int dish_id;
    private String dish_name;
    private double price;

    public MenuItem(int dish_id, String dish_name, double price)
    {
        this.dish_id = dish_id;
        this.dish_name = dish_name;
        this.price = price;
    }
    public MenuItem(int dish_id, String dish_name)
    {
        this.dish_id = dish_id;
        this.dish_name = dish_name;
    }

    public int getMenuId() {
        return dish_id;
    }

    public String getDish() {
        return dish_name;
    }

    public double getPrice() {
        return price;
    }

    public String toString() {
        return "MenuItem{" +
                "menu_id=" + dish_id +
                ", dish='" + dish_name + '\'' +
                ", price=" + price +
                '}';
    }
}