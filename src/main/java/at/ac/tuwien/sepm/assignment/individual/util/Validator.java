package at.ac.tuwien.sepm.assignment.individual.util;

import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import org.springframework.stereotype.Component;

@Component
public class Validator {



    public void validateNewOwner(Owner owner) throws ValidationException {
        if (owner.getName() == null) {
            throw new ValidationException("Field name must not be empty");
        }
        if (owner.getCreatedAt() != null || owner.getUpdatedAt() != null) {
            throw new ValidationException("Fields createdAt and updatedAt can't be set manually");
        }
    }

    public void validateUpdateOwner(Owner owner) throws ValidationException {
    }
}
