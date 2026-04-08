package model;

public class User {
	
	private int id;
	private String username;
	private String password_hash;
    private String email;
    private boolean role;
    private String location;
	
	public User(int id, String username, String password_hash, String email, boolean role, String location)
	{
		this.id = id;
		this.username = username;
		this.password_hash = password_hash;
        this.email = email;
        this.role = role;
        this.location = location;
	}
    public User(int id,String username, String password_hash, String email, boolean role)
    {
        this.username = username;
        this.password_hash = password_hash;
        this.email = email;
        this.role = role;
        this.id = id;
    }
    public User(){}


	public int getId() {
		return id;
	}

	public String getUserName() {
		return username;
	}

	public String getUserPasswordHash() {
		return password_hash;
	}
    public String getUserEmail() {
        return email;
    }
    public boolean isAdmin() {
        return role;
    }
    public String getUserLocation() {
        return location;
    }
    public boolean getRole() {
        return role;
    }
    public int getUserId() {
        return id;
    }

    public void setUserPasswordHash(String password_hash) {
        this.password_hash = password_hash;
    }
    public void setUserId(int userId) {
        this.id = userId;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setUserName(String username) {
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(boolean role) {
        this.role = role;
    }

    public void setUserLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password_hash + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
