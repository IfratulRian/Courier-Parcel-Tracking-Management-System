package service.branch;

import exception.DuplicateIdException;
import exception.RecordNotFoundException;
import model.branch.Branch;
import utility.IDGenerator;
import java.util.List;

public class BranchService {
    private final BranchManager branchManager;

    public BranchService(BranchManager branchManager) {
        this.branchManager = branchManager;
    }

    public Branch registerBranch(String name, String address, String city, String phone)
            throws DuplicateIdException {
        String id = IDGenerator.generateBranchId();
        for (Branch branch : branchManager.getBranches()) {
            if (branch.getName().equalsIgnoreCase(name) && branch.getCity().equalsIgnoreCase(city)) {
                throw new DuplicateIdException("A branch with this name and city already exists.");
            }
        }
        Branch branch = new Branch(id, name, address, city, phone);
        branchManager.addBranch(branch);
        return branch;
    }

    public Branch getBranchById(String id) throws RecordNotFoundException {
        Branch branch = branchManager.findById(id);
        if (branch == null) {
            throw new RecordNotFoundException("Branch not found.");
        }
        return branch;
    }

    public List<Branch> getAllBranches() {
        return branchManager.getBranches();
    }

    public void setActive(String branchId, boolean active) throws RecordNotFoundException {
        getBranchById(branchId).setActive(active);
    }
}
