package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
public class OwnerJdbcDao implements OwnerDao {

    private static final String TABLE_NAME = "Owner";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public OwnerJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Owner findOneById(Long id) {
        LOGGER.trace("Get owner with id {}", id);
        final String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id=?";
        List<Owner> owners = jdbcTemplate.query(sql, new Object[]{id}, this::mapRow);

        if (owners.isEmpty()) throw new NotFoundException("Could not find owner with id " + id);

        return owners.get(0);
    }

    @Override
    public Owner add(Owner owner) throws DataAccessException {
        LOGGER.trace("Data Access Layer: add(Owner owner)");
        LOGGER.debug("Data Access Layer: entering method add with parameters: owner={}", owner);

        if (owner.getName() == null || owner.getId() != null)
            throw new IllegalArgumentException("Field name must not be null and id can't be set manually");

        if (owner.getCreatedAt() != null || owner.getUpdatedAt() != null)
            throw new IllegalArgumentException("fields createdAt and updatedAt can't be set manually");


        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());

        final String sql = "INSERT INTO " + TABLE_NAME +
            " (name, created_at, updated_at)" +
            " VALUES(?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int ra = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, owner.getName());
            stmt.setObject(2, owner.getCreatedAt());
            stmt.setObject(3, owner.getUpdatedAt());
            LOGGER.debug("Data Access Layer: add() returning jdbc statement ");
            return stmt;
        }, keyHolder);

        owner.setId(((Number) Objects.requireNonNull(keyHolder.getKeys()).get("id")).longValue());

        LOGGER.debug("Data Access Layer: add() returning owner=" + owner);
        return owner;
    }


    private Owner mapRow(ResultSet resultSet, int i) throws SQLException {
        final Owner owner = new Owner();
        owner.setId(resultSet.getLong("id"));
        owner.setName(resultSet.getString("name"));
        owner.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        owner.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        return owner;
    }

}
