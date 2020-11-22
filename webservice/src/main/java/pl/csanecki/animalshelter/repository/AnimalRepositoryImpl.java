package pl.csanecki.animalshelter.repository;

import io.vavr.control.Option;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import pl.csanecki.animalshelter.dto.AnimalCreated;
import pl.csanecki.animalshelter.dto.AnimalDetails;
import pl.csanecki.animalshelter.dto.AnimalRequest;
import pl.csanecki.animalshelter.service.AnimalRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AnimalRepositoryImpl implements AnimalRepository {

    private final JdbcTemplate jdbcTemplate;

    public AnimalRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AnimalCreated save(AnimalRequest animal) {
        jdbcTemplate.update(
                "INSERT INTO animals(name, kind, age) VALUES(?, ?, ?)",
                animal.name, animal.kind, animal.age
        );

        return null;
    }

    @Override
    public Option<AnimalDetails> findAnimalBy(int id) {
        return null;
    }

    private static final class AnimalDetailsMapper implements RowMapper<AnimalDetails> {

        public AnimalDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new AnimalDetails(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("kind"),
                    rs.getInt("age"),
                    rs.getString("admittedAt"),
                    rs.getString("adpotedAt")
            );
        }
    }
}
