package model.user;

public class Admin extends Person {
    public Admin(String id, String name, String username, String password) {
        super(id, name, "", "", username, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
