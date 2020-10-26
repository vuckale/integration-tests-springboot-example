package at.ac.tuwien.sepm.assignment.individual.unit.persistence;

import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class OwnerDaoTestBase {

    @Autowired
    OwnerDao ownerDao;
}
