package service.user;

import model.user.Admin;
import model.user.Customer;
import model.user.DeliveryAgent;
import model.user.Person;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private final List<Person> users = new ArrayList<>();

    public void addUser(Person person) {
        users.add(person);
    }

    public void removeUser(String id) {
        users.removeIf(person -> person.getId().equals(id));
    }

    public List<Person> getUsers() {
        return users;
    }

    public Person findById(String id) {
        for (Person person : users) {
            if (person.getId().equalsIgnoreCase(id)) {
                return person;
            }
        }
        return null;
    }

    public Customer findCustomerByUsername(String username) {
        for (Person person : users) {
            if (person instanceof Customer && person.getUsername().equals(username)) {
                return (Customer) person;
            }
        }
        return null;
    }

    public DeliveryAgent findAgentById(String id) {
        for (Person person : users) {
            if (person instanceof DeliveryAgent &&
                    person.getId().equalsIgnoreCase(id)) {
                return (DeliveryAgent) person;
            }
        }
        return null;
    }

    public DeliveryAgent findAgentByUsername(String username) {
        for (Person person : users) {
            if (person instanceof DeliveryAgent &&
                    person.getUsername().equalsIgnoreCase(username)) {
                return (DeliveryAgent) person;
            }
        }
        return null;
    }

    public List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (Person person : users) {
            if (person instanceof Customer) {
                customers.add((Customer) person);
            }
        }
        return customers;
    }

    public List<DeliveryAgent> getAgents() {
        List<DeliveryAgent> agents = new ArrayList<>();
        for (Person person : users) {
            if (person instanceof DeliveryAgent) {
                agents.add((DeliveryAgent) person);
            }
        }
        return agents;
    }
}
