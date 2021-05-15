package pl.devcezz.cqrs.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.devcezz.cqrs.exception.NoHandlerForEventException;
import pl.devcezz.cqrs.exception.NotImplementedEventHandlerInterfaceException;
import pl.devcezz.cqrs.exception.NotImplementedEventInterfaceException;
import pl.devcezz.tests.TestFiles;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutoEventsBusTest {

    @TempDir
    Path tempDir;

    @Test
    void should_properly_set_events_bus() {
        FirstMailEventHandler firstMailEventHandler = new FirstMailEventHandler();
        SecondMailEventHandler secondMailEventHandler = new SecondMailEventHandler();
        AutoEventsBus autoEventsBus = new AutoEventsBus(
                Set.of(firstMailEventHandler, secondMailEventHandler)
        );

        var handlers = autoEventsBus.getHandlers();

        assertThat(handlers.get(MailEvent.class)).containsExactlyInAnyOrder(firstMailEventHandler, secondMailEventHandler);
        assertThat(handlers.get(ChatEvent.class)).isNull();
    }

    @Test
    void should_event_handler_handle_event() {
        Path path = TestFiles.createTestFileInDir("auto_events_bus_test_%s" + System.currentTimeMillis(), tempDir);
        AutoEventsBus autoEventsBus = new AutoEventsBus(
                Set.of(
                        new FirstMailEventHandler(path, "Event handled - first"),
                        new SecondMailEventHandler(path, "Event handled - second")
                )
        );

        autoEventsBus.publish(new MailEvent());

        assertThat(TestFiles.readFileLines(path)).containsExactlyInAnyOrder("Event handled - first", "Event handled - second");
    }

    @Test
    void should_fail_when_no_event_handler_for_event() {
        AutoEventsBus autoEventsBus = new AutoEventsBus(
                Set.of(new FirstMailEventHandler())
        );

        assertThatThrownBy(() -> autoEventsBus.publish(new ChatEvent()))
                .isInstanceOf(NoHandlerForEventException.class);
    }

    @Test
    void should_fail_when_set_event_handler_without_generic() {
        assertThatThrownBy(() -> new AutoEventsBus(Set.of(new EventHandlerWithoutGeneric())))
                .isInstanceOf(NotImplementedEventHandlerInterfaceException.class);
    }

    @Test
    void should_fail_when_set_event_handler_without_generic_event_implementation() {
        assertThatThrownBy(() -> new AutoEventsBus(Set.of(new EventHandlerForEventInterface())))
                .isInstanceOf(NotImplementedEventInterfaceException.class);
    }
}

class MailEvent implements Event {}
class ChatEvent implements Event {}

class FirstMailEventHandler implements EventHandler<MailEvent>, Serializable {

    private Path path;
    private String message;

    FirstMailEventHandler() {
    }

    FirstMailEventHandler(final Path path, final String message) {
        this.path = path;
        this.message = message;
    }

    @Override
    public void handle(final MailEvent event) {
        Optional.ofNullable(path)
                .ifPresent(path -> TestFiles.writeMessageToFile(path, message));
    }
}
class SecondMailEventHandler implements EventHandler<MailEvent> {

    private Path path;
    private String message;

    SecondMailEventHandler() {
    }

    SecondMailEventHandler(final Path path, final String message) {
        this.path = path;
        this.message = message;
    }

    @Override
    public void handle(final MailEvent event) {
        Optional.ofNullable(path)
                .ifPresent(path -> TestFiles.writeMessageToFile(path, message));
    }
}
class EventHandlerWithoutGeneric implements EventHandler {
    @Override
    public void handle(final Event event) {

    }
}
class EventHandlerForEventInterface implements EventHandler<Event> {
    @Override
    public void handle(final Event event) {

    }
}