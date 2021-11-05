package pl.devcezz.animalshelter.notification;

import pl.devcezz.cqrs.event.EventHandler;
import pl.devcezz.animalshelter.shelter.event.AnimalEvent.AcceptingAnimalFailed;
import pl.devcezz.animalshelter.shelter.event.AnimalEvent.AcceptingAnimalWarned;
import pl.devcezz.animalshelter.shelter.event.AnimalEvent.AcceptingAnimalSucceeded;

class HandleAcceptanceFailure implements EventHandler<AcceptingAnimalFailed> {

    @Override
    public void handle(final AcceptingAnimalFailed event) {
        System.out.println(event.reason());
    }
}

class HandleAcceptanceWarning implements EventHandler<AcceptingAnimalWarned> {

    @Override
    public void handle(final AcceptingAnimalWarned event) {
        System.out.println("Warned");
    }
}

class HandleAcceptanceSuccess implements EventHandler<AcceptingAnimalSucceeded> {

    @Override
    public void handle(final AcceptingAnimalSucceeded event) {
        System.out.println("Succeed");
    }
}