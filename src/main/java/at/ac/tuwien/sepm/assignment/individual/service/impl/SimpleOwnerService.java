package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import at.ac.tuwien.sepm.assignment.individual.util.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.util.Validator;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class SimpleOwnerService implements OwnerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OwnerDao ownerDao;
    private final Validator validator;

    @Autowired
    public SimpleOwnerService(OwnerDao ownerDao, Validator validator) {
        this.ownerDao = ownerDao;
        this.validator = validator;
    }

    @Override
    public Owner findOneById(Long id) {
        LOGGER.trace("findOneById({})", id);
        return ownerDao.findOneById(id);
    }

    @Override
    public Owner add(Owner owner) throws ValidationException {
        LOGGER.trace("Service Layer: add(Owner owner)");
        validator.validateNewOwner(owner);
        try {
            return ownerDao.add(owner);
        } catch (DataAccessException e) {
            throw handleDataAccessException(("Problem while adding owner"), e);
        }
    }

    private RuntimeException handleDataAccessException(String errMsg, DataAccessException e) {
        LOGGER.error(errMsg, e);
        return new RuntimeException(errMsg, e);
    }

}
