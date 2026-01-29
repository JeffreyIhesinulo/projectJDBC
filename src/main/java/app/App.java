package app;

import dao.MenuDAO;
import model.Cart;
import model.CartItem;
import dao.OrderDAO;

import java.util.ArrayList;
import java.util.List;


public class App {
	public static void main(String[] args) throws Exception 
	{
        MenuDAO menuDao = new MenuDAO();
        menuDao.findAll().forEach(System.out::println);
		
	}

}
