package model.user;

public class DeliveryAgent extends Person {
    private String branchId;

    public DeliveryAgent() {
        super();
    }

    public DeliveryAgent(String id, String name, String phone, String email,
                         String username, String password, String branchId) {
        super(id, name, phone, email, username, password);
        this.branchId = branchId;
    }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    @Override
    public String getRole() {
        return "AGENT";
    }
}
