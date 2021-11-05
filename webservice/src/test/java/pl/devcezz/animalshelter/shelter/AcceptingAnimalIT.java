package pl.devcezz.animalshelter.shelter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pl.devcezz.animalshelter.shelter.event.AnimalEvent.AcceptingAnimalFailed;
import pl.devcezz.animalshelter.shelter.event.AnimalEvent.AcceptingAnimalSucceeded;
import pl.devcezz.animalshelter.shelter.event.AnimalEvent.AcceptingAnimalWarned;
import pl.devcezz.animalshelter.shelter.exception.AcceptingAnimalRejectedException;
import pl.devcezz.cqrs.event.EventsBus;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static pl.devcezz.animalshelter.shelter.AnimalFixture.acceptAnimalCommand;
import static pl.devcezz.animalshelter.shelter.AnimalFixture.animal;
import static pl.devcezz.animalshelter.shelter.AnimalFixture.anyAnimalId;

@SpringBootTest(classes = { ShelterConfig.class, AcceptingAnimalIT.Config.class })
@ActiveProfiles("container")
@Testcontainers
class AcceptingAnimalIT {

    private final static EventsBus EVENTS_BUS = mock(EventsBus.class);

    private final AnimalId animalId = anyAnimalId();

    @Container
    private static final MySQLContainer<?> DB_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.24"))
            .withUsername("test")
            .withPassword("test");

    static {
        DB_CONTAINER.start();
        System.setProperty("DB_PORT", String.valueOf(DB_CONTAINER.getFirstMappedPort()));
    }

    @BeforeEach
    void resetMock() {
        Mockito.reset(EVENTS_BUS);
    }

    @Test
    @Transactional
    @DisplayName("Should accept animal into shelter without reaching safe threshold")
    void should_accept_animal_into_shelter_without_reaching_safe_threshold(
            @Autowired AcceptingAnimal acceptingAnimal,
            @Autowired EventsBus eventsBus,
            @Autowired ShelterDatabaseRepository repository
    ) {
        acceptingAnimal.handle(acceptAnimalCommand(animalId.value()));

        verify(eventsBus).publish(isA(AcceptingAnimalSucceeded.class));
        assertThat(repository.queryForAvailableAnimals())
                .hasSize(1)
                .allSatisfy(animal -> assertThat(animal.animalId()).isEqualTo(animalId));
    }

    @Test
    @Transactional
    @DisplayName("Should accept animal into shelter reaching safe threshold")
    void should_accept_animal_into_shelter_reaching_safe_threshold(
            @Autowired AcceptingAnimal acceptingAnimal,
            @Autowired EventsBus eventsBus,
            @Autowired ShelterDatabaseRepository repository
    ) {
        addAnimalToShelter(repository);

        acceptingAnimal.handle(acceptAnimalCommand(animalId.value()));

        verify(eventsBus).publish(isA(AcceptingAnimalWarned.class));
        assertThat(repository.queryForAvailableAnimals())
                .hasSize(2)
                .anySatisfy(animal -> assertThat(animal.animalId()).isEqualTo(animalId));
    }

    @Test
    @Transactional
    @DisplayName("Should not accept animal into shelter because of reaching limit")
    void should_not_accept_animal_into_shelter_because_of_reaching_limit(
            @Autowired AcceptingAnimal acceptingAnimal,
            @Autowired EventsBus eventsBus,
            @Autowired ShelterDatabaseRepository repository
    ) {
        addAnimalToShelter(repository);
        addAnimalToShelter(repository);

        assertThatThrownBy(() -> acceptingAnimal.handle(acceptAnimalCommand(animalId.value())))
                .isInstanceOf(AcceptingAnimalRejectedException.class);

        verify(eventsBus).publish(isA(AcceptingAnimalFailed.class));
        assertThat(repository.queryForAvailableAnimals())
                .hasSize(2)
                .noneSatisfy(animal -> assertThat(animal.animalId()).isEqualTo(animalId));
    }

    private void addAnimalToShelter(ShelterDatabaseRepository repository) {
        repository.save(animal());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class Config extends ShelterDatabaseConfig {

        @Bean
        EventsBus eventsBus() {
            return EVENTS_BUS;
        }

        @Bean
        DataSource dataSource() {
            return DataSourceBuilder.create()
                    .url(DB_CONTAINER.getJdbcUrl())
                    .username(DB_CONTAINER.getUsername())
                    .password(DB_CONTAINER.getPassword())
                    .build();
        }
    }
}