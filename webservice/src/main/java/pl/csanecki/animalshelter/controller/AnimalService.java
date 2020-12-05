package pl.csanecki.animalshelter.controller;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.csanecki.animalshelter.dto.AnimalDetails;
import pl.csanecki.animalshelter.dto.AnimalRequest;

public interface AnimalService {
    Option<AnimalDetails> accept(AnimalRequest animal);

    Option<AnimalDetails> getAnimalBy(int id);

    List<AnimalDetails> getAllAnimals();
}
