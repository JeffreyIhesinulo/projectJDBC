package Model;

public class User {
    private  int userId;
    private String userName;
    private String userEmail;

    public User(int userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setEmail(String email) {
        this.userEmail = email;
    }

    @Override
    public String toString() {
        return
                "Id: " + userId +
                " | Name: " + userName +
                " | email: " + userEmail
                ;
    }
}
