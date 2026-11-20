package service.branch;

import model.branch.Branch;
import java.util.ArrayList;
import java.util.List;

public class BranchManager {
    private final List<Branch> branches = new ArrayList<>();

    public void addBranch(Branch branch) {
        branches.add(branch);
    }

    public Branch findById(String id) {
        for (Branch branch : branches) {
            if (branch.getId().equalsIgnoreCase(id)) {
                return branch;
            }
        }
        return null;
    }

    public List<Branch> getBranches() {
        return branches;
    }
}
