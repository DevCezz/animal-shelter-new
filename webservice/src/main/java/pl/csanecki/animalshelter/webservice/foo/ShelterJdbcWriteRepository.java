package pl.csanecki.animalshelter.webservice.foo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import pl.csanecki.animalshelter.webservice.foo.add.AddAnimalCommand;
import pl.csanecki.animalshelter.webservice.repository.DatabaseRuntimeError;

public class ShelterJdbcWriteRepository implements ShelterWriteRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShelterJdbcWriteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void registerAnimal(AddAnimalCommand command) {
        int rowAffected = jdbcTemplate.update(
                "INSERT INTO animals(name, kind, age) VALUES(?, ?, ?)",
                command.name, command.kind, command.age
        );

        if (rowAffected == 0) {
            throw new DatabaseRuntimeError("Cannot get id for admitted animal");
        }
    }

    @Override
    public void updateAdoptedAtToNow(long animalId) {
        int rowAffected = jdbcTemplate.update("UPDATE animals SET adoptedAt = NOW() WHERE id = ? AND adoptedAt IS NULL", animalId);

        if (rowAffected == 0) {
            throw new DatabaseRuntimeError("Someone has updated adopted at for animal in the meantime, animal: " + animalId);
        }
    }
}