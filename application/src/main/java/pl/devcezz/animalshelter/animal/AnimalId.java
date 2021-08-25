package pl.devcezz.animalshelter.animal;

import java.util.UUID;

public record AnimalId(UUID value) {

    public AnimalId {
        if (value == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
    }
}
