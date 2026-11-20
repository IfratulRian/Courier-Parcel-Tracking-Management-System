package model.user;

public class Customer extends Person {
    private String address;

    public Customer() {
        super();
    }

    public Customer(String id, String name, String phone, String email,
                    String username, String password, String address) {
        super(id, name, phone, email, username, password);
        this.address = address;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }
}
