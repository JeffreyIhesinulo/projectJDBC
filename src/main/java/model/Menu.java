package model;

public class Menu {
    private int menu_id;
    private String dish;
    private double price;

    public Menu(int menu_id, String dish, double price) {
        this.menu_id = menu_id;
        this.dish = dish;
        this.price = price;
    }

    public int getMenuId() {
        return menu_id;
    }

    public String getDish() {
        return dish;
    }


    public double getPrice() {
        return price;
    }

    public String toString() {
        return "Menu{" +
                "menu_id=" + menu_id +
                ", dish='" + dish + '\'' +
                ", price=" + price +
                '}';


    }
}