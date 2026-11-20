package service.user;

import exception.DuplicateIdException;
import exception.RecordNotFoundException;
import model.user.Admin;
import model.user.Customer;
import model.user.DeliveryAgent;
import model.user.Person;
import utility.IDGenerator;

public class UserService {
    private final UserManager userManager;

    public UserService(UserManager userManager) {
        this.userManager = userManager;
    }

    public Customer registerCustomer(String name, String phone, String email,
                                     String username, String password, String address)
            throws DuplicateIdException {
        Customer existing = userManager.findCustomerByUsername(username);
        if (existing != null) {
            throw new DuplicateIdException("Username already exists.");
        }
        Customer customer = new Customer(IDGenerator.generateUserId("CUSTOMER"),
                name, phone, email, username, password, address);
        userManager.addUser(customer);
        return customer;
    }

    public Customer loginCustomer(String username, String password)
            throws RecordNotFoundException {
        Customer customer = userManager.findCustomerByUsername(username);
        if (customer == null || !customer.getPassword().equals(password) || !customer.isActive()) {
            throw new RecordNotFoundException("Invalid customer username or password.");
        }
        return customer;
    }

    public DeliveryAgent loginAgent(String username, String password)
            throws RecordNotFoundException {
        DeliveryAgent agent = userManager.findAgentByUsername(username);
        if (agent == null || !agent.getPassword().equals(password) || !agent.isActive()) {
            throw new RecordNotFoundException("Invalid agent username or password.");
        }
        return agent;
    }

    public boolean loginAdmin(Admin admin, String username, String password) {
        return admin.getUsername().equals(username) && admin.getPassword().equals(password);
    }

    public DeliveryAgent registerAgent(String name, String phone, String email,
                                       String username, String password, String branchId)
            throws DuplicateIdException {
        if (userManager.findAgentByUsername(username) != null) {
            throw new DuplicateIdException("Agent username already exists.");
        }
        DeliveryAgent agent = new DeliveryAgent(IDGenerator.generateUserId("AGENT"),
                name, phone, email, username, password, branchId);
        userManager.addUser(agent);
        return agent;
    }

    public boolean removeAgent(String agentId) throws RecordNotFoundException {
        DeliveryAgent agent = userManager.findAgentById(agentId);
        if (agent == null) {
            throw new RecordNotFoundException("Delivery agent not found.");
        }
        agent.setActive(false);
        return true;
    }

    public void updateCustomerProfile(Customer customer, String name, String phone,
                                      String email, String address) {
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);
    }
}
