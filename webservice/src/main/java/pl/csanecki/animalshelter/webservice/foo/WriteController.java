package pl.csanecki.animalshelter.webservice.foo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.csanecki.animalshelter.webservice.web.addanimal.AddAnimalCommand;
import pl.csanecki.animalshelter.webservice.web.adoptanimal.AdoptAnimalCommand;
import pl.devcezz.cqrs.command.CommandsBus;

import java.util.UUID;

@RestController
@RequestMapping("/shelter/animals")
class WriteController {

    private final CommandsBus commandsBus;

    @Autowired
    WriteController(CommandsBus commandsBus) {
        this.commandsBus = commandsBus;
    }

    @PostMapping
    ResponseEntity<Void> acceptIntoShelter(@RequestBody AddAnimalCommand command) {
        commandsBus.send(command);

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(command.getUuid())
                        .toUri()
        ).build();
    }

    @PostMapping("/{uuid}/adopt")
    ResponseEntity<Void> adoptAnimal(@PathVariable String uuid) {
        commandsBus.send(new AdoptAnimalCommand(uuid));

        return ResponseEntity.ok().build();
    }
}

class AddAnimalRequest {

    final UUID id;
    final String name;
    final String kind;
    final int age;

    AddAnimalRequest(final String name, final String kind, final int age) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.kind = kind;
        this.age = age;
    }
}