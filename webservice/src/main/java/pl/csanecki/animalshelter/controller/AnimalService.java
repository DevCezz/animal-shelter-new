package pl.csanecki.animalshelter.controller;

import pl.csanecki.animalshelter.dto.AnimalCreated;
import pl.csanecki.animalshelter.dto.AnimalDetails;
import pl.csanecki.animalshelter.dto.AnimalRequest;

public interface AnimalService {
    AnimalCreated accept(AnimalRequest animal);

    AnimalDetails getAnimalBy(int id);
}
