package app;

import DAO.UserDAO;
import Model.User;

import java.sql.SQLException;

public class test {
    public static void main(String[] args)
    {

        UserDAO userDAO = new UserDAO();
        try{
            userDAO.deleteUser(50);
        }
        catch(Exception e){
            e.printStackTrace();
        }


        try {
            for(User u : userDAO.getAllUsers())
            {
                System.out.println(u);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }


//        User user = new User(1, "Hulio", "hulio@hui.com");
//        try{
//            userDAO.updateUser(user);
//        }
//        catch (SQLException e){
//            e.printStackTrace();
//        }



    }
}
