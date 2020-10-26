package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.util.ValidationException;

public interface OwnerService {


    /**
     * @param id of the owner to find.
     * @return the owner with the specified id.
     * @throws RuntimeException  will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the owner could not be found in the system.
     */
    Owner findOneById(Long id);

    /**
     * @param owner owner object to add to DB
     * @return owner entity that was successfully added to DB
     * This function validates @params and  passed them to Data Access Layer
     */
    Owner add(Owner owner);

}
