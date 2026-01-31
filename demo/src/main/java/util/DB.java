package util;
import java.sql.*;

public class DB {
    private static final String url = "jdbc:mysql://localhost:3306/demo";
    private static final String username = "jeff";
    private static final String password = "StrongPass123!";

    private DB()
    {

    }


      public static Connection getConnection()  throws SQLException
    {
        return DriverManager.getConnection(url,username,password);
    }

}
