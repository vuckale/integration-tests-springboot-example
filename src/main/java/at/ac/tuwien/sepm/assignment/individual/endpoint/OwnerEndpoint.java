package at.ac.tuwien.sepm.assignment.individual.endpoint;

import at.ac.tuwien.sepm.assignment.individual.endpoint.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.endpoint.mapper.OwnerMapper;
import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import at.ac.tuwien.sepm.assignment.individual.util.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(OwnerEndpoint.BASE_URL)
public class OwnerEndpoint {

    static final String BASE_URL = "/owners";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OwnerService ownerService;
    private final OwnerMapper ownerMapper;

    @Autowired
    public OwnerEndpoint(OwnerService ownerService, OwnerMapper ownerMapper) {
        this.ownerService = ownerService;
        this.ownerMapper = ownerMapper;
    }

    @GetMapping(value = "/{id}")
    public OwnerDto getOneById(@PathVariable("id") Long id) {
        LOGGER.info("GET " + BASE_URL + "/{}", id);
        try {
            return ownerMapper.entityToDto(ownerService.findOneById(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading owner", e);
        }
    }

    @PostMapping(value = "add")
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerDto add(@RequestBody OwnerDto ownerDto) {
        LOGGER.info("POST " + BASE_URL + "/add");
        LOGGER.trace("POST " + BASE_URL + "/add");
        LOGGER.debug("POST " + BASE_URL + "/add owner={}", ownerDto);
        String ADD_ERROR = "Error during adding owner";
        try {
            Owner ownerEntity = ownerMapper.dtoToEntity(ownerDto);
            return ownerMapper.entityToDto(ownerService.add(ownerEntity));
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                ADD_ERROR + ": " + e.getMessage());
        }
    }
}
