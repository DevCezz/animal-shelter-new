package pl.devcezz.animalshelter.animal;

import io.vavr.collection.Set;
import pl.devcezz.cqrs.event.Event;

import static pl.devcezz.animalshelter.animal.AnimalEvent.AcceptingAnimalFailed.acceptingAnimalRejected;
import static pl.devcezz.animalshelter.animal.AnimalEvent.AcceptingAnimalSucceeded.acceptingAnimalSucceeded;
import static pl.devcezz.animalshelter.animal.AnimalEvent.AcceptingAnimalWarned.acceptingAnimalWarned;

class Shelter {

    private final ShelterLimits shelterLimits;
    private final Set<ShelterAnimal> shelterAnimals;

    Shelter(
            final ShelterLimits shelterLimits,
            final Set<ShelterAnimal> shelterAnimals
    ) {
        this.shelterLimits = shelterLimits;
        this.shelterAnimals = shelterAnimals;
    }

    Event accept(Animal animal) {
        if (safeThresholdExceededAfterAccepting(animal)) {
            if (capacityReachedAfterAccepting(animal)) {
                return acceptingAnimalRejected("Capacity of shelter is exceeded");
            }
            return acceptingAnimalWarned();
        }
        return acceptingAnimalSucceeded();
    }

    private boolean capacityReachedAfterAccepting(Animal animal) {
        return shelterLimits.capacity() < shelterAnimals.length() + 1;
    }

    private boolean safeThresholdExceededAfterAccepting(Animal animal) {
        return shelterLimits.safeThreshold() <= shelterAnimals.length() + 1;
    }
}