package pl.devcezz.shelter.catalogue

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static pl.devcezz.shelter.catalogue.AnimalFixture.NON_PRESENT_ANIMAL_ID
import static pl.devcezz.shelter.catalogue.AnimalFixture.dog

@SpringBootTest(classes = CatalogueTestContext.class)
class CatalogueDatabaseIT extends Specification {

    @Autowired
    CatalogueDatabase catalogueDatabase

    def 'should be able to save and load new animal'() {
        given:
            Animal animal = dog()
        when:
            catalogueDatabase.saveNew(animal)
        and:
            Option<Animal> dog = catalogueDatabase.findBy(animal.getAnimalId())
        then:
            dog.isDefined()
            dog.get() == animal
    }

    def 'should not load not present animal'() {
        when:
            Option<Animal> ddd = catalogueDatabase.findBy(NON_PRESENT_ANIMAL_ID)
        then:
            ddd.isEmpty()
    }

}
